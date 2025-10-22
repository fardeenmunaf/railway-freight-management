package app;

import app.controller.InventoryBalanceController;
import app.model.InventoryBalance;
import app.repositories.*;
import app.service.InventoryDispatch;
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

        // =================================================================
        // RELOCATION TEST: Move lot89 to bay27
        // =================================================================
        System.out.println("\n=================================================================");
        System.out.println("=== TESTING RELOCATION: LOT89 -> BAY27 ===");
        System.out.println("=================================================================");

        app.service.InventoryRelocation inventoryRelocation = new app.service.InventoryRelocation();
        app.controller.InventoryLotController lotController = new app.controller.InventoryLotController();
        app.controller.InventoryMoveController moveController = new app.controller.InventoryMoveController();

        // STEP 1: Find lot89
        System.out.println("\n--- STEP 1: Finding lot89 ---");
        app.model.InventoryLot lot89 = lotController.getInventoryLotById(89);
        if (lot89 == null) {
            System.out.println("❌ ERROR: Lot 89 not found!");
            return;
        }
        System.out.println("✓ Lot 89 found:");
        System.out.println("  Lot ID: " + lot89.getLotId());
        System.out.println("  SKU: " + lot89.getSku());
        System.out.println("  Quantity: " + lot89.getBoxCode());
        System.out.println("  Received Date: " + lot89.getReceivedDate());
        System.out.println("  Expiration Date: " + lot89.getExpDate());

        // STEP 2: Find current location of lot89
        System.out.println("\n--- STEP 2: Finding current location of lot89 ---");
        InventoryBalance currentBalance = null;
        for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
            if (balance.getBay() != null) {
                for (app.model.InventoryLot lot : balance.getLots()) {
                    if (lot.getLotId().equals(89)) {
                        currentBalance = balance;
                        break;
                    }
                }
                if (currentBalance != null) break;
            }
        }

        if (currentBalance == null) {
            System.out.println("❌ ERROR: Lot 89 not found in any bay!");
            return;
        }

        System.out.println("✓ Lot 89 is currently in Bay " + currentBalance.getBay().getBayId());
        System.out.println("  Current Balance ID: " + currentBalance.getBalanceId());
        System.out.println("  Total Quantity in bay: " + currentBalance.getQty());
        System.out.println("  Number of Lots in bay: " + currentBalance.getLots().size());
        System.out.println("  All lots in current bay:");
        int idx = 1;
        for (app.model.InventoryLot lot : currentBalance.getLots()) {
            String marker = lot.getLotId().equals(89) ? " ◄── LOT TO RELOCATE" : "";
            System.out.println("    " + idx + ". Lot " + lot.getLotId() +
                             " (SKU: " + lot.getSku() +
                             ", Qty: " + lot.getBoxCode() +
                             ", Exp: " + lot.getExpDate() + ")" + marker);
            idx++;
        }

        // STEP 3: Check target bay27
        System.out.println("\n--- STEP 3: Checking target bay27 ---");
        InventoryBalance bay27Balance = null;
        for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(27)) {
                bay27Balance = balance;
                break;
            }
        }

        if (bay27Balance != null) {
            System.out.println("✓ Bay 27 has existing inventory:");
            System.out.println("  Balance ID: " + bay27Balance.getBalanceId());
            System.out.println("  Total Quantity: " + bay27Balance.getQty());
            System.out.println("  Number of Lots: " + bay27Balance.getLots().size());
            System.out.println("  Existing lots in bay27 (FEFO order):");
            idx = 1;
            for (app.model.InventoryLot lot : bay27Balance.getLots()) {
                System.out.println("    " + idx + ". Lot " + lot.getLotId() +
                                 " (SKU: " + lot.getSku() +
                                 ", Qty: " + lot.getBoxCode() +
                                 ", Exp: " + lot.getExpDate() + ")");
                idx++;
            }
        } else {
            System.out.println("✓ Bay 27 is currently empty");
        }

        // Count moves before relocation
        int movesBefore = moveController.getAllInventoryMoves().size();

        // Store original values BEFORE relocation for comparison
        Integer originalSourceQty = currentBalance.getQty();
        Integer originalSourceLotCount = currentBalance.getLots().size();
        Integer sourceBayId = currentBalance.getBay().getBayId();

        // STEP 4: Execute relocation
        System.out.println("\n--- STEP 4: Executing relocation ---");
        System.out.println(">>> Relocating Lot 89 from Bay " + sourceBayId + " to Bay 27...");

        boolean success = inventoryRelocation.relocateLot(89, 27, "Test relocation - Moving lot89 to bay27");

        // STEP 5: Show results
        System.out.println("\n=================================================================");
        if (success) {
            System.out.println("✅ RELOCATION SUCCESSFUL!");
            System.out.println("=================================================================");

            System.out.println("\n--- STEP 5: Verification ---");

            // Check source bay after relocation
            InventoryBalance sourceAfter = null;
            for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
                if (balance.getBay() != null && balance.getBay().getBayId().equals(sourceBayId)) {
                    sourceAfter = balance;
                    break;
                }
            }

            System.out.println("\n▸ Source Bay " + sourceBayId + " after relocation:");
            if (sourceAfter != null) {
                System.out.println("  Balance ID: " + sourceAfter.getBalanceId());
                System.out.println("  Total Quantity: " + sourceAfter.getQty() + " (was: " + originalSourceQty + ")");
                System.out.println("  Number of Lots: " + sourceAfter.getLots().size() + " (was: " + originalSourceLotCount + ")");
                if (!sourceAfter.getLots().isEmpty()) {
                    System.out.println("  Remaining lots:");
                    for (app.model.InventoryLot lot : sourceAfter.getLots()) {
                        System.out.println("    - Lot " + lot.getLotId() + " (SKU: " + lot.getSku() + ", Qty: " + lot.getBoxCode() + ")");
                    }
                }
            } else {
                System.out.println("  ✓ Bay is now empty (balance deleted)");
                System.out.println("  (was: " + originalSourceQty + " quantity, " + originalSourceLotCount + " lots)");
            }

            // Check target bay27 after relocation
            InventoryBalance bay27After = null;
            for (InventoryBalance balance : balanceController.getAllInventoryBalances()) {
                if (balance.getBay() != null && balance.getBay().getBayId().equals(27)) {
                    bay27After = balance;
                    break;
                }
            }

            System.out.println("\n▸ Target Bay 27 after relocation:");
            if (bay27After != null) {
                System.out.println("  Balance ID: " + bay27After.getBalanceId());
                System.out.println("  Total Quantity: " + bay27After.getQty());
                System.out.println("  Number of Lots: " + bay27After.getLots().size());
                System.out.println("  All lots in bay27 (FEFO order):");
                idx = 1;
                for (app.model.InventoryLot lot : bay27After.getLots()) {
                    String marker = lot.getLotId().equals(89) ? " ◄── RELOCATED LOT" : "";
                    System.out.println("    " + idx + ". Lot " + lot.getLotId() +
                                     " (SKU: " + lot.getSku() +
                                     ", Qty: " + lot.getBoxCode() +
                                     ", Exp: " + lot.getExpDate() + ")" + marker);
                    idx++;
                }
            } else {
                System.out.println("  ❌ ERROR: No balance found!");
            }

            // Show inventory move record
            System.out.println("\n▸ Inventory Move Record (Audit Trail):");
            java.util.List<app.model.InventoryMove> allMoves = moveController.getAllInventoryMoves();
            if (allMoves.size() > movesBefore) {
                app.model.InventoryMove lastMove = allMoves.get(allMoves.size() - 1);
                System.out.println("  Move ID: " + lastMove.getMoveId());
                System.out.println("  Lot ID: " + lastMove.getLot().getLotId());
                System.out.println("  From Bay: " + lastMove.getFromBay().getBayId());
                System.out.println("  To Bay: " + lastMove.getToBay().getBayId());
                System.out.println("  Quantity: " + lastMove.getQty());
                System.out.println("  Reason: " + lastMove.getReason());

                // Format timestamp to be more readable
                java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String formattedTimestamp = lastMove.getCreatedAt().format(formatter);
                System.out.println("  Timestamp: " + formattedTimestamp);
            }

        } else {
            System.out.println("❌ RELOCATION FAILED!");
            System.out.println("=================================================================");
            System.out.println("Possible reasons:");
            System.out.println("  - Lot already in target bay");
            System.out.println("  - Target bay has no capacity");
            System.out.println("  - Target bay contains different SKU");
            System.out.println("  - Validation errors");
        }

        System.out.println("\n=================================================================");
        System.out.println("=== TEST COMPLETED ===");
        System.out.println("=================================================================\n");

        //MainMenu.display();
    }
}