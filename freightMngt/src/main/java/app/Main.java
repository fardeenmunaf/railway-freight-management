package app;

import app.model.PickTask;
import app.model.PickWave;
import app.model.InventoryBalance;
import app.controller.InventoryBalanceController;
import app.model.ReturnLine;
import app.model.ReturnOrder;
import app.repositories.*;
import app.service.ReturnService;
import app.service.TravelTimeFeature;
import app.service.Allocation;
import app.service.PickingPlan;
import app.service.WagonsUnloading;
import app.view.MainMenu;

import java.util.List;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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


        // Limpa dados existentesrepositories.getReturnOrderRepository().clear();
        //        repositories.getReturnLineRepository().clear();
        //
        //        String csvFile = "src\\main\\java\\app\\sampleDataset\\returns.csv";
        //        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        //
        //        try {
        //            List<String> lines = Files.readAllLines(Paths.get(csvFile));
        //
        //            for (int i = 1; i < lines.size(); i++) { // pular header
        //                String[] parts = lines.get(i).split(",", -1); // preserva colunas vazias
        //                String returnIdStr = parts[0].replaceAll("[^0-9]", "");
        //                Integer returnId = Integer.parseInt(returnIdStr);
        //                String sku = parts[1];
        //                Integer qty = Integer.parseInt(parts[2]);
        //                String reason = parts[3];
        //                LocalDateTime timestamp = LocalDateTime.parse(parts[4], formatter);
        //                String expiryStr = parts.length > 5 ? parts[5] : "";
        //                LocalDateTime expiryDate = (expiryStr == null || expiryStr.isEmpty()) ? null : LocalDateTime.parse(expiryStr + "T00:00:00");
        //
        //                // Criar ReturnOrder
        //                ReturnOrder order = new ReturnOrder(returnId, null, reason, "received", timestamp);
        //                repositories.getReturnOrderRepository().addReturnOrder(order);
        //
        //                // Criar ReturnLine
        //                ReturnLine lineObj = new ReturnLine(i, order, sku, qty, "good"); // condition inicial = good
        //                repositories.getReturnLineRepository().addReturnLine(lineObj);
        //            }
        //
        //        } catch (Exception e) {
        //            System.err.println("Erro lendo CSV: " + e.getMessage());
        //            return;
        //        }
        //
        //        // Processar retornos
        //        ReturnService service = new ReturnService();
        //        service.processAllReturns();
        //
        //        // Separar restocked vs discarded
        //        List<ReturnLine> restocked = new ArrayList<>();
        //        List<ReturnLine> discarded = new ArrayList<>();
        //
        //        for (ReturnOrder order : repositories.getReturnOrderRepository().getAllReturnOrders()) {
        //            List<ReturnLine> lines = repositories.getReturnLineRepository().getReturnLinesByReturnId(order.getReturnId());
        //            for (ReturnLine line : lines) {
        //                if ("discarded".equalsIgnoreCase(line.getCondition())) {
        //                    discarded.add(line);
        //                } else {
        //                    restocked.add(line);
        //                }
        //            }
        //        }
        //
        //        // Mostrar resultados
        //        System.out.println("\n=== Restocked Items ===");
        //        for (ReturnLine line : restocked) {
        //            System.out.printf("ReturnId=R%d | SKU=%s | Qty=%d | Reason=%s%n",
        //                    line.getReturnOrder().getReturnId(), line.getSku(), line.getQty(), line.getReturnOrder().getReason());
        //        }
        //
        //        System.out.println("\n=== Discarded Items ===");
        //        for (ReturnLine line : discarded) {
        //            System.out.printf("ReturnId=R%d | SKU=%s | Qty=%d | Reason=%s%n",
        //                    line.getReturnOrder().getReturnId(), line.getSku(), line.getQty(), line.getReturnOrder().getReason());
        //        }


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


        System.out.println("\n\n\n");

        MainMenu.display();
    }
}

