package app.service;

import java.util.ArrayList;
import java.util.List;

import app.controller.CartController;
import app.controller.InventoryBalanceController;
import app.controller.OrderController;
import app.controller.OrderLineController;
import app.controller.PickWaveController;
import app.controller.PickTaskController;
import app.model.*;

public class PickingPlan {
    List<PickTask> tasks;

    InventoryBalanceController inventoryBalanceController = new InventoryBalanceController();
    OrderController orderController = new OrderController();
    OrderLineController orderLineController = new OrderLineController();
    CartController cartController = new CartController();
    PickWaveController pickWaveController = new PickWaveController();
    PickTaskController pickTaskController = new PickTaskController();

    public PickingPlan() {
        this.tasks = new ArrayList<>();
    }

    public List<InventoryBalance> getAllItemBalences() {
        return inventoryBalanceController.getAllInventoryBalances();
    }

    public List<Order> getAllOrders() {
        return orderController.getAllOrders();
    }

    public List<OrderLine> getAllOrderLines() {
        return orderLineController.getAllOrderLines();
    }

    public List<OrderLine> getAllOrderLinesFromOrder(int orderId) {
        return orderLineController.getOrderLinesByOrderId(orderId);
    }

    public List<PickTask> getAllPickTasks() {
        return tasks;
    }

    public List<Cart> getAllCarts() {
        return cartController.getAllCarts();
    }

    /**
     * Calculates how many items from an order line a cart can carry based on weight capacity.
     *
     * @param cart the cart to check capacity
     * @param orderLine the order line containing the item information
     * @return the maximum number of items the cart can carry, or 0 if cart/orderLine is null
     */
    public int calculateCartCapacity(Cart cart, OrderLine orderLine) {
        if (cart == null || orderLine == null || orderLine.getSku() == null) {
            return 0;
        }

        Double maxWeight = cart.getMaxWeight();
        Double unitWeight = orderLine.getSku().getUnitWeight();

        if (maxWeight == null || unitWeight == null || unitWeight <= 0) {
            return 0;
        }

        // Calculate how many items fit based on weight
        double capacity = maxWeight / unitWeight;

        // Use floor to get the actual number of complete items that fit
        int floorCapacity = (int) Math.floor(capacity);

        // If floor is 0 but there's some capacity (e.g., 0.5), allow at least 1 item
        // This handles cases where items are heavier than cart capacity
        if (floorCapacity == 0 && capacity > 0) {
            return 1;
        }

        return floorCapacity;
    }

    /**
     * Calculates how many trips are needed to transport all items from an order line.
     * If the cart cannot carry any items (capacity is 0), returns -1 to indicate impossibility.
     *
     * @param cart the cart to use for transport
     * @param orderLine the order line containing the items to transport
     * @return the number of trips needed, or -1 if impossible to transport
     */
    public int calculateTripsNeeded(Cart cart, OrderLine orderLine) {
        if (cart == null || orderLine == null || orderLine.getQty() == null) {
            return -1;
        }

        int cartCapacity = calculateCartCapacity(cart, orderLine);

        // If cart cannot carry any items, it's impossible
        if (cartCapacity == 0) {
            return -1;
        }

        double totalQuantity = orderLine.getQty();

        // Calculate trips needed (round up to ensure all items are transported)
        return (int) Math.ceil(totalQuantity / cartCapacity);
    }

    /**
     * Plans allocations by invoking the Allocation.
     * @param mode allocation mode: "STRICT" (default) or "PARTIAL"
     * @return AllocationResult with eligibilities and allocation rows
     */
    public Allocation.AllocationResult planAllocations(String mode) {
        if (mode == null || mode.isBlank()) mode = "STRICT";
        Allocation allocator = new Allocation();
        List<Order> orders = getAllOrders();
        List<InventoryBalance> balances = getAllItemBalences();
        return allocator.allocateOrders(orders, balances, mode);
    }

    /**
     * Gets detailed location information for items allocated to order lines.
     * Shows which bays contain the items needed for each order line and their quantities.
     *
     * @param sku the SKU to search for (optional, null returns all)
     * @param orderId the order ID to filter (optional, null returns all)
     * @return list of allocation records with location and quantity details
     */
    public List<Allocation.AllocationRecord> getItemLocations(Integer sku, Integer orderId) {
        // Run allocation to get current state
        Allocation.AllocationResult result = planAllocations("STRICT");

        List<Allocation.AllocationRecord> allocations = result.getAllocations();

        // Filter by SKU if provided
        if (sku != null) {
            allocations = allocations.stream()
                    .filter(a -> sku.equals(a.getSku()))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Filter by order ID if provided
        if (orderId != null) {
            allocations = allocations.stream()
                    .filter(a -> orderId.equals(a.getOrderId()))
                    .collect(java.util.stream.Collectors.toList());
        }

        return allocations;
    }

    /**
     * Gets eligibility status for all order lines, showing which are dispatchable.
     * Returns information about requested vs allocated quantities and status.
     *
     * @return list of order eligibilities with status information
     */
    public List<Allocation.OrderEligibility> getOrderEligibilities() {
        Allocation.AllocationResult result = planAllocations("STRICT");
        return result.getEligibilities();
    }


    /**
     * Plans picking tasks with pick waves for all eligible order lines.
     *
     * The planning process:
     * 1. Creates ONE PickTask per OrderLine (that is ELIGIBLE or PARTIAL)
     * 2. Each PickTask contains multiple PickWaves
     * 3. Each PickWave represents ONE trip the cart needs to make
     * 4. The number of waves depends on cart capacity and total quantity needed
     * 5. If items are spread across multiple bays, creates waves for each location
     */
    public void planPickingTask() {
        System.out.println("\n=== Planning Picking Tasks ===");

        // Get all necessary data
        List<OrderLine> orderLines = getAllOrderLines();
        List<Cart> carts = getAllCarts();

        // Run allocation to get eligibilities and locations
        Allocation.AllocationResult allocationResult = planAllocations("PARTIAL");
        List<Allocation.OrderEligibility> eligibilities = allocationResult.getEligibilities();
        List<Allocation.AllocationRecord> allocations = allocationResult.getAllocations();

        int cartIndex = 0; // Track which cart to use next

        // Process each order line (ONE PickTask per OrderLine)
        for (OrderLine orderLine : orderLines) {
            // Check if this order line is eligible (ELIGIBLE or PARTIAL)
            Allocation.OrderEligibility eligibility = findEligibility(eligibilities, orderLine);

            if (eligibility == null ||
                (!eligibility.getStatus().equals("ELIGIBLE") && !eligibility.getStatus().equals("PARTIAL"))) {
                System.out.println("Skipping OrderLine " + orderLine.getOrderLineId() +
                                 " for Order " + orderLine.getOrder().getOrderId() +
                                 " - Not eligible");
                continue;
            }

            System.out.println("\nProcessing OrderLine " + orderLine.getOrderLineId() +
                             " for Order " + orderLine.getOrder().getOrderId() +
                             " (SKU: " + orderLine.getSku().getSku() + ")");

            // Get all bay locations for this order line
            Integer skuId = orderLine.getSku().getItemId();
            List<Allocation.AllocationRecord> itemLocations = getItemLocationsForOrderLine(
                allocations, orderLine.getOrder().getOrderId(), skuId);

            if (itemLocations.isEmpty()) {
                System.out.println("  No inventory locations found - skipping");
                continue;
            }

            // Group locations by bay to avoid duplicates and sum quantities
            List<Allocation.AllocationRecord> groupedLocations = groupLocationsByBay(itemLocations);

            // Create ONE pick task for this order line
            Integer pickTaskId = pickTaskController.getNextPickTaskId();
            List<PickWave> pickWaves = new ArrayList<>();

            // Get the total quantity needed from the order line
            double qtyNeeded = eligibility.getAllocatedQty();
            double qtyCollected = 0.0;

            // For each bay location, create waves (trips) until we have enough quantity
            for (Allocation.AllocationRecord location : groupedLocations) {
                // Check if we've already collected enough
                if (qtyCollected >= qtyNeeded) {
                    System.out.println("  Quantity target reached (" + qtyCollected + "/" + qtyNeeded + ") - skipping remaining locations");
                    break;
                }

                if (carts.isEmpty()) {
                    System.out.println("  ERROR: No carts available!");
                    break;
                }

                // Get next available cart (round-robin)
                Cart cart = carts.get(cartIndex % carts.size());
                cartIndex++;

                int cartCapacity = calculateCartCapacity(cart, orderLine);

                if (cartCapacity == 0) {
                    System.out.println("  WARNING: Cart " + cart.getCartId() +
                                     " cannot carry items from OrderLine " + orderLine.getOrderLineId());
                    continue;
                }

                // Calculate how much we still need and how much is available at this location
                double remainingNeeded = qtyNeeded - qtyCollected;
                double qtyAtLocation = location.getQty();
                double qtyToCollectHere = Math.min(remainingNeeded, qtyAtLocation);

                int tripsNeeded = (int) Math.ceil(qtyToCollectHere / cartCapacity);

                System.out.println("  Location: Aisle " + location.getAisle() +
                                 ", Bay " + location.getBay() +
                                 " - Available: " + qtyAtLocation +
                                 " - To collect: " + qtyToCollectHere +
                                 " - Trips needed: " + tripsNeeded +
                                 " (Cart capacity: " + cartCapacity + ")");

                // Create ONE WAVE for EACH TRIP needed at this location
                double qtyCollectedFromLocation = 0.0;
                for (int trip = 0; trip < tripsNeeded; trip++) {
                    Integer pickWaveId = pickWaveController.getNextPickWaveId();

                    double qtyStillNeededFromLocation = qtyToCollectHere - qtyCollectedFromLocation;
                    double qtyForThisTrip = Math.min(cartCapacity, qtyStillNeededFromLocation);

                    PickWave pickWave = new PickWave(
                        pickWaveId,
                        cart,
                        "FIFO", // Default strategy
                        "PLANNED" // Initial status
                    );

                    pickWaveController.createPickWave(pickWave);
                    pickWaves.add(pickWave);

                    qtyCollectedFromLocation += qtyForThisTrip;
                    qtyCollected += qtyForThisTrip;

                    System.out.println("    Wave " + pickWaveId +
                                     ": Cart " + cart.getCartId() +
                                     ", Trip " + (trip + 1) + "/" + tripsNeeded +
                                     ", Qty: " + qtyForThisTrip +
                                     " (Total collected: " + qtyCollected + "/" + qtyNeeded + ")");
                }
            }

            // Create the pick task with all waves for this order line
            if (!pickWaves.isEmpty()) {
                PickTask pickTask = new PickTask(
                    pickTaskId,
                    pickWaves,
                    orderLine,
                    eligibility.getRequestedQty().intValue() - eligibility.getAllocatedQty().intValue(), // balance
                    eligibility.getAllocatedQty(),
                    null // positionSequence - can be set later for route optimization
                );

                pickTaskController.createPickTask(pickTask);
                tasks.add(pickTask);

                System.out.println("  PickTask " + pickTaskId +
                                 " created with " + pickWaves.size() + " wave(s) (trips)");
            }
        }

        System.out.println("\n=== Picking Task Planning Complete ===");
        System.out.println("Total Pick Tasks created: " + tasks.size() + " (one per eligible OrderLine)");
        System.out.println("Total Pick Waves created: " + pickWaveController.getAllPickWaves().size() + " (one per trip)");
    }

    /**
     * Finds the eligibility record for a given order line.
     */
    private Allocation.OrderEligibility findEligibility(List<Allocation.OrderEligibility> eligibilities,
                                                        OrderLine orderLine) {
        for (Allocation.OrderEligibility e : eligibilities) {
            if (e.getOrderId().equals(orderLine.getOrder().getOrderId()) &&
                e.getLineNo().equals(orderLine.getOrderLineId())) {
                return e;
            }
        }
        return null;
    }

    /**
     * Gets all allocation records (bay locations) for a specific order line.
     */
    private List<Allocation.AllocationRecord> getItemLocationsForOrderLine(
            List<Allocation.AllocationRecord> allAllocations,
            Integer orderId,
            Integer sku) {
        List<Allocation.AllocationRecord> result = new ArrayList<>();
        for (Allocation.AllocationRecord record : allAllocations) {
            if (record.getOrderId().equals(orderId) && record.getSku().equals(sku)) {
                result.add(record);
            }
        }
        return result;
    }

    /**
     * Groups allocation records by unique bay location (aisle + bay),
     * summing quantities for duplicate locations.
     */
    private List<Allocation.AllocationRecord> groupLocationsByBay(List<Allocation.AllocationRecord> locations) {
        java.util.Map<String, Allocation.AllocationRecord> grouped = new java.util.HashMap<>();

        for (Allocation.AllocationRecord record : locations) {
            String locationKey = record.getAisle() + "-" + record.getBay();

            if (grouped.containsKey(locationKey)) {
                // Sum quantities for the same location
                Allocation.AllocationRecord existing = grouped.get(locationKey);
                Allocation.AllocationRecord merged = new Allocation.AllocationRecord(
                    existing.getOrderId(),
                    existing.getLineNo(),
                    existing.getSku(),
                    existing.getQty() + record.getQty(), // Sum quantities
                    existing.getBoxId(),
                    existing.getAisle(),
                    existing.getBay()
                );
                grouped.put(locationKey, merged);
            } else {
                grouped.put(locationKey, record);
            }
        }

        return new ArrayList<>(grouped.values());
    }
}
