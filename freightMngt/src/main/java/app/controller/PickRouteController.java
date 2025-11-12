package app.controller;

import app.service.Allocation;
import app.service.PickingPlan;
import app.service.PickRouteOptimizationService;
import app.service.PickRouteOptimizationService.*;

import java.util.*;

public class PickRouteController {

    private PickingPlan pickingPlan;
    private PickRouteOptimizationService routeService;

    public PickRouteController() {
        this.pickingPlan = new PickingPlan();
        this.routeService = new PickRouteOptimizationService();
    }

    /**
     * Public method to optimize routes (used by Main for order grouping)
     */
    public Map<String, PickRouteOptimizationService.RouteResult> optimizeRoute(
            List<PickRouteOptimizationService.BayLocation> bays) {
        return routeService.optimizeRoute(bays);
    }


    /**
     * Calculate optimal routes for a specific order
     */
    public Map<String, RouteResult> calculateRouteForOrder(Integer orderId) {
        // Get allocation records from PickingPlan
        List<Allocation.AllocationRecord> allocations =
                pickingPlan.getItemLocations(null, orderId);

        // Convert to BayLocation list (merging duplicates)
        List<BayLocation> bays = convertAndMergeBays(allocations);

        // Run both optimization strategies
        return routeService.optimizeRoute(bays);
    }


    /**
     * Calculate routes for a specific SKU across all orders
     */
    public Map<String, RouteResult> calculateRouteForSKU(Integer sku) {
        List<Allocation.AllocationRecord> allocations =
                pickingPlan.getItemLocations(sku, null);

        List<BayLocation> bays = convertAndMergeBays(allocations);
        return routeService.optimizeRoute(bays);
    }

    /**
     * Calculate routes for all pick tasks in a PickingPlanResult
     */
    /**
     * Calculate routes for all pick tasks in a PickingPlanResult
     */
    public Map<Integer, Map<String, RouteResult>> calculateRoutesForPickTasks(
            PickingPlan.PickingPlanResult planResult) {

        Map<Integer, Map<String, RouteResult>> results = new HashMap<>();
        Map<Integer, Map<String, RouteResult>> skuRouteCache = new HashMap<>();

        for (app.model.PickTask pickTask : planResult.getPickTasks()) {
            Integer sku = pickTask.getOrderLine().getSku().getItemId();

            // Check if we already calculated routes for this SKU
            if (!skuRouteCache.containsKey(sku)) {
                List<Allocation.AllocationRecord> allocations =
                        pickingPlan.getItemLocations(sku, null);

                if (!allocations.isEmpty()) {
                    List<BayLocation> bays = convertAndMergeBays(allocations);
                    Map<String, RouteResult> routes = routeService.optimizeRoute(bays);
                    skuRouteCache.put(sku, routes);
                }
            }

            // Use cached route for this pick task
            if (skuRouteCache.containsKey(sku)) {
                results.put(pickTask.getPickTaskId(), skuRouteCache.get(sku));
            }
        }

        return results;
    }

    /**
     * Helper: Convert AllocationRecords to BayLocations and merge duplicates
     */
    private List<PickRouteOptimizationService.BayLocation> convertAndMergeBays(
            List<Allocation.AllocationRecord> allocations) {

        Map<String, Double> bayQuantities = new HashMap<>();

        for (Allocation.AllocationRecord record : allocations) {
            // bay() já retorna Integer diretamente (ex: 12, 15)
            String aisle = record.getAisle();
            Integer bayNumber = record.getBay();
            Double quantity = record.getQty();

            // Assumindo que todos são da mesma aisle ou você tem como obter
            // Se precisar da aisle, adicione um método record.aisle()
            String key = aisle.split("-")[1].replace("A", "") + "-" + bayNumber; // ou use aisle correto
            bayQuantities.merge(key, quantity, Double::sum);
        }

        List<PickRouteOptimizationService.BayLocation> bays = new ArrayList<>();
        for (Map.Entry<String, Double> entry : bayQuantities.entrySet()) {
            String[] parts = entry.getKey().split("-");
            String aisle = parts[0];           // "1"
            int bay = Integer.parseInt(parts[1]); // 12

            bays.add(new PickRouteOptimizationService.BayLocation(
                    aisle,
                    bay,
                    entry.getValue()
            ));
        }

        return bays;
    }

    /**
     * Compares two route strategies and returns a comparison result
     */
    public static class RouteComparison {
        private final String betterStrategy;
        private final int savedSteps;
        private final boolean equal;

        public RouteComparison(String betterStrategy, int savedSteps, boolean equal) {
            this.betterStrategy = betterStrategy;
            this.savedSteps = savedSteps;
            this.equal = equal;
        }

        public String getBetterStrategy() { return betterStrategy; }
        public int getSavedSteps() { return savedSteps; }
        public boolean isEqual() { return equal; }
    }

    /**
     * Compare two strategies and return the result
     */
    public RouteComparison compareStrategies(
            PickRouteOptimizationService.RouteResult strategyA,
            PickRouteOptimizationService.RouteResult strategyB) {

        if (strategyA == null || strategyB == null) {
            return null;
        }

        int distanceA = strategyA.getTotalDistance();
        int distanceB = strategyB.getTotalDistance();

        if (distanceA < distanceB) {
            return new RouteComparison("Strategy A", distanceB - distanceA, false);
        } else if (distanceB < distanceA) {
            return new RouteComparison("Strategy B", distanceA - distanceB, false);
        } else {
            return new RouteComparison(null, 0, true);
        }
    }

    /**
     * Calculate total distances for all tasks
     */
    public static class TotalDistanceSummary {
        private final int totalDistanceA;
        private final int totalDistanceB;
        private final RouteComparison comparison;

        public TotalDistanceSummary(int totalDistanceA, int totalDistanceB, RouteComparison comparison) {
            this.totalDistanceA = totalDistanceA;
            this.totalDistanceB = totalDistanceB;
            this.comparison = comparison;
        }

        public int getTotalDistanceA() { return totalDistanceA; }
        public int getTotalDistanceB() { return totalDistanceB; }
        public RouteComparison getComparison() { return comparison; }
    }

    /**
     * Calculate total distances for all tasks routes
     */
    public TotalDistanceSummary calculateTotalDistances(
            Map<Integer, Map<String, PickRouteOptimizationService.RouteResult>> allRoutes) {

        int totalDistanceA = 0;
        int totalDistanceB = 0;

        for (Map<String, PickRouteOptimizationService.RouteResult> taskRoutes : allRoutes.values()) {
            PickRouteOptimizationService.RouteResult strategyA = taskRoutes.get("strategyA");
            PickRouteOptimizationService.RouteResult strategyB = taskRoutes.get("strategyB");

            if (strategyA != null) totalDistanceA += strategyA.getTotalDistance();
            if (strategyB != null) totalDistanceB += strategyB.getTotalDistance();
        }

        RouteComparison comparison = null;
        if (totalDistanceA != 0 && totalDistanceB != 0) {
            if (totalDistanceA < totalDistanceB) {
                comparison = new RouteComparison("Strategy A", totalDistanceB - totalDistanceA, false);
            } else if (totalDistanceB < totalDistanceA) {
                comparison = new RouteComparison("Strategy B", totalDistanceA - totalDistanceB, false);
            } else {
                comparison = new RouteComparison(null, 0, true);
            }
        }

        return new TotalDistanceSummary(totalDistanceA, totalDistanceB, comparison);
    }



}

