package app;

import app.model.PickTask;
import app.model.PickWave;
import app.model.InventoryBalance;
import app.controller.InventoryBalanceController;
import app.repositories.*;
import app.service.TravelTimeFeature;
import app.service.Allocation;
import app.service.PickingPlan;
import app.service.WagonsUnloading;
import app.view.MainMenu;

import java.util.List;

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

        PickingPlan pickingPlan = new PickingPlan();
        // Ver localizações de um SKU específico
        System.out.println("\nItem locations for SKU 0008:");
        List<Allocation.AllocationRecord> locations = pickingPlan.getItemLocations(8, null);
        for (Allocation.AllocationRecord loc : locations) {
            System.out.println("Lot SKU " + loc.getSku() + " na Aisle " +
                    loc.getAisle() + ", Bay " + loc.getBay() +
                    ", Qty: " + loc.getQty());
        }
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

        // Create PickingPlan instance

//        // Test planPickingTask
        System.out.println("\n=== Testing Pick Task Planning ===");
        PickingPlan.PickingPlanResult result = pickingPlan.planPickingTask();

//        System.out.println("\n=== Pick Tasks List ===");
//        for (PickTask task : result.getPickTasks()) {
//            System.out.println("PickTask " + task.getPickTaskId() +
//                    " - Waves: " + task.getPickWaves().size());
//        }

//        System.out.println("\n=== Pick Waves List ===");
//        for (PickWave wave : result.getPickWaves()) {
//            System.out.println("PickWave " + wave.getPickWaveId() +
//                    " - Cart: " + wave.getCart().getCartId() +
//                    " - Status: " + wave.getStatus());
//        }

        // Test planPickingTaskForOrder for a specific order
        System.out.println("\n=== Testing Pick Task Planning for Order 1 ===");
        PickingPlan.PickingPlanResult orderResult = pickingPlan.planPickingTaskForOrder(5);

        System.out.println("Pick Waves for Order 1: " + orderResult.getPickWaves().size());


        System.out.println("\n--- AFTER UNLOADING ---");
        InventoryBalanceController balanceController = new InventoryBalanceController();
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        allBalances = balanceController.getAllInventoryBalances();
        int assignedCount = 0;
        int stillUnassigned = 0;
        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null) {
                assignedCount++;
                System.out.println("Balance ID: " + balance.getBalanceId() +
                        " - Bay: " + balance.getBay().getBayNo());

                // Print lot details including expDate and receivedDate
                for (app.model.InventoryLot lot : balance.getLots()) {
                    System.out.println("  Lot ID: " + lot.getLotId() +
                            ", SKU: " + lot.getSku() +
                            ", Qty: " + lot.getQty() +
                            ", ExpDate: " + (lot.getExpDate() != null ? lot.getExpDate() : "N/A") +
                            ", ReceivedDate: " + (lot.getReceivedDate() != null ? lot.getReceivedDate() : "N/A"));
                }
            } else {
                stillUnassigned++;
            }
        }
        System.out.println("\nTotal assigned balances: " + assignedCount);
        System.out.println("Total still unassigned: " + stillUnassigned);

        // Print inventory balance for bay 2
        System.out.println("\n=== Inventory Balance for Bay 2 ===");

        boolean foundBay2 = false;
        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && balance.getBay().getBayId() == 2) {
                System.out.println("Balance ID: " + balance.getBalanceId());
                System.out.println("Bay ID: " + balance.getBay().getBayId());
                System.out.println("Quantity: " + balance.getQty());
                System.out.println("Number of lots: " + balance.getLots().size());
                if (!balance.getLots().isEmpty()) {
                    System.out.println("Lots details:");
                    balance.getLots().forEach(lot -> {
                        System.out.println("  - Lot ID: " + lot.getLotId() +
                                ", SKU: " + lot.getSku() +
                                ", Qty: " + lot.getQty());
                    });
                }
                System.out.println("---");
                foundBay2 = true;
            }
        }

        if (!foundBay2) {
            System.out.println("No inventory balance found for bay 2");
        }

        boolean foundBay3 = false;
        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && balance.getBay().getBayId() == 3) {
                System.out.println("Balance ID: " + balance.getBalanceId());
                System.out.println("Bay ID: " + balance.getBay().getBayId());
                System.out.println("Quantity: " + balance.getQty());
                System.out.println("Number of lots: " + balance.getLots().size());
                if (!balance.getLots().isEmpty()) {
                    System.out.println("Lots details:");
                    balance.getLots().forEach(lot -> {
                        System.out.println("  - Lot ID: " + lot.getLotId() +
                                ", SKU: " + lot.getSku() +
                                ", Qty: " + lot.getQty());
                    });
                }
                System.out.println("---");
                foundBay3 = true;
            }
        }

        if (!foundBay3) {
            System.out.println("No inventory balance found for bay 3");
        }
        System.out.println("=====================================\n");


        System.out.println("\n\n\n");

        MainMenu.display();
    }
}

