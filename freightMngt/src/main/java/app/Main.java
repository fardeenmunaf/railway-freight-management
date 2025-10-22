package app;

import app.controller.InventoryBalanceController;
import app.controller.InventoryMoveController;
import app.model.InventoryBalance;
import app.model.InventoryMove;
import app.model.WarehouseBay;
import app.repositories.*;
import app.service.InventoryDispatch;
import app.service.InventoryRelocation;
import app.service.WagonsUnloading;
import app.view.MainMenu;

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

        System.out.println("Initializing dataset...");
        appInitializer.run();

        System.out.println(itemCategoryRepository.getAllCategories().stream().limit(3).toList());
        System.out.println(itemRepository.getAllItems().stream().limit(3).toList());
        System.out.println(orderRepository.getAllOrders().stream().limit(3).toList());
        System.out.println(warehouseRepository.getAllWarehouses().stream().limit(3).toList());
        System.out.println(warehouseAisleRepository.getAllAisles().stream().limit(3).toList());
        System.out.println(warehouseBayRepository.getAllWarehouseBays().stream().limit(3).toList());
        System.out.println("Dataset initialized successfully.");

        System.out.println("\n=== TESTING WAGONS UNLOADING ===");

        WagonsUnloading wagonsUnloading = new WagonsUnloading();
        InventoryBalanceController balanceController = new InventoryBalanceController();

        System.out.println("\n--- BEFORE UNLOADING ---");
        java.util.List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        int unassignedCount = 0;
        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() == null) {
                unassignedCount++;
                Integer sku = balance.getLots().isEmpty() ? null : balance.getLots().get(0).getSku();
                System.out.println("Balance ID: " + balance.getBalanceId() +
                    " | SKU: " + sku +
                    " | Qty: " + balance.getQty() +
                    " | Lots: " + balance.getLots().size() +
                    " | Bay: null");
            }
        }
        System.out.println("Total unassigned balances: " + unassignedCount);





        System.out.println("\n=== TESTING LOTS ORDERING (FEFO/FIFO) ===");
        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() == null && !balance.getLots().isEmpty()) {
                Integer sku = balance.getLots().get(0).getSku();
                System.out.println("\n--- Balance ID: " + balance.getBalanceId() + " | SKU: " + sku + " ---");
                System.out.println("Total Lots: " + balance.getLots().size() + " | Total Qty: " + balance.getQty());

                int lotIndex = 1;
                for (app.model.InventoryLot lot : balance.getLots()) {
                    String expDateStr = lot.getExpDate() != null ? lot.getExpDate().toString() : "NO EXPIRY";
                    String receivedStr = lot.getReceivedDate().toString();
                    System.out.println("  Lot " + lotIndex + ": ID=" + lot.getLotId() +
                        " | Qty=" + lot.getBoxCode() +
                        " | ExpDate=" + expDateStr +
                        " | ReceivedAt=" + receivedStr);
                    lotIndex++;
                }
            }
        }

        System.out.println("\n--- EXECUTING UNLOADING ---");
        wagonsUnloading.unloadAllWagons();

        System.out.println("\n--- AFTER UNLOADING ---");
        allBalances = balanceController.getAllInventoryBalances();
        int assignedCount = 0;
        int stillUnassigned = 0;
        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null) {
                assignedCount++;
                Integer sku = balance.getLots().isEmpty() ? null : balance.getLots().get(0).getSku();
                System.out.println("Balance ID: " + balance.getBalanceId() +
                        " | SKU: " + sku +
                        " | Qty: " + balance.getQty() +
                        " | Lots: " + balance.getLots().size() +
                        " | Bay: " + balance.getBay().getBayId());
            } else {
                stillUnassigned++;
            }
        }
        System.out.println("\nTotal assigned balances: " + assignedCount);
        System.out.println("Total still unassigned: " + stillUnassigned);





        System.out.println("\n=== TESTING DISPATCH OPERATION ===");
        InventoryDispatch inventoryDispatch = new InventoryDispatch();

        // Test 1: Dispatch with sufficient stock
        System.out.println("\n--- Test 1: Dispatch 50 units of SKU 22 ---");
        allBalances = balanceController.getAllInventoryBalances();
        System.out.println("Before dispatch:");
        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && !balance.getLots().isEmpty()) {
                Integer sku = balance.getLots().get(0).getSku();
                if (sku.equals(22)) {
                    System.out.println("  Balance ID: " + balance.getBalanceId() +
                        " | SKU: " + sku +
                        " | Qty: " + balance.getQty() +
                        " | Lots: " + balance.getLots().size() +
                        " | Bay: " + balance.getBay().getBayId());
                }
            }
        }

        boolean dispatchSuccess = inventoryDispatch.dispatchStock(22, 50);
        System.out.println("Dispatch result: " + (dispatchSuccess ? "SUCCESS" : "FAILED"));

        System.out.println("After dispatch:");
        allBalances = balanceController.getAllInventoryBalances();
        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && !balance.getLots().isEmpty()) {
                Integer sku = balance.getLots().get(0).getSku();
                if (sku.equals(22)) {
                    System.out.println("  Balance ID: " + balance.getBalanceId() +
                        " | SKU: " + sku +
                        " | Qty: " + balance.getQty() +
                        " | Lots: " + balance.getLots().size() +
                        " | Bay: " + balance.getBay().getBayId());
                }
            }
        }

        // Test 2: Dispatch with insufficient stock
        System.out.println("\n--- Test 2: Dispatch 10000 units of SKU 22 (insufficient stock) ---");
        dispatchSuccess = inventoryDispatch.dispatchStock(22, 10000);
        System.out.println("Dispatch result: " + (dispatchSuccess ? "SUCCESS" : "FAILED (as expected - insufficient stock)"));



        System.out.println("\n=== TESTING RELOCATION OPERATION ===");

        InventoryRelocation relocationService = new InventoryRelocation();
        app.controller.InventoryLotController lotController = new app.controller.InventoryLotController();
        app.controller.WarehouseBayController bayController = new app.controller.WarehouseBayController();
        InventoryMoveController moveController = new InventoryMoveController();

        // Get data for testing
        allBalances = balanceController.getAllInventoryBalances();
        java.util.List<WarehouseBay> allBays = bayController.getAllWarehouseBays();

        // Find balances with lots for testing
        InventoryBalance testBalance1 = null;
        InventoryBalance testBalance2 = null;
        WarehouseBay emptyBay = null;
        WarehouseBay fullBay = null;

        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && !balance.getLots().isEmpty()) {
                if (testBalance1 == null) {
                    testBalance1 = balance;
                } else if (testBalance2 == null && !balance.getLots().get(0).getSku().equals(testBalance1.getLots().get(0).getSku())) {
                    testBalance2 = balance;
                }
            }
        }

        // Find an empty bay
        for (WarehouseBay bay : allBays) {
            boolean isEmpty = true;
            for (InventoryBalance balance : allBalances) {
                if (balance.getBay() != null && balance.getBay().getBayId().equals(bay.getBayId())) {
                    isEmpty = false;
                    break;
                }
            }
            if (isEmpty) {
                emptyBay = bay;
                break;
            }
        }

        // Find a bay that is full (capacity = number of lots)
        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && !balance.getLots().isEmpty()) {
                WarehouseBay bay = balance.getBay();
                int lotsInBay = balance.getLots().size();
                if (bay.getCapacityBox() <= lotsInBay) {
                    fullBay = bay;
                    break;
                }
            }
        }

        System.out.println("\n--- Test 1: SUCCESSFUL RELOCATION ---");
        if (testBalance1 != null && !testBalance1.getLots().isEmpty() && emptyBay != null) {
            app.model.InventoryLot lotToRelocate = testBalance1.getLots().get(0);
            Integer originalBayId = testBalance1.getBay().getBayId();

            System.out.println("Relocating Lot " + lotToRelocate.getLotId() +
                             " (SKU: " + lotToRelocate.getSku() +
                             ") from Bay " + originalBayId +
                             " to Bay " + emptyBay.getBayId());

            boolean result = relocationService.relocateLot(
                lotToRelocate.getLotId(),
                emptyBay.getBayId(),
                "Test: Successful relocation to empty bay"
            );

            System.out.println("Result: " + (result ? "✓ SUCCESS" : "✗ FAILED"));
        } else {
            System.out.println("✗ Insufficient data for test");
        }

        System.out.println("\n--- Test 2: RELOCATION WITH NON-EXISTENT LOT ID ---");
        Integer nonExistentLotId = 999999;
        if (emptyBay != null) {
            System.out.println("Attempting to relocate non-existent Lot " + nonExistentLotId +
                             " to Bay " + emptyBay.getBayId());

            boolean result = relocationService.relocateLot(
                nonExistentLotId,
                emptyBay.getBayId(),
                "Test: Non-existent lot"
            );

            System.out.println("Result: " + (result ? "✗ UNEXPECTED SUCCESS" : "✓ FAILED AS EXPECTED (Lot not found)"));
        } else {
            System.out.println("✗ Insufficient data for test");
        }

        System.out.println("\n--- Test 3: RELOCATION TO BAY WITH INSUFFICIENT CAPACITY ---");
        if (testBalance1 != null && !testBalance1.getLots().isEmpty()) {
            // Create a scenario: find a bay with capacity = 1 and try to add another lot
            WarehouseBay targetBayNoCapacity = null;

            // Try to use the full bay we found earlier
            if (fullBay != null) {
                targetBayNoCapacity = fullBay;
            } else {
                // Find any bay and check its capacity vs current lots
                for (InventoryBalance balance : allBalances) {
                    if (balance.getBay() != null && !balance.getLots().isEmpty()) {
                        WarehouseBay bay = balance.getBay();
                        int currentLots = balance.getLots().size();
                        if (bay.getCapacityBox() <= currentLots) {
                            targetBayNoCapacity = bay;
                            break;
                        }
                    }
                }
            }

            if (targetBayNoCapacity != null) {
                // Find a lot from a different bay to relocate
                app.model.InventoryLot lotForCapacityTest = null;
                for (InventoryBalance balance : allBalances) {
                    if (balance.getBay() != null && !balance.getLots().isEmpty() &&
                        !balance.getBay().getBayId().equals(targetBayNoCapacity.getBayId())) {
                        lotForCapacityTest = balance.getLots().get(0);
                        break;
                    }
                }

                if (lotForCapacityTest != null) {
                    System.out.println("Attempting to relocate Lot " + lotForCapacityTest.getLotId() +
                                     " to Bay " + targetBayNoCapacity.getBayId() +
                                     " (capacity: " + targetBayNoCapacity.getCapacityBox() + ", currently full)");

                    boolean result = relocationService.relocateLot(
                        lotForCapacityTest.getLotId(),
                        targetBayNoCapacity.getBayId(),
                        "Test: Insufficient capacity"
                    );

                    System.out.println("Result: " + (result ? "✗ UNEXPECTED SUCCESS" : "✓ FAILED AS EXPECTED (No capacity)"));
                } else {
                    System.out.println("✗ Could not find lot for capacity test");
                }
            } else {
                System.out.println("✗ Could not find full bay for capacity test");
            }
        } else {
            System.out.println("✗ Insufficient data for test");
        }

        System.out.println("\n--- Test 4: RELOCATION TO BAY WITH INCOMPATIBLE SKU ---");
        if (testBalance1 != null && testBalance2 != null &&
            !testBalance1.getLots().isEmpty() && !testBalance2.getLots().isEmpty()) {

            Integer sku1 = testBalance1.getLots().get(0).getSku();
            Integer sku2 = testBalance2.getLots().get(0).getSku();

            if (!sku1.equals(sku2)) {
                // Try to move a lot from balance1 (with sku1) to the bay of balance2 (which has sku2)
                app.model.InventoryLot lotWithDifferentSku = testBalance1.getLots().get(0);
                Integer targetBayWithDifferentSku = testBalance2.getBay().getBayId();

                System.out.println("Attempting to relocate Lot " + lotWithDifferentSku.getLotId() +
                                 " (SKU: " + sku1 + ") to Bay " + targetBayWithDifferentSku +
                                 " (which contains SKU: " + sku2 + ")");

                boolean result = relocationService.relocateLot(
                    lotWithDifferentSku.getLotId(),
                    targetBayWithDifferentSku,
                    "Test: SKU incompatibility"
                );

                System.out.println("Result: " + (result ? "✗ UNEXPECTED SUCCESS" : "✓ FAILED AS EXPECTED (SKU incompatible)"));
            } else {
                System.out.println("✗ Could not find balances with different SKUs");
            }
        } else {
            System.out.println("✗ Insufficient data for test");
        }

        // Print all inventory moves created during relocations
        System.out.println("\n=== ALL INVENTORY MOVES CREATED DURING RELOCATIONS ===");
        java.util.List<InventoryMove> allMoves = moveController.getAllInventoryMoves();

        if (allMoves.isEmpty()) {
            System.out.println("No inventory moves recorded.");
        } else {
            System.out.println("Total Inventory Moves: " + allMoves.size());
            System.out.println("\n" + "=".repeat(100));

            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            for (InventoryMove move : allMoves) {
                System.out.println("\n┌─ INVENTORY MOVE #" + move.getMoveId() + " " + "─".repeat(80));
                System.out.println("│ Lot ID:        " + move.getLot().getLotId());
                System.out.println("│ SKU:           " + move.getLot().getSku());
                System.out.println("│ From Bay:      " + move.getFromBay().getBayId());
                System.out.println("│ To Bay:        " + move.getToBay().getBayId());
                System.out.println("│ Quantity:      " + move.getQty() + " units");
                System.out.println("│ Reason:        " + move.getReason());
                System.out.println("│ Created At:    " + move.getCreatedAt().format(formatter));
                System.out.println("└" + "─".repeat(99));
            }

            System.out.println("\n" + "=".repeat(100));
            System.out.println("Summary: " + allMoves.size() + " inventory move(s) recorded successfully.");
        }


        //MainMenu.display();
    }
}