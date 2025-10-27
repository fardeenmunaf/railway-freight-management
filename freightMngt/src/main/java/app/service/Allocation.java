package app.service;

import app.controller.OrderLineController;
import app.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for allocating inventory to order lines using FEFO (First Expired, First Out) strategy.
 * Handles both STRICT and PARTIAL allocation modes while ensuring stock consistency.
 * <p>
 * Note: mode defaults to "STRICT" when null or blank.
 */
public class Allocation {

    private static final double EPS = 1e-9;

    /**
     * Allocates inventory to a list of orders using FEFO.
     *
     * @param orders            list of orders to process
     * @param inventoryBalances list of available inventory balances
     * @param mode              allocation mode: "STRICT" or "PARTIAL" (defaults to "STRICT" when null/blank)
     * @return AllocationResult containing order line eligibilities and allocation records
     */
    public AllocationResult allocateOrders(List<Order> orders, List<InventoryBalance> inventoryBalances, String mode) {
        String effectiveMode = (mode == null || mode.isBlank()) ? "STRICT" : mode;

        Map<Integer, WarehouseBay> lotToBay = indexLotToBay(inventoryBalances);

        List<Order> sortedOrders = orders.stream()
                .sorted(Comparator
                        .comparing(Order::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Order::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Order::getOrderId))
                .collect(Collectors.toList());

        List<OrderEligibility> eligibilities = new ArrayList<>();
        List<AllocationRecord> allocations = new ArrayList<>();

        Map<Integer, Double> availableStock = createStockMap(inventoryBalances);

        for (Order order : sortedOrders) {
            processOrder(order, inventoryBalances, availableStock, effectiveMode, eligibilities, allocations, lotToBay);
        }

        return new AllocationResult(eligibilities, allocations);
    }

    /**
     * Builds an index map from lotId to WarehouseBay for fast lookup.
     *
     * @param balances inventory balances that contain lots and warehouse bay information
     * @return map where key is lotId and value is the corresponding WarehouseBay
     */
    private Map<Integer, WarehouseBay> indexLotToBay(List<InventoryBalance> balances) {
        Map<Integer, WarehouseBay> map = new HashMap<>();
        if (balances == null) return map;

        for (InventoryBalance balance : balances) {
            WarehouseBay bay = balance.getBay();
            List<InventoryLot> lots = balance.getLots();
            if (lots == null) continue;
            for (InventoryLot lot : lots) {
                if (lot != null && lot.getLotId() != null) {
                    map.put(lot.getLotId(), bay);
                }
            }
        }
        return map;
    }

    /**
     * Processes a single order by allocating its order lines.
     *
     * @param order             order to process
     * @param inventoryBalances available inventory balances
     * @param availableStock    mutable map of available quantity per lotId
     * @param mode              allocation mode ("STRICT" or "PARTIAL")
     * @param eligibilities     accumulator list for order line eligibilities
     * @param allocations       accumulator list for final allocation records
     * @param lotToBay          precomputed map of lotId to WarehouseBay
     */
    private void processOrder(Order order, List<InventoryBalance> inventoryBalances,
                              Map<Integer, Double> availableStock, String mode,
                              List<OrderEligibility> eligibilities, List<AllocationRecord> allocations,
                              Map<Integer, WarehouseBay> lotToBay) {

        List<OrderLine> orderLines = getOrderLinesSorted(order);

        for (OrderLine line : orderLines) {
            processOrderLine(line, inventoryBalances, availableStock, mode, eligibilities, allocations, lotToBay);
        }
    }

    /**
     * Processes a single order line using FEFO allocation:
     * iterates available lots, allocates until requested quantity is satisfied,
     * updates available stock and records temporary allocations which are committed or rolled back.
     *
     * @param line              order line to allocate
     * @param inventoryBalances available inventory balances
     * @param availableStock    mutable map of available quantity per lotId
     * @param mode              allocation mode ("STRICT" or "PARTIAL")
     * @param eligibilities     accumulator for order line eligibilities
     * @param allocations       accumulator for final allocation records
     * @param lotToBay          precomputed map of lotId to WarehouseBay
     */
    private void processOrderLine(OrderLine line, List<InventoryBalance> inventoryBalances,
                                  Map<Integer, Double> availableStock, String mode,
                                  List<OrderEligibility> eligibilities, List<AllocationRecord> allocations,
                                  Map<Integer, WarehouseBay> lotToBay) {

        Integer skuId = line.getSku().getItemId();
        Double requestedQty = line.getQty() != null ? line.getQty() : 0.0;
        Double remainingQty = requestedQty;
        List<AllocationRecord> lineAllocations = new ArrayList<>();


        List<InventoryLot> availableLots = getAvailableLotsForSku(skuId, inventoryBalances, availableStock);

        for (InventoryLot lot : availableLots) {
            if (remainingQty <= 0.0) break;

            Double availableInLot = availableStock.getOrDefault(lot.getLotId(), 0.0);
            if (availableInLot <= 0.0) continue;

            Double allocateQty = Math.min(remainingQty, availableInLot);

            WarehouseBay bay = findBayForLot(lot, lotToBay);
            String aisleCode = null;
            Integer bayNo = null;
            if (bay != null) {
                WarehouseAisle aisle = bay.getAisle();
                aisleCode = aisle != null ? aisle.getUniqueCode() : null;
                bayNo = bay.getBayNo();
            }

            AllocationRecord allocation = new AllocationRecord(
                    line.getOrder().getOrderId(),
                    line.getOrderLineId(),
                    skuId,
                    allocateQty,
                    lot.getLotId(),
                    aisleCode,
                    bayNo
            );

            lineAllocations.add(allocation);
            remainingQty -= allocateQty;

            double newAvailable = Math.max(0.0, availableInLot - allocateQty);
            availableStock.put(lot.getLotId(), newAvailable);
        }

        Double allocatedQty = requestedQty - remainingQty;
        String status = determineLineStatus(requestedQty, allocatedQty, mode);

        eligibilities.add(new OrderEligibility(
                line.getOrder().getOrderId(),
                line.getOrderLineId(),
                skuId,
                requestedQty,
                allocatedQty,
                status
        ));

        if (shouldKeepAllocations(status)) {
            allocations.addAll(lineAllocations);
        } else {
            rollbackStockChanges(lineAllocations, availableStock);
        }
    }

    /**
     * Creates a map of lotId to available quantity from inventory balances.
     *
     * @param inventoryBalances inventory balances containing lots
     * @return map where key is lotId and value is available quantity as double
     */
    private Map<Integer, Double> createStockMap(List<InventoryBalance> inventoryBalances) {
        Map<Integer, Double> stockMap = new HashMap<>();

        for (InventoryBalance balance : inventoryBalances) {
            for (InventoryLot lot : balance.getLots()) {
                stockMap.put(lot.getLotId(), lot.getQty() != null ? lot.getQty().doubleValue() : 0.0);
            }
        }

        return stockMap;
    }

    /**
     * Gets available lots for a SKU sorted by FEFO:
     * expiration date ascending (nulls last), then received date, then lotId.
     * Only lots with positive available quantity are returned.
     *
     * @param skuId             SKU identifier
     * @param inventoryBalances inventory balances to search
     * @param availableStock    map of available quantities by lotId
     * @return sorted list of InventoryLot available for allocation
     */
    private List<InventoryLot> getAvailableLotsForSku(Integer skuId, List<InventoryBalance> inventoryBalances,
                                                      Map<Integer, Double> availableStock) {

        // Create lot-to-bay map for sorting by bay proximity
        Map<Integer, WarehouseBay> lotToBayMap = indexLotToBay(inventoryBalances);

        // Create a more explicit FEFO comparator that handles our specific case
        Comparator<InventoryLot> fefoComparator = (lot1, lot2) -> {
            // First: Compare expiration dates (FEFO - First Expired First Out)
            // Lots with expiration dates come BEFORE lots without expiration dates
            if (lot1.getExpDate() != null && lot2.getExpDate() != null) {
                int expDateCompare = lot1.getExpDate().compareTo(lot2.getExpDate());
                if (expDateCompare != 0) {
                    return expDateCompare; // Earlier expiration date wins
                }
            } else if (lot1.getExpDate() != null && lot2.getExpDate() == null) {
                return -1; // Lot with expiration date comes BEFORE lot without
            } else if (lot1.getExpDate() == null && lot2.getExpDate() != null) {
                return 1; // Lot without expiration date comes AFTER lot with
            }

            // Second: Compare received dates (FIFO - First In First Out)
            if (lot1.getReceivedDate() != null && lot2.getReceivedDate() != null) {
                int receivedDateCompare = lot1.getReceivedDate().compareTo(lot2.getReceivedDate());
                if (receivedDateCompare != 0) {
                    return receivedDateCompare; // Earlier received date wins
                }
            } else if (lot1.getReceivedDate() != null && lot2.getReceivedDate() == null) {
                return -1; // Lot with received date comes BEFORE lot without
            } else if (lot1.getReceivedDate() == null && lot2.getReceivedDate() != null) {
                return 1; // Lot without received date comes AFTER lot with
            }

            // Third: Compare bay numbers (closer bays first)
            WarehouseBay bay1 = lotToBayMap.get(lot1.getLotId());
            WarehouseBay bay2 = lotToBayMap.get(lot2.getLotId());

            Integer bayNo1 = bay1 != null ? bay1.getBayNo() : Integer.MAX_VALUE;
            Integer bayNo2 = bay2 != null ? bay2.getBayNo() : Integer.MAX_VALUE;

            int bayCompare = bayNo1.compareTo(bayNo2);
            if (bayCompare != 0) {
                return bayCompare; // Lower bay number wins
            }

            // Fourth: Compare lot IDs (tie-breaker)
            return lot1.getLotId().compareTo(lot2.getLotId());
        };

        List<InventoryLot> sortedLots = inventoryBalances.stream()
                .flatMap(balance -> balance.getLots().stream())
                .filter(lot -> lot.getSku() != null && lot.getSku().equals(skuId))
                .filter(lot -> availableStock.getOrDefault(lot.getLotId(), 0.0) > 0.0)
                .sorted(fefoComparator)
                .collect(Collectors.toList());



        return sortedLots;
    }

    /**
     * Finds the WarehouseBay where a lot is stored using the precomputed map.
     *
     * @param lot      inventory lot
     * @param lotToBay map of lotId to WarehouseBay
     * @return WarehouseBay or null if not found
     */
    private WarehouseBay findBayForLot(InventoryLot lot, Map<Integer, WarehouseBay> lotToBay) {
        if (lot == null || lot.getLotId() == null) return null;
        return lotToBay.get(lot.getLotId());
    }

    /**
     * Determines the status of an order line based on requested and allocated quantities and the mode.
     * Uses EPS tolerance to avoid floating point precision issues.
     *
     * @param requestedQty requested quantity
     * @param allocatedQty allocated quantity
     * @param mode         allocation mode ("STRICT" or "PARTIAL")
     * @return "ELIGIBLE" if fully allocated, "PARTIAL" if partially allocated and mode is PARTIAL, otherwise "UNDISPATCHABLE"
     */
    private String determineLineStatus(Double requestedQty, Double allocatedQty, String mode) {
        double req = requestedQty != null ? requestedQty : 0.0;
        double alloc = allocatedQty != null ? allocatedQty : 0.0;

        if (Math.abs(alloc - req) < EPS) {
            return "ELIGIBLE";
        } else if (alloc > 0.0 && "PARTIAL".equalsIgnoreCase(mode)) {
            return "PARTIAL";
        } else {
            return "UNDISPATCHABLE";
        }
    }

    /**
     * Returns whether allocations should be kept based on the line status.
     *
     * @param status one of "ELIGIBLE", "PARTIAL", "UNDISPATCHABLE"
     * @return true when allocations should be retained
     */
    private boolean shouldKeepAllocations(String status) {
        return "ELIGIBLE".equals(status) || "PARTIAL".equals(status);
    }

    /**
     * Rolls back stock changes for allocations that are not kept by returning quantities to availableStock.
     *
     * @param allocations    list of allocation records to rollback
     * @param availableStock mutable map of available quantities by lotId
     */
    private void rollbackStockChanges(List<AllocationRecord> allocations, Map<Integer, Double> availableStock) {
        for (AllocationRecord allocation : allocations) {
            Integer lotId = allocation.getBoxId();
            Double currentStock = availableStock.getOrDefault(lotId, 0.0);
            availableStock.put(lotId, currentStock + allocation.getQty());
        }
    }

    /**
     * Gets order lines for an order sorted by orderLineId (used as line number).
     * <p>
     * Note: if OrderLine exposes getLineNo(), this method may be adapted to use it; currently it uses getOrderLineId().
     *
     * @param order order from which to retrieve lines
     * @return sorted list of OrderLine or empty list if order is invalid
     */
    private List<OrderLine> getOrderLinesSorted(Order order) {
        if (order == null || order.getOrderId() == null) return Collections.emptyList();
        OrderLineController orderLineController = new OrderLineController();
        List<OrderLine> lines = orderLineController.getOrderLinesByOrderId(order.getOrderId());

        return lines.stream()
                .sorted(Comparator.comparing(OrderLine::getOrderLineId,
                        Comparator.nullsFirst(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * Result of allocation operation containing eligibilities and allocation records.
     */
    public static class AllocationResult {
        private List<OrderEligibility> eligibilities;
        private List<AllocationRecord> allocations;

        /**
         * Constructs an AllocationResult.
         *
         * @param eligibilities list of per-line eligibilities
         * @param allocations   list of allocation records
         */
        public AllocationResult(List<OrderEligibility> eligibilities, List<AllocationRecord> allocations) {
            this.eligibilities = eligibilities;
            this.allocations = allocations;
        }

        /**
         * Returns the list of order line eligibilities.
         *
         * @return list of OrderEligibility
         */
        public List<OrderEligibility> getEligibilities() {
            return eligibilities;
        }

        /**
         * Returns the list of allocation records.
         *
         * @return list of AllocationRecord
         */
        public List<AllocationRecord> getAllocations() {
            return allocations;
        }
    }

    /**
     * Represents the eligibility of an order line after allocation attempt.
     */
    public static class OrderEligibility {
        private Integer orderId;
        private Integer lineNo;
        private Integer sku;
        private Double requestedQty;
        private Double allocatedQty;
        private String status;

        /**
         * Constructs an OrderEligibility.
         *
         * @param orderId      order identifier
         * @param lineNo       line number (orderLineId)
         * @param sku          SKU identifier
         * @param requestedQty requested quantity
         * @param allocatedQty allocated quantity
         * @param status       resulting status ("ELIGIBLE", "PARTIAL", "UNDISPATCHABLE")
         */
        public OrderEligibility(Integer orderId, Integer lineNo, Integer sku, Double requestedQty, Double allocatedQty, String status) {
            this.orderId = orderId;
            this.lineNo = lineNo;
            this.sku = sku;
            this.requestedQty = requestedQty;
            this.allocatedQty = allocatedQty;
            this.status = status;
        }

        /**
         * Returns order id.
         */
        public Integer getOrderId() {
            return orderId;
        }

        /**
         * Returns line number.
         */
        public Integer getLineNo() {
            return lineNo;
        }

        /**
         * Returns SKU id.
         */
        public Integer getSku() {
            return sku;
        }

        /**
         * Returns requested quantity.
         */
        public Double getRequestedQty() {
            return requestedQty;
        }

        /**
         * Returns allocated quantity.
         */
        public Double getAllocatedQty() {
            return allocatedQty;
        }

        /**
         * Returns line status.
         */
        public String getStatus() {
            return status;
        }
    }

    /**
     * Allocation record describing which quantity was taken from which lot for an order line.
     */
    public static class AllocationRecord {
        private Integer orderId;
        private Integer lineNo;
        private Integer sku;
        private Double qty;
        private Integer boxId;
        private String aisle;
        private Integer bay;

        /**
         * Constructs an AllocationRecord.
         *
         * @param orderId order identifier
         * @param lineNo  line number (orderLineId)
         * @param sku     SKU identifier
         * @param qty     allocated quantity
         * @param boxId   lot identifier (lotId)
         * @param aisle   aisle code where the lot is stored
         * @param bay     bay number where the lot is stored
         */
        public AllocationRecord(Integer orderId, Integer lineNo, Integer sku, Double qty, Integer boxId, String aisle, Integer bay) {
            this.orderId = orderId;
            this.lineNo = lineNo;
            this.sku = sku;
            this.qty = qty;
            this.boxId = boxId;
            this.aisle = aisle;
            this.bay = bay;
        }

        /**
         * Returns order id.
         */
        public Integer getOrderId() {
            return orderId;
        }

        /**
         * Returns line number.
         */
        public Integer getLineNo() {
            return lineNo;
        }

        /**
         * Returns SKU id.
         */
        public Integer getSku() {
            return sku;
        }

        /**
         * Returns allocated quantity.
         */
        public Double getQty() {
            return qty;
        }

        /**
         * Returns the lot id (box id).
         */
        public Integer getBoxId() {
            return boxId;
        }

        /**
         * Returns aisle code.
         */
        public String getAisle() {
            return aisle;
        }

        /**
         * Returns bay number.
         */
        public Integer getBay() {
            return bay;
        }
    }
}
