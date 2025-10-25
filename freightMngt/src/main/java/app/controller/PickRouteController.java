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
            Integer bayNumber = record.getBay();
            Double quantity = record.getQty();

            // Assumindo que todos são da mesma aisle ou você tem como obter
            // Se precisar da aisle, adicione um método record.aisle()
            String key = "1-" + bayNumber; // ou use aisle correto
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


}

