package app;

import app.controller.PickRouteController;
import app.model.PickTask;
import app.model.PickWave;
import app.model.InventoryBalance;
import app.controller.InventoryBalanceController;
import app.repositories.*;
import app.service.*;
import app.view.MainMenu;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        AppInitializer appInitializer = new AppInitializer();
        Repositories repositories = Repositories.getInstance();
        ItemCategoryRepository itemCategoryRepository = repositories.getItemCategoryRepository();
        ItemRepository itemRepository = repositories.getItemRepository();
        OrderRepository orderRepository = repositories.getOrderRepository();
        WarehouseRepository warehouseRepository = repositories.getWarehouseRepository();
        WarehouseAisleRepository warehouseAisleRepository = repositories.getWarehouseAisleRepository();
        WarehouseBayRepository warehouseBayRepository = repositories.getWarehouseBayRepository();
        OrderLineRepository orderLineRepository = repositories.getOrderLineRepository();
        ReturnOrderRepository returnOrderRepository = repositories.getReturnOrderRepository();

        TravelTimeFeature.TravelTimeCalculator travelTime = TravelTimeFeature.create(
                repositories.getFacilityRepository(),
                repositories.getLineSegmentRepository(),
                repositories.getLineSegmentFacilityRepository(),
                repositories.getLocomotiveRepository()
        );


        System.out.println("Initializing dataset...");
        appInitializer.run();

//        System.out.println(itemCategoryRepository.getAllCategories().stream().limit(3).toList());
//        System.out.println(itemRepository.getAllItems().stream().limit(3).toList());
        System.out.println(orderRepository.getAllOrders().stream().limit(3).toList());
//        System.out.println(warehouseRepository.getAllWarehouses().stream().limit(3).toList());
//        System.out.println(warehouseAisleRepository.getAllAisles().stream().limit(3).toList());
//        System.out.println(warehouseBayRepository.getAllWarehouseBays().stream().limit(3).toList());
        System.out.println(orderLineRepository.getAllOrderLines().stream().limit(3).toList());
//        System.out.println(returnOrderRepository.getAllReturnOrders().stream().limit(3).toList());
        System.out.println("Dataset initialized successfully.\n\n");


        System.out.println("Unloading wagons and fill bays with items...");
        WagonsUnloading wagonsUnloading = new WagonsUnloading();
        wagonsUnloading.unloadAllWagons();


        //Testes comentados para mais tarde!!!!!!!
//        System.out.println("Available inventory balances:");
//        System.out.println(pickingPlan.getAllItemBalences().stream().limit(1).toList());
//        System.out.println("Orders to be picked:");
//        System.out.println(pickingPlan.getAllOrders().stream().limit(1).toList());
//        System.out.println("Order lines to be picked:");
//        System.out.println(pickingPlan.getAllOrderLines().stream().limit(1).toList());
//        System.out.println("Order lines from order ID 1:");
//        System.out.println(pickingPlan.getAllOrderLinesFromOrder(1).stream().limit(1).toList());
//        System.out.println("Carts available:");
//        System.out.println(pickingPlan.getAllCarts().stream().limit(1).toList());
//        System.out.println("Cart capacity for order line ID 1:");
//        System.out.println(pickingPlan.calculateCartCapacity(pickingPlan.getAllCarts().get(0), pickingPlan.getAllOrderLines().get(0)));
//        System.out.println("Cart travels");
//        System.out.println(pickingPlan.calculateTripsNeeded(pickingPlan.getAllCarts().get(0), pickingPlan.getAllOrderLines().get(0)));
//        System.out.println("Wagons unloaded and bays filled successfully.\n\n");

        // --- Demo - STRICT MODE ---
//        System.out.println("Running allocation planner (STRICT mode)...");
//        app.service.Allocation.AllocationResult result = pickingPlan.planAllocations("STRICT");
//
//        System.out.println("\nOrder line eligibilities:");
//        if (result.getEligibilities().isEmpty()) {
//            System.out.println("  (no eligibilities)");
//        } else {
//            for (app.service.Allocation.OrderEligibility e : result.getEligibilities()) {
//                System.out.println(String.format(
//                        "  orderId=%s, lineNo=%s, sku=%s, requested=%s, allocated=%s, status=%s",
//                        String.valueOf(e.getOrderId()),
//                        String.valueOf(e.getLineNo()),
//                        String.valueOf(e.getSku()),
//                        String.valueOf(e.getRequestedQty()),
//                        String.valueOf(e.getAllocatedQty()),
//                        String.valueOf(e.getStatus())
//                ));
//            }
//        }


        // --- Demo - PARTIAL MODE ---
//        System.out.println("Running allocation planner (PARTIAL mode)...");
//        app.service.Allocation.AllocationResult result2 = pickingPlan.planAllocations("PARTIAL");
//
//        System.out.println("\nOrder line eligibilities:");
//        if (result2.getEligibilities().isEmpty()) {
//            System.out.println("  (no eligibilities)");
//        } else {
//            for (app.service.Allocation.OrderEligibility e : result2.getEligibilities()) {
//                System.out.println(String.format(
//                        "  orderId=%s, lineNo=%s, sku=%s, requested=%s, allocated=%s, status=%s",
//                        String.valueOf(e.getOrderId()),
//                        String.valueOf(e.getLineNo()),
//                        String.valueOf(e.getSku()),
//                        String.valueOf(e.getRequestedQty()),
//                        String.valueOf(e.getAllocatedQty()),
//                        String.valueOf(e.getStatus())
//                ));
//            }
//        }

        // Create PickingPlan instance
        PickingPlan pickingPlan = new PickingPlan();


        // Ver localizações de um SKU específico
//        System.out.println("\nItem locations for SKU 0008:");
//        List<Allocation.AllocationRecord> locations = pickingPlan.getItemLocations(8, null);
//        for (Allocation.AllocationRecord loc : locations) {
//            System.out.println("Lot SKU " + loc.getSku() + " na Aisle " +
//                    loc.getAisle() + ", Bay " + loc.getBay() +
//                    ", Qty: " + loc.getQty());
//        }
//
//        // Ver status de elegibilidade das encomendas
//        System.out.println("\nOrder eligibilities:");
//        Allocation.AllocationResult partialAllocation = pickingPlan.planAllocations("PARTIAL");
//        List<Allocation.OrderEligibility> eligibilities = partialAllocation.getEligibilities();
//        for (Allocation.OrderEligibility e : eligibilities) {
//            if (e.getStatus().equals("ELIGIBLE") || e.getStatus().equals("PARTIAL"))
//                System.out.println("Order " + e.getOrderId() +
//                        ", SKU " + e.getSku() +
//                        ", Status: " + e.getStatus());
//        }

        // --- end demo ---


//        // Test planPickingTask
        System.out.println("\n=== Testing Pick Task Planning ===");
        PickingPlan.PickingPlanResult result = pickingPlan.planPickingTask();

        System.out.println("\n=== Pick Tasks List ===");
        for (PickTask task : result.getPickTasks()) {
            System.out.println("PickTask " + task.getPickTaskId() +
                    " - Waves: " + task.getPickWaves().size());
        }

        System.out.println("\n=== Pick Waves List ===");
        for (PickWave wave : result.getPickWaves()) {
            System.out.println("PickWave " + wave.getPickWaveId() +
                    " - Cart: " + wave.getCart().getCartId() +
                    " - Status: " + wave.getStatus());
        }

        // Calculate routes for all pick tasks
        PickRouteController routeController = new PickRouteController();
        System.out.println("\n=== Calculating Optimal Routes ===");
        Map<Integer, Map<String, PickRouteOptimizationService.RouteResult>> allRoutes =
                routeController.calculateRoutesForPickTasks(result);

        // Display results
        for (Map.Entry<Integer, Map<String, PickRouteOptimizationService.RouteResult>> entry : allRoutes.entrySet()) {
            System.out.println("\nPickTask " + entry.getKey() + ":");
            for (Map.Entry<String, PickRouteOptimizationService.RouteResult> routeEntry : entry.getValue().entrySet()) {
                System.out.println("  " + routeEntry.getValue());
            }
        }

        // Calculate and display total distance for all pick tasks
        System.out.println("\n=== Total Distance Summary ===");
        int totalDistanceStrategyA = 0;
        int totalDistanceStrategyB = 0;

        for (Map.Entry<Integer, Map<String, PickRouteOptimizationService.RouteResult>> entry : allRoutes.entrySet()) {
            Map<String, PickRouteOptimizationService.RouteResult> strategies = entry.getValue();

            if (strategies.containsKey("strategyA")) {
                totalDistanceStrategyA += strategies.get("strategyA").getTotalDistance();
            }
            if (strategies.containsKey("strategyB")) {
                totalDistanceStrategyB += strategies.get("strategyB").getTotalDistance();
            }
        }

        System.out.println("Total Distance (Strategy A - Deterministic Sweep): " + totalDistanceStrategyA);
        System.out.println("Total Distance (Strategy B - Nearest Neighbour): " + totalDistanceStrategyB);
        System.out.println("Best Strategy: " + (totalDistanceStrategyA <= totalDistanceStrategyB ?
                "Strategy A (saves " + (totalDistanceStrategyB - totalDistanceStrategyA) + " steps)" :
                "Strategy B (saves " + (totalDistanceStrategyA - totalDistanceStrategyB) + " steps)"));

// === NOVA SECÇÃO: Rotas consolidadas por Order com detalhe ===

        System.out.println("\n\n=== Detailed Route Information by Order ===");

// 1. Agrupar PickTasks por Order
        Map<Integer, List<PickTask>> tasksByOrder = new HashMap<>();
        for (PickTask pickTask : result.getPickTasks()) {
            Integer orderId = pickTask.getOrderLine().getOrder().getOrderId();
            tasksByOrder.computeIfAbsent(orderId, k -> new ArrayList<>()).add(pickTask);
        }

// 2. Processar cada Order
        for (Map.Entry<Integer, List<PickTask>> entry : tasksByOrder.entrySet()) {
            Integer orderId = entry.getKey();
            List<PickTask> tasks = entry.getValue();

            System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║  ORDER " + orderId + " - Detailed Route                              ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");

            // Coletar bays únicas e mapear SKUs por bay
            Map<String, PickRouteOptimizationService.BayLocation> uniqueBays = new LinkedHashMap<>();
            Map<String, List<Integer>> skusByBay = new HashMap<>();

            for (PickTask pickTask : tasks) {
                Integer sku = pickTask.getOrderLine().getSku().getItemId();
                List<Allocation.AllocationRecord> allocations =
                        pickingPlan.getItemLocations(sku, orderId);

                for (Allocation.AllocationRecord record : allocations) {
                    String bayKey = "1-" + record.getBay();

                    if (!uniqueBays.containsKey(bayKey)) {
                        uniqueBays.put(bayKey, new PickRouteOptimizationService.BayLocation(
                                "1", record.getBay(), record.getQty()
                        ));
                    }

                    skusByBay.computeIfAbsent(bayKey, k -> new ArrayList<>()).add(sku);
                }
            }

            // Calcular rotas
            List<PickRouteOptimizationService.BayLocation> orderBays =
                    new ArrayList<>(uniqueBays.values());

            Map<String, PickRouteOptimizationService.RouteResult> orderRoutes =
                    routeController.optimizeRoute(orderBays);

            // Apresentar Strategy A
            if (orderRoutes.containsKey("strategyA")) {
                PickRouteOptimizationService.RouteResult routeA = orderRoutes.get("strategyA");

                System.out.println("\n▶ Strategy A (Deterministic Sweep):");
                System.out.println("  Total Distance: " + routeA.getTotalDistance() + " steps\n");

                int currentAisle = 0;
                int currentBay = 0;
                int stepCount = 0;

                System.out.println("  Step-by-step route:");
                System.out.println("  ─────────────────────────────────────────────────────────────");

                for (int i = 0; i < routeA.getPath().size(); i++) {
                    PickRouteOptimizationService.BayLocation bay = routeA.getPath().get(i);
                    int nextAisle = Integer.parseInt(bay.getAisle());
                    int nextBay = bay.getBay();

                    int distance = 0;
                    if (currentAisle == nextAisle) {
                        distance = Math.abs(currentBay - nextBay);
                    } else {
                        distance = currentBay + Math.abs(currentAisle - nextAisle) * 3 + nextBay;
                    }

                    stepCount += distance;

                    String bayKey = nextAisle + "-" + nextBay;
                    List<Integer> skus = skusByBay.getOrDefault(bayKey, Collections.emptyList());

                    System.out.println(String.format(
                            "  [%d] (%d,%d) → (%d,%d) | Distance: %d steps | Cumulative: %d steps",
                            i + 1,
                            currentAisle, currentBay,
                            nextAisle, nextBay,
                            distance,
                            stepCount
                    ));

                    System.out.println("      ↳ Pick SKU(s): " + skus + " (qty: " + bay.getQuantity() + ")");

                    currentAisle = nextAisle;
                    currentBay = nextBay;
                }

                System.out.println("  ─────────────────────────────────────────────────────────────");
                System.out.println("  Final position: (" + currentAisle + "," + currentBay + ")");
            }

            // Apresentar Strategy B
            if (orderRoutes.containsKey("strategyB")) {
                PickRouteOptimizationService.RouteResult routeB = orderRoutes.get("strategyB");

                System.out.println("\n▶ Strategy B (Nearest Neighbour):");
                System.out.println("  Total Distance: " + routeB.getTotalDistance() + " steps\n");

                int currentAisle = 0;
                int currentBay = 0;
                int stepCount = 0;

                System.out.println("  Step-by-step route:");
                System.out.println("  ─────────────────────────────────────────────────────────────");

                for (int i = 0; i < routeB.getPath().size(); i++) {
                    PickRouteOptimizationService.BayLocation bay = routeB.getPath().get(i);
                    int nextAisle = Integer.parseInt(bay.getAisle());
                    int nextBay = bay.getBay();

                    int distance = 0;
                    if (currentAisle == nextAisle) {
                        distance = Math.abs(currentBay - nextBay);
                    } else {
                        distance = currentBay + Math.abs(currentAisle - nextAisle) * 3 + nextBay;
                    }

                    stepCount += distance;

                    String bayKey = nextAisle + "-" + nextBay;
                    List<Integer> skus = skusByBay.getOrDefault(bayKey, Collections.emptyList());

                    System.out.println(String.format(
                            "  [%d] (%d,%d) → (%d,%d) | Distance: %d steps | Cumulative: %d steps",
                            i + 1,
                            currentAisle, currentBay,
                            nextAisle, nextBay,
                            distance,
                            stepCount
                    ));

                    System.out.println("      ↳ Pick SKU(s): " + skus + " (qty: " + bay.getQuantity() + ")");

                    currentAisle = nextAisle;
                    currentBay = nextBay;
                }

                System.out.println("  ─────────────────────────────────────────────────────────────");
                System.out.println("  Final position: (" + currentAisle + "," + currentBay + ")");
            }
        }

        System.out.println("\n\n\n");

        MainMenu.display();
    }
}

