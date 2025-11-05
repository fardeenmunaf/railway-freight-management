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

    private String allocationMode = "STRICT";

    InventoryBalanceController inventoryBalanceController = new InventoryBalanceController();
    OrderController orderController = new OrderController();
    OrderLineController orderLineController = new OrderLineController();
    CartController cartController = new CartController();
    PickWaveController pickWaveController = new PickWaveController();
    PickTaskController pickTaskController = new PickTaskController();

    /**
     * Sets the allocation mode to be used for planning.
     * @param mode "STRICT" or "PARTIAL"
     */
    public void setAllocationMode(String mode) {
        if ("PARTIAL".equalsIgnoreCase(mode) || "STRICT".equalsIgnoreCase(mode)) {
            this.allocationMode = mode.toUpperCase();
        }
    }

    /**
     * Gets the current allocation mode.
     */
    public String getAllocationMode() {
        return allocationMode;
    }

    // Shared allocation result to prevent duplicate allocations
    private static Allocation.AllocationResult sharedAllocationResult = null;

    public PickingPlan() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Resets the shared allocation result - call this at the start of new planning cycle
     */
    public static void resetSharedAllocation() {
        sharedAllocationResult = null;
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
     * @param cart      the cart to check capacity
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
     * @param cart      the cart to use for transport
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
     * Calculates the number of trips needed for a cart to transport a specific quantity of items.
     * Overloaded version that accepts a custom quantity instead of using the full OrderLine quantity.
     *
     * @param cart      the cart to use
     * @param orderLine the order line containing item information
     * @param quantity  the specific quantity to transport
     * @return the number of trips needed, or -1 if impossible
     */
    public int calculateTripsNeeded(Cart cart, OrderLine orderLine, double quantity) {
        if (cart == null || orderLine == null || quantity <= 0) {
            return -1;
        }

        int cartCapacity = calculateCartCapacity(cart, orderLine);

        // If cart cannot carry any items, it's impossible
        if (cartCapacity == 0) {
            return -1;
        }

        // Calculate trips needed (round up to ensure all items are transported)
        return (int) Math.ceil(quantity / cartCapacity);
    }

    /**
     * Plans allocations by invoking the Allocation.
     *
     * @param mode allocation mode: "STRICT" (default) or "PARTIAL"
     * @return AllocationResult with eligibilities and allocation rows
     */
    public Allocation.AllocationResult planAllocations(String mode) {
        // Use shared allocation result if available to prevent duplicate allocations
        if (sharedAllocationResult == null) {
            String effectiveMode = (mode != null && !mode.isBlank()) ? mode : this.allocationMode;
            Allocation allocator = new Allocation();
            List<Order> orders = getAllOrders();
            List<InventoryBalance> balances = getAllItemBalences();
            sharedAllocationResult = allocator.allocateOrders(orders, balances, effectiveMode);
        }

        return sharedAllocationResult;
    }

    /**
     * Gets detailed location information for items allocated to order lines.
     * Shows which bays contain the items needed for each order line and their quantities.
     *
     * @param sku     the SKU to search for (optional, null returns all)
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
     * Inner class to hold the result of picking task planning.
     * Contains both the list of created PickTasks and PickWaves.
     */
    public static class PickingPlanResult {
        private final List<PickTask> pickTasks;
        private final List<PickWave> pickWaves;

        public PickingPlanResult(List<PickTask> pickTasks, List<PickWave> pickWaves) {
            this.pickTasks = pickTasks;
            this.pickWaves = pickWaves;
        }

        public List<PickTask> getPickTasks() {
            return pickTasks;
        }

        public List<PickWave> getPickWaves() {
            return pickWaves;
        }
    }

    /**
     * Plans picking tasks with pick waves for all eligible order lines.
     * <p>
     * The planning process:
     * 1. Creates ONE PickTask per OrderLine (that is ELIGIBLE or PARTIAL)
     * 2. Each PickTask contains multiple PickWaves
     * 3. Each PickWave represents ONE trip the cart needs to make
     * 4. The number of waves depends on cart capacity and total quantity needed
     * 5. If items are spread across multiple bays, creates waves for each location
     *
     * @return PickingPlanResult containing lists of all created PickTasks and PickWaves
     */
    public PickingPlanResult planPickingTask() {
        System.out.println("\n=== Planning Picking Tasks ===");

        // Get all necessary data
        List<OrderLine> orderLines = getAllOrderLines();
        List<Cart> carts = getAllCarts();

        // Run allocation to get eligibilities and locations
        Allocation.AllocationResult allocationResult = planAllocations(this.allocationMode);
        List<Allocation.OrderEligibility> eligibilities = allocationResult.getEligibilities();
        List<Allocation.AllocationRecord> allocations = allocationResult.getAllocations();

        // Process all order lines
        return planPickingTaskInternal(orderLines, carts, eligibilities, allocations, null);
    }

    /**
     * Plans picking tasks with pick waves for a specific order only.
     *
     * @param orderId the ID of the order to plan picking tasks for
     * @return PickingPlanResult containing lists of created PickTasks and PickWaves for this order
     */
    public PickingPlanResult planPickingTaskForOrder(Integer orderId) {
        System.out.println("\n=== Planning Picking Tasks for Order " + orderId + " ===");

        // Get all necessary data
        List<OrderLine> allOrderLines = getAllOrderLines();
        List<Cart> carts = getAllCarts();

        // Filter order lines for this specific order
        List<OrderLine> orderLinesForOrder = allOrderLines.stream()
                .filter(ol -> ol.getOrder().getOrderId().equals(orderId))
                .collect(java.util.stream.Collectors.toList());

        if (orderLinesForOrder.isEmpty()) {
            System.out.println("No order lines found for Order " + orderId);
            return new PickingPlanResult(new ArrayList<>(), new ArrayList<>());
        }

        // Run allocation to get eligibilities and locations
        Allocation.AllocationResult allocationResult = planAllocations(this.allocationMode);
        List<Allocation.OrderEligibility> eligibilities = allocationResult.getEligibilities();
        List<Allocation.AllocationRecord> allocations = allocationResult.getAllocations();

        // Process only the order lines for this order
        return planPickingTaskInternal(orderLinesForOrder, carts, eligibilities, allocations, orderId);
    }

    /**
     * Internal method to plan picking tasks for a given set of order lines.
     *
     * @param orderLines      the order lines to process
     * @param carts           available carts
     * @param eligibilities   order eligibilities
     * @param allocations     allocation records
     * @param specificOrderId if not null, only processes this order ID
     * @return PickingPlanResult containing lists of created PickTasks and PickWaves
     */
    private PickingPlanResult planPickingTaskInternal(
            List<OrderLine> orderLines,
            List<Cart> carts,
            List<Allocation.OrderEligibility> eligibilities,
            List<Allocation.AllocationRecord> allocations,
            Integer specificOrderId) {

        // Track waves created in THIS execution only
        List<PickWave> createdWaves = new ArrayList<>();

        // Run allocation to get eligibilities and locations
        List<Allocation.OrderEligibility> eligibilitiesForTask = eligibilities;
        List<Allocation.AllocationRecord> allocationsForTask = allocations;

        // Process each order line (ONE PickTask per OrderLine)
        for (OrderLine orderLine : orderLines) {
            Integer skuId = orderLine.getSku().getItemId();

            // Check if this order line is eligible (ELIGIBLE or PARTIAL)
            Allocation.OrderEligibility eligibility = findEligibility(eligibilitiesForTask, orderLine);

            if (eligibility == null ||
                    (!eligibility.getStatus().equals("ELIGIBLE") && !eligibility.getStatus().equals("PARTIAL"))) {
                continue;
            }

            System.out.println("\n>>> Processing OrderLine " + orderLine.getOrderLineId() +
                " (Order " + orderLine.getOrder().getOrderId() + ", SKU " + skuId +
                ", Qty: " + orderLine.getQty() + ") <<<");

            // Get all bay locations for this order line
            List<Allocation.AllocationRecord> itemLocations = getItemLocationsForOrderLine(
                    allocationsForTask, orderLine.getOrder().getOrderId(), skuId);

            if (itemLocations.isEmpty()) {
                System.out.println("  No inventory locations found - skipping OrderLine " +
                    orderLine.getOrderLineId() + " (Order " + orderLine.getOrder().getOrderId() + ")");
                continue;
            }

            // Group locations by bay to avoid duplicates and sum quantities
            List<Allocation.AllocationRecord> groupedLocations = groupLocationsByBay(itemLocations);

            // Show bay locations for this order line
            if (groupedLocations.size() > 1) {
                System.out.println("🔄 MULTI-BAY ORDER - OrderLine " + orderLine.getOrderLineId() +
                    " (Order " + orderLine.getOrder().getOrderId() + ") spans " + groupedLocations.size() + " bays:");
                for (Allocation.AllocationRecord location : groupedLocations) {
                    System.out.println("   → Bay " + location.getBay() + ": " + location.getQty() + " units");
                }
            } else if (groupedLocations.size() == 1) {
                System.out.println("📍 Single bay - Bay " + groupedLocations.get(0).getBay() +
                    ": " + groupedLocations.get(0).getQty() + " units");
            }

            // Find the MOST SUITABLE cart for this order line (not round-robin)
            Cart bestCart = findBestCart(carts, orderLine);

            if (bestCart == null) {
                System.out.println("  ERROR: No suitable cart found for OrderLine " + orderLine.getOrderLineId());
                continue;
            }

            int cartCapacity = calculateCartCapacity(bestCart, orderLine);

            System.out.println("  Selected Cart " + bestCart.getCartId() +
                    " (capacity: " + cartCapacity + " items per trip)" +
                    (cartCapacity >= 5 ? " ✓" : " - LOW CAPACITY WARNING"));

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

                // Use the best cart selected for this order line

                // Calculate how much we still need and how much is available at this location
                double remainingNeeded = qtyNeeded - qtyCollected;
                double qtyAtLocation = location.getQty();
                double qtyToCollectHere = Math.min(remainingNeeded, qtyAtLocation);

                // Use calculateTripsNeeded method instead of manual calculation
                int tripsNeeded = calculateTripsNeeded(bestCart, orderLine, qtyToCollectHere);

                if (tripsNeeded < 0) {
                    System.out.println("  ERROR: Cannot calculate trips for Cart " + bestCart.getCartId());
                    continue;
                }

                System.out.println("  Location: Aisle " + location.getAisle() +
                        ", Bay " + location.getBay() +
                        " - Available: " + qtyAtLocation +
                        " - To collect: " + qtyToCollectHere +
                        " - Trips needed: " + tripsNeeded);

                // Create ONE WAVE for EACH TRIP needed at this location
                double qtyCollectedFromLocation = 0.0;
                for (int trip = 0; trip < tripsNeeded; trip++) {
                    Integer pickWaveId = pickWaveController.getNextPickWaveId();

                    double qtyStillNeededFromLocation = qtyToCollectHere - qtyCollectedFromLocation;
                    double qtyForThisTrip = Math.min(cartCapacity, qtyStillNeededFromLocation);

                    PickWave pickWave = new PickWave(
                            pickWaveId,
                            bestCart,
                            "FIFO", // Default strategy
                            "PLANNED" // Initial status
                    );

                    pickWaveController.createPickWave(pickWave);
                    pickWaves.add(pickWave);
                    createdWaves.add(pickWave); // Track waves created in this execution

                    qtyCollectedFromLocation += qtyForThisTrip;
                    qtyCollected += qtyForThisTrip;

                    System.out.println("    Wave " + pickWaveId +
                            ": Cart " + bestCart.getCartId() +
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

        String orderInfo = specificOrderId != null ? " for Order " + specificOrderId : "";
        System.out.println("\n=== Picking Task Planning Complete" + orderInfo + " ===");
        System.out.println("Total Pick Tasks created: " + tasks.size() + " (one per eligible OrderLine)");
        System.out.println("Total Pick Waves created: " + createdWaves.size() + " (one per trip)");

        // Return both lists through PickingPlanResult
        return new PickingPlanResult(tasks, createdWaves);
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
     * Finds the most suitable cart for an order line.
     * Prioritizes carts that can carry at least 3 items per trip.
     * If no cart can carry 2+ items, returns the one with the highest capacity.
     *
     * @param carts     list of available carts
     * @param orderLine the order line to transport
     * @return the most suitable cart, or null if no suitable cart found
     */
    private Cart findBestCart(List<Cart> carts, OrderLine orderLine) {
        if (carts == null || carts.isEmpty() || orderLine == null) {
            return null;
        }

        Cart bestCart = null;
        int bestCapacity = 0;

        // First, try to find a cart that can carry at least 3 items
        for (Cart cart : carts) {
            int capacity = calculateCartCapacity(cart, orderLine);

            if (capacity >= 2) {
                // If we don't have a best cart yet, or this one is better (but not too large)
                if (bestCart == null ||
                        (capacity < bestCapacity && capacity >= 3) ||
                        (bestCapacity < 3 && capacity >= 3)) {
                    bestCart = cart;
                    bestCapacity = capacity;
                }
                // If current best is also >= 2, prefer smaller cart (more efficient)
                else if (bestCapacity >= 3 && capacity >= 3 && capacity < bestCapacity) {
                    bestCart = cart;
                    bestCapacity = capacity;
                }
            }
        }

        // If no cart can carry 2+ items, find the one with highest capacity
        if (bestCart == null) {
            for (Cart cart : carts) {
                int capacity = calculateCartCapacity(cart, orderLine);
                if (capacity > bestCapacity) {
                    bestCart = cart;
                    bestCapacity = capacity;
                }
            }
        }

        return bestCart;
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
     * summing quantities for duplicate locations while maintaining FEFO order.
     */
    private List<Allocation.AllocationRecord> groupLocationsByBay(List<Allocation.AllocationRecord> locations) {
        java.util.Map<String, Allocation.AllocationRecord> grouped = new java.util.LinkedHashMap<>();



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
