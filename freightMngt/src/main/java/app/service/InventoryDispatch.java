package app.service;

import app.controller.InventoryBalanceController;
import app.model.Allocation;
import app.model.InventoryBalance;
import app.model.InventoryLot;
import app.model.WarehouseBay;

import java.util.*;

/**
 * Service class responsible for dispatching inventory from warehouse bays.
 * This service manages the dispatch operation following FEFO/FIFO rules,
 * consuming stock from the front of bay lists and supporting partial dispatch
 * across multiple balances.
 */
public class InventoryDispatch {

    private final InventoryBalanceController balanceController;

    /**
     * Constructs a new InventoryDispatch service.
     * Initializes the required controller for managing inventory balances.
     */
    public InventoryDispatch() {
        this.balanceController = new InventoryBalanceController();
    }

    /**
     * Dispatches a specified quantity of a SKU from warehouse inventory.
     * This method follows FEFO/FIFO rules by consuming lots from the front of each balance's list.
     * If insufficient stock is available, no dispatch occurs and the method returns false.
     *
     * @param sku the SKU identifier to dispatch
     * @param quantityToDispatch the total quantity of items to dispatch
     * @return true if the dispatch was successful, false if insufficient stock
     */
    public boolean dispatchStock(Integer sku, Integer quantityToDispatch) {
        // Get all balances with this SKU that are assigned to bays
        List<InventoryBalance> balancesWithSku = getBalancesWithSku(sku);

        if (balancesWithSku.isEmpty()) {
            return false;
        }

        // Calculate total available stock
        Integer totalAvailable = 0;
        for (InventoryBalance balance : balancesWithSku) {
            totalAvailable += balance.getQty();
        }

        // If insufficient stock, do not dispatch anything
        if (totalAvailable < quantityToDispatch) {
            return false;
        }

        // Proceed with dispatch
        Integer remainingToDispatch = quantityToDispatch;

        for (InventoryBalance balance : balancesWithSku) {
            if (remainingToDispatch <= 0) {
                break;
            }

            List<InventoryLot> lots = new ArrayList<>(balance.getLots());
            List<InventoryLot> lotsToRemove = new ArrayList<>();

            for (InventoryLot lot : lots) {
                if (remainingToDispatch <= 0) {
                    break;
                }

                Integer lotQty = lot.getQty();

                if (lotQty <= remainingToDispatch) {
                    // Consume entire lot
                    lotsToRemove.add(lot);
                    remainingToDispatch -= lotQty;
                } else {
                    // Consume partial lot
                    Integer newQty = lotQty - remainingToDispatch;
                    lot.setQty(newQty);
                    remainingToDispatch = 0;
                }
            }

            // Update balance
            if (!lotsToRemove.isEmpty()) {
                lots.removeAll(lotsToRemove);
                balance.setLots(lots);
            }

            // Recalculate total quantity
            Integer newTotalQty = 0;
            for (InventoryLot lot : balance.getLots()) {
                newTotalQty += lot.getQty();
            }
            balance.setQty(newTotalQty);

            // If balance has no more lots, delete it (bay remains but balance is removed)
            if (balance.getLots().isEmpty()) {
                balanceController.deleteInventoryBalance(balance.getBalanceId());
            } else {
                balanceController.updateInventoryBalance(balance.getBalanceId(), balance);
            }
        }

        return true;
    }

    /**
     * Retrieves all inventory balances that contain the specified SKU and are assigned to bays.
     * The balances are sorted by FEFO/FIFO rules to ensure correct dispatch priority:
     * 1. Balances with lots having expiry dates come first (sorted by earliest expiry - FEFO)
     * 2. Balances with lots without expiry dates come after (sorted by earliest received - FIFO)
     * 3. If dates are equal, sort by balanceId ASC (tie-breaker)
     *
     * @param sku the SKU identifier to search for
     * @return a list of InventoryBalance objects ordered by FEFO/FIFO rules
     */
    private List<InventoryBalance> getBalancesWithSku(Integer sku) {
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        List<InventoryBalance> result = new ArrayList<>();

        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && !balance.getLots().isEmpty()) {
                Integer balanceSku = balance.getLots().get(0).getSku();
                if (sku.equals(balanceSku)) {
                    result.add(balance);
                }
            }
        }

        // Sort by FEFO/FIFO rules (manual bubble sort)
        for (int i = 0; i < result.size() - 1; i++) {
            for (int j = 0; j < result.size() - i - 1; j++) {
                if (shouldSwap(result.get(j), result.get(j + 1))) {
                    // Swap
                    InventoryBalance temp = result.get(j);
                    result.set(j, result.get(j + 1));
                    result.set(j + 1, temp);
                }
            }
        }

        return result;
    }

    /**
     * Determines if two balances should be swapped during sorting.
     * Applies FEFO/FIFO rules based on the first lot in each balance.
     *
     * @param b1 the first balance to compare
     * @param b2 the second balance to compare
     * @return true if b1 should come after b2 (swap needed), false otherwise
     */
    private boolean shouldSwap(InventoryBalance b1, InventoryBalance b2) {
        InventoryLot firstLot1 = b1.getLots().get(0);
        InventoryLot firstLot2 = b2.getLots().get(0);

        boolean b1HasExpiry = firstLot1.getExpDate() != null;
        boolean b2HasExpiry = firstLot2.getExpDate() != null;

        // Both have expiry dates - sort by expiry date ASC (FEFO - earliest first)
        if (b1HasExpiry && b2HasExpiry) {
            int expComparison = firstLot1.getExpDate().compareTo(firstLot2.getExpDate());
            if (expComparison != 0) {
                return expComparison > 0; // Swap if b1 expires later than b2
            }
            // Tie-break by balanceId ASC
            return b1.getBalanceId() > b2.getBalanceId();
        }

        // Balances with expiry date come first (b1 has expiry, b2 doesn't - no swap)
        if (b1HasExpiry) {
            return false; // b1 stays before b2
        }

        // b2 has expiry, b1 doesn't - swap so b2 comes first
        if (b2HasExpiry) {
            return true;
        }

        // Both have no expiry date - sort by receivedDate ASC (FIFO - oldest first)
        int receivedComparison = firstLot1.getReceivedDate().compareTo(firstLot2.getReceivedDate());
        if (receivedComparison != 0) {
            return receivedComparison > 0; // Swap if b1 received later than b2
        }

        // Tie-break by balanceId ASC
        return b1.getBalanceId() > b2.getBalanceId();
    }

    /**
     * USEI04 - Pick Path Sequencing
     *
     * This class implements two strategies for sequencing picking paths in a warehouse:
     * - Strategy A: Deterministic sweep (sort by aisle ascending)
     * - Strategy B: Nearest-neighbour greedy algorithm
     *
     * The warehouse uses a 2D coordinate system where:
     * - Entrance is at (0, 0)
     * - Each bay is identified by (aisle, bay) coordinates
     * - Horizontal spacing between adjacent aisles is 3 units
     *
     * Distance Formula:
     * - Same aisle (a1 == a2): D = |b1 - b2|
     * - Different aisles (a1 != a2): D = |b1| + |a1 - a2| * 3 + |b2|
     *
     * @author G113
     * @version 1.0
     */
    public static class PickPathSequencer {

        /**
         * Represents a bay coordinate in the warehouse
         */
        private static class BayCoordinate {
            int aisle;
            int bay;
            int totalQuantity; // Sum of quantities if duplicate bays exist

            public BayCoordinate(int aisle, int bay, int quantity) {
                this.aisle = aisle;
                this.bay = bay;
                this.totalQuantity = quantity;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                BayCoordinate that = (BayCoordinate) o;
                return aisle == that.aisle && bay == that.bay;
            }

            @Override
            public int hashCode() {
                return Objects.hash(aisle, bay);
            }

            @Override
            public String toString() {
                return "(" + aisle + ", " + bay + ")";
            }
        }

        /**
         * Represents a picking route with path and total distance
         */
        private static class PickingRoute {
            List<BayCoordinate> path;
            double totalDistance;
            String strategyName;

            public PickingRoute(List<BayCoordinate> path, double totalDistance, String strategyName) {
                this.path = path;
                this.totalDistance = totalDistance;
                this.strategyName = strategyName;
            }
        }

        /**
         * Generates and displays picking routes using both Strategy A and Strategy B
         *
         * @param allocations list of allocations from the picking plan (USEI03)
         */
        public void generateAndDisplayRoutes(List<Allocation> allocations) {
            if (allocations == null || allocations.isEmpty()) {
                System.out.println("No allocations provided for pick path sequencing.");
                return;
            }

            // Step 1: Merge duplicate bays (sum quantities)
            List<BayCoordinate> bays = mergeDuplicateBays(allocations);

            if (bays.isEmpty()) {
                System.out.println("No valid bays found for pick path sequencing.");
                return;
            }

            System.out.println("\n========================================");
            System.out.println("USEI04 - PICK PATH SEQUENCING");
            System.out.println("========================================\n");

            System.out.println("Total unique bays to visit: " + bays.size());
            System.out.println("Bays: " + bays);
            System.out.println();

            // Step 2: Execute Strategy A
            PickingRoute routeA = executeStrategyA(bays);
            displayRoute(routeA);

            System.out.println();

            // Step 3: Execute Strategy B
            PickingRoute routeB = executeStrategyB(bays);
            displayRoute(routeB);

            // Step 4: Comparison
            System.out.println("\n========================================");
            System.out.println("COMPARISON");
            System.out.println("========================================");
            System.out.println("Strategy A Total Distance: " + routeA.totalDistance);
            System.out.println("Strategy B Total Distance: " + routeB.totalDistance);

            if (routeA.totalDistance < routeB.totalDistance) {
                System.out.println("Best Strategy: Strategy A (saves " +
                        String.format("%.2f", routeB.totalDistance - routeA.totalDistance) + " units)");
            } else if (routeB.totalDistance < routeA.totalDistance) {
                System.out.println("Best Strategy: Strategy B (saves " +
                        String.format("%.2f", routeA.totalDistance - routeB.totalDistance) + " units)");
            } else {
                System.out.println("Both strategies have equal distance.");
            }
            System.out.println("========================================\n");
        }

        /**
         * Merges duplicate bays by summing their quantities
         * Acceptance Criteria: "Duplicate bays must be merged into a single stop"
         *
         * @param allocations list of allocations
         * @return list of unique bay coordinates with summed quantities
         */
        private List<BayCoordinate> mergeDuplicateBays(List<Allocation> allocations) {
            Map<String, BayCoordinate> bayMap = new HashMap<>();

            for (Allocation allocation : allocations) {
                WarehouseBay warehouseBay = allocation.getWarehouseBay();

                if (warehouseBay == null || warehouseBay.getAisle() == null) {
                    continue; // Skip invalid allocations
                }

                int aisle = warehouseBay.getAisle().getAisleId();
                int bay = warehouseBay.getBayNo();
                int quantity = allocation.getQuantity();

                String key = aisle + "," + bay;

                if (bayMap.containsKey(key)) {
                    // Merge: sum quantities
                    bayMap.get(key).totalQuantity += quantity;
                } else {
                    // New bay
                    bayMap.put(key, new BayCoordinate(aisle, bay, quantity));
                }
            }

            return new ArrayList<>(bayMap.values());
        }

        /**
         * Strategy A: Deterministic Sweep
         * Sort bays by aisle ascending, then calculate distance in that order
         *
         * @param bays list of bay coordinates
         * @return picking route with path and total distance
         */
        private PickingRoute executeStrategyA(List<BayCoordinate> bays) {
            // Make a copy to avoid modifying the original list
            List<BayCoordinate> sortedBays = new ArrayList<>(bays);

            // Sort by aisle ascending (deterministic sweep)
            sortedBays.sort(Comparator.comparingInt(b -> b.aisle));

            // Calculate total distance
            double totalDistance = calculateTotalDistance(sortedBays);

            return new PickingRoute(sortedBays, totalDistance, "Strategy A (Deterministic Sweep)");
        }

        /**
         * Strategy B: Nearest-Neighbour Greedy Algorithm
         * Start from entrance (0,0), always pick the nearest unvisited bay
         *
         * @param bays list of bay coordinates
         * @return picking route with path and total distance
         */
        private PickingRoute executeStrategyB(List<BayCoordinate> bays) {
            List<BayCoordinate> path = new ArrayList<>();
            Set<BayCoordinate> unvisited = new HashSet<>(bays);

            // Start from entrance (0, 0)
            int currentAisle = 0;
            int currentBay = 0;

            while (!unvisited.isEmpty()) {
                // Find nearest neighbour
                BayCoordinate nearest = null;
                double minDistance = Double.MAX_VALUE;

                for (BayCoordinate candidate : unvisited) {
                    double dist = calculateDistance(currentAisle, currentBay,
                            candidate.aisle, candidate.bay);
                    if (dist < minDistance) {
                        minDistance = dist;
                        nearest = candidate;
                    }
                }

                // Visit the nearest bay
                if (nearest != null) {
                    path.add(nearest);
                    unvisited.remove(nearest);
                    currentAisle = nearest.aisle;
                    currentBay = nearest.bay;
                }
            }

            // Calculate total distance
            double totalDistance = calculateTotalDistance(path);

            return new PickingRoute(path, totalDistance, "Strategy B (Nearest-Neighbour)");
        }

        /**
         * Calculates the distance between two coordinates using the warehouse distance formula
         *
         * Distance Rules:
         * - Same aisle (a1 == a2): D = |b1 - b2|
         * - Different aisles (a1 != a2): D = |b1| + |a1 - a2| * 3 + |b2|
         *
         * @param a1 aisle of first coordinate
         * @param b1 bay of first coordinate
         * @param a2 aisle of second coordinate
         * @param b2 bay of second coordinate
         * @return distance between the two coordinates
         */
        private double calculateDistance(int a1, int b1, int a2, int b2) {
            if (a1 == a2) {
                // Same aisle: D = |b1 - b2|
                return Math.abs(b1 - b2);
            } else {
                // Different aisles: D = |b1| + |a1 - a2| * 3 + |b2|
                return Math.abs(b1) + Math.abs(a1 - a2) * 3 + Math.abs(b2);
            }
        }

        /**
         * Calculates the total distance for a complete path starting from entrance (0,0)
         *
         * Formula: D_Total = sum of D(c_i, c_{i+1}) for i = 0 to n-1
         * where c_0 = (0, 0) is the entrance
         *
         * @param path ordered list of bays to visit
         * @return total distance
         */
        private double calculateTotalDistance(List<BayCoordinate> path) {
            if (path.isEmpty()) {
                return 0.0;
            }

            double totalDistance = 0.0;
            int currentAisle = 0;
            int currentBay = 0;

            for (BayCoordinate bay : path) {
                double dist = calculateDistance(currentAisle, currentBay, bay.aisle, bay.bay);
                totalDistance += dist;
                currentAisle = bay.aisle;
                currentBay = bay.bay;
            }

            return totalDistance;
        }

        /**
         * Displays the picking route in a formatted manner
         *
         * @param route the picking route to display
         */
        private void displayRoute(PickingRoute route) {
            System.out.println("========================================");
            System.out.println(route.strategyName);
            System.out.println("========================================");

            if (route.path.isEmpty()) {
                System.out.println("No path to display.");
                return;
            }

            System.out.println("Path sequence:");

            // Display detailed route with distances
            int currentAisle = 0;
            int currentBay = 0;
            double cumulativeDistance = 0.0;

            System.out.printf("%-10s -> %-10s | %-15s | %-20s\n",
                    "From", "To", "Distance", "Cumulative Dist.");
            System.out.println("-".repeat(70));

            for (int i = 0; i < route.path.size(); i++) {
                BayCoordinate bay = route.path.get(i);
                double dist = calculateDistance(currentAisle, currentBay, bay.aisle, bay.bay);
                cumulativeDistance += dist;

                System.out.printf("(%d, %d)    -> (%d, %d)    | %-15.2f | %-20.2f\n",
                        currentAisle, currentBay, bay.aisle, bay.bay, dist, cumulativeDistance);

                currentAisle = bay.aisle;
                currentBay = bay.bay;
            }

            System.out.println("-".repeat(70));
            System.out.printf("Total Distance: %.2f units\n", route.totalDistance);

            // Display simplified path
            System.out.println("\nSimplified Path:");
            System.out.print("(0, 0) -> ");
            for (int i = 0; i < route.path.size(); i++) {
                System.out.print(route.path.get(i));
                if (i < route.path.size() - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.println();
        }
    }
}


