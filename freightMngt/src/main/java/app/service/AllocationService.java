package app.service;

import app.controller.OrderLineController;
import app.model.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for allocating inventory to order lines using FEFO (First Expired, First Out) strategy.
 * Handles both STRICT and PARTIAL allocation modes while ensuring stock consistency.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class AllocationService {

    /**
     * Allocates inventory to orders using FEFO strategy.
     *
     * @param orders            List of orders to process
     * @param inventoryBalances List of available inventory balances
     * @param mode              Allocation mode: "STRICT" or "PARTIAL"
     * @return AllocationResult containing eligibility status and allocations
     */
    public AllocationResult allocateOrders(List<Order> orders, List<InventoryBalance> inventoryBalances, String mode) {

        // Sort orders by priority ASC, dueDate ASC, orderId ASC
        List<Order> sortedOrders = orders.stream()
                .sorted(Comparator
                        .comparing(Order::getPriority, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Order::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Order::getOrderId))
                .collect(Collectors.toList());

        List<OrderEligibility> eligibilities = new ArrayList<>();
        List<AllocationRecord> allocations = new ArrayList<>();

        // Create working copy of inventory to track available quantities (as Double to allow fractional logic)
        Map<Integer, Double> availableStock = createStockMap(inventoryBalances);

        for (Order order : sortedOrders) {
            processOrder(order, inventoryBalances, availableStock, mode, eligibilities, allocations);
        }

        return new AllocationResult(eligibilities, allocations);
    }

    /**
     * Processes a single order by allocating its order lines.
     */
    private void processOrder(Order order, List<InventoryBalance> inventoryBalances,
                              Map<Integer, Double> availableStock, String mode,
                              List<OrderEligibility> eligibilities, List<AllocationRecord> allocations) {

        // Get order lines sorted by orderLineId (which serves as lineNo)
        List<OrderLine> orderLines = getOrderLinesSorted(order);

        for (OrderLine line : orderLines) {
            processOrderLine(line, inventoryBalances, availableStock, mode, eligibilities, allocations);
        }
    }

    /**
     * Processes a single order line using FEFO allocation strategy.
     */
    private void processOrderLine(OrderLine line, List<InventoryBalance> inventoryBalances,
                                  Map<Integer, Double> availableStock, String mode,
                                  List<OrderEligibility> eligibilities, List<AllocationRecord> allocations) {

        Integer skuId = line.getSku().getItemId();
        Double requestedQty = line.getQty() != null ? line.getQty() : 0.0;
        Double remainingQty = requestedQty;
        List<AllocationRecord> lineAllocations = new ArrayList<>();

        // Get all lots for this SKU sorted by FEFO (expDate ASC, nulls last)
        List<InventoryLot> availableLots = getAvailableLotsForSku(skuId, inventoryBalances, availableStock);

        // Allocate from each lot in FEFO order
        for (InventoryLot lot : availableLots) {
            if (remainingQty <= 0.0) break;

            Double availableInLot = availableStock.getOrDefault(lot.getLotId(), 0.0);
            if (availableInLot <= 0.0) continue;

            Double allocateQty = Math.min(remainingQty, availableInLot);

            // Find bay for the lot (may be null)
            WarehouseBay bay = findBayForLot(lot, inventoryBalances);
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

            // Update available stock (never below zero)
            double newAvailable = Math.max(0.0, availableInLot - allocateQty);
            availableStock.put(lot.getLotId(), newAvailable);
        }

        // Determine line status and keep allocations based on mode
        Double allocatedQty = requestedQty - remainingQty;
        String status = determineLineStatus(requestedQty, allocatedQty, mode);

        // Add eligibility record
        eligibilities.add(new OrderEligibility(
                line.getOrder().getOrderId(),
                line.getOrderLineId(),
                skuId,
                requestedQty,
                allocatedQty,
                status
        ));

        // Keep allocations based on mode and status
        if (shouldKeepAllocations(status)) {
            allocations.addAll(lineAllocations);
        } else {
            // Rollback stock changes if not keeping allocations
            rollbackStockChanges(lineAllocations, availableStock);
        }
    }

    /**
     * Creates a map of lot ID to available quantity from inventory balances.
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
     * Gets available lots for a SKU sorted by FEFO (expDate ASC; nulls last) and then by receivedDate and lotId.
     */
    private List<InventoryLot> getAvailableLotsForSku(Integer skuId, List<InventoryBalance> inventoryBalances,
                                                      Map<Integer, Double> availableStock) {

        Comparator<InventoryLot> fefoComparator = Comparator
                .comparing(InventoryLot::getExpDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(InventoryLot::getReceivedDate, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(InventoryLot::getLotId);

        return inventoryBalances.stream()
                .flatMap(balance -> balance.getLots().stream())
                .filter(lot -> lot.getSku() != null && lot.getSku().equals(skuId))
                .filter(lot -> availableStock.getOrDefault(lot.getLotId(), 0.0) > 0.0)
                .sorted(fefoComparator)
                .collect(Collectors.toList());
    }

    /**
     * Finds the warehouse bay where a lot is stored.
     */
    private WarehouseBay findBayForLot(InventoryLot lot, List<InventoryBalance> inventoryBalances) {
        if (lot == null || lot.getLotId() == null) return null;

        for (InventoryBalance balance : inventoryBalances) {
            List<InventoryLot> lots = balance.getLots();
            if (lots == null) continue;
            for (InventoryLot l : lots) {
                if (l != null && l.getLotId() != null && l.getLotId().equals(lot.getLotId())) {
                    return balance.getBay();
                }
            }
        }

        return null;
    }

    /**
     * Determines the status of an order line based on allocated quantity and mode.
     */
    private String determineLineStatus(Double requestedQty, Double allocatedQty, String mode) {
        if (Objects.equals(allocatedQty, requestedQty)) {
            return "ELIGIBLE";
        } else if (allocatedQty > 0.0 && "PARTIAL".equalsIgnoreCase(mode)) {
            return "PARTIAL";
        } else {
            return "UNDISPATCHABLE";
        }
    }

    /**
     * Determines if allocations should be kept based on line status.
     */
    private boolean shouldKeepAllocations(String status) {
        return "ELIGIBLE".equals(status) || "PARTIAL".equals(status);
    }

    /**
     * Rollbacks stock changes for allocations that won't be kept.
     */
    private void rollbackStockChanges(List<AllocationRecord> allocations, Map<Integer, Double> availableStock) {
        for (AllocationRecord allocation : allocations) {
            Integer lotId = allocation.getBoxId();
            Double currentStock = availableStock.getOrDefault(lotId, 0.0);
            availableStock.put(lotId, currentStock + allocation.getQty());
        }
    }

    /**
     * Gets order lines for an order sorted by orderLineId (serving as lineNo).
     */
    private List<OrderLine> getOrderLinesSorted(Order order) {
        if (order == null || order.getOrderId() == null) return Collections.emptyList();
        OrderLineController olController = new OrderLineController();
        List<OrderLine> lines = olController.getOrderLinesByOrderId(order.getOrderId());
        return lines.stream()
                .sorted(Comparator.comparing(OrderLine::getOrderLineId))
                .collect(Collectors.toList());
    }

    // Inner classes for results
    public static class AllocationResult {
        private List<OrderEligibility> eligibilities;
        private List<AllocationRecord> allocations;

        public AllocationResult(List<OrderEligibility> eligibilities, List<AllocationRecord> allocations) {
            this.eligibilities = eligibilities;
            this.allocations = allocations;
        }

        public List<OrderEligibility> getEligibilities() {
            return eligibilities;
        }

        public List<AllocationRecord> getAllocations() {
            return allocations;
        }
    }

    public static class OrderEligibility {
        private Integer orderId;
        private Integer lineNo;
        private Integer sku;
        private Double requestedQty;
        private Double allocatedQty;
        private String status;

        public OrderEligibility(Integer orderId, Integer lineNo, Integer sku, Double requestedQty, Double allocatedQty, String status) {
            this.orderId = orderId;
            this.lineNo = lineNo;
            this.sku = sku;
            this.requestedQty = requestedQty;
            this.allocatedQty = allocatedQty;
            this.status = status;
        }

        // Getters
        public Integer getOrderId() {
            return orderId;
        }

        public Integer getLineNo() {
            return lineNo;
        }

        public Integer getSku() {
            return sku;
        }

        public Double getRequestedQty() {
            return requestedQty;
        }

        public Double getAllocatedQty() {
            return allocatedQty;
        }

        public String getStatus() {
            return status;
        }
    }

    public static class AllocationRecord {
        private Integer orderId;
        private Integer lineNo;
        private Integer sku;
        private Double qty;
        private Integer boxId;
        private String aisle;
        private Integer bay;

        public AllocationRecord(Integer orderId, Integer lineNo, Integer sku, Double qty, Integer boxId, String aisle, Integer bay) {
            this.orderId = orderId;
            this.lineNo = lineNo;
            this.sku = sku;
            this.qty = qty;
            this.boxId = boxId;
            this.aisle = aisle;
            this.bay = bay;
        }

        // Getters
        public Integer getOrderId() {
            return orderId;
        }

        public Integer getLineNo() {
            return lineNo;
        }

        public Integer getSku() {
            return sku;
        }

        public Double getQty() {
            return qty;
        }

        public Integer getBoxId() {
            return boxId;
        }

        public String getAisle() {
            return aisle;
        }

        public Integer getBay() {
            return bay;
        }
    }
}
