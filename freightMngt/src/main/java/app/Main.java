package app;

import app.controller.InventoryBalanceController;
import app.model.InventoryBalance;
import app.repositories.*;
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

        //MainMenu.display();
    }
}