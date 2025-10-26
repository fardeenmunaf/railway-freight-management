package app.view;

import app.controller.InventoryBalanceController;
import app.model.InventoryBalance;
import app.model.InventoryLot;
import app.model.WarehouseBay;
import app.repositories.Repositories;
import app.repositories.WarehouseBayRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * View class for displaying bay content overview.
 * Shows the occupancy status of all warehouse bays with their contents.
 */
public class BayContentOverview {

    /**
     * Displays the bay content overview interface.
     *
     * @param scanner Scanner object for user input
     */
    public static void display(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    BAY CONTENT OVERVIEW");
        System.out.println("=".repeat(60));

        // Get repositories and controllers
        WarehouseBayRepository warehouseBayRepository = Repositories.getInstance().getWarehouseBayRepository();
        InventoryBalanceController balanceController = new InventoryBalanceController();

        List<WarehouseBay> allBays = warehouseBayRepository.getAllWarehouseBays();
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();

        int totalBays = allBays.size();
        int occupiedBays = 0;

        for (WarehouseBay bay : allBays) {
            System.out.println("\nBAY " + bay.getBayId() + " (Aisle: " +
                    (bay.getAisle() != null ? bay.getAisle().getAisleNo() : "N/A") +
                    " | BayNo: " + bay.getBayNo() +
                    " | Capacity: " + bay.getCapacityBox() + ")");

            // gather balances that belong to this bay (compare bayId if available)
            List<InventoryBalance> balancesInBay = new ArrayList<>();
            for (InventoryBalance balance : allBalances) {
                if (balance.getBay() != null && balance.getBay().getBayId() != null && bay.getBayId() != null) {
                    if (balance.getBay().getBayId().equals(bay.getBayId())) {
                        balancesInBay.add(balance);
                    }
                }
            }

            if (balancesInBay.isEmpty()) {
                System.out.println("   [EMPTY]");
            } else {
                occupiedBays++;
                for (InventoryBalance balance : balancesInBay) {
                    Integer sku = (balance.getLots() == null || balance.getLots().isEmpty())
                            ? null
                            : balance.getLots().get(0).getSku();

                    System.out.println("   Balance ID: " + balance.getBalanceId() +
                            " | SKU: " + sku +
                            " | Qty: " + balance.getQty() +
                            " | Lots: " + (balance.getLots() == null ? 0 : balance.getLots().size()));

                    int lotIndex = 1;
                    if (balance.getLots() != null) {
                        for (InventoryLot lot : balance.getLots()) {
                            String expDateStr = (lot.getExpDate() != null) ? lot.getExpDate().toString() : "NO EXPIRY";
                            String receivedStr = (lot.getReceivedDate() != null) ? lot.getReceivedDate().toString() : "UNKNOWN";
                            System.out.println("      Lot " + lotIndex + ": " +
                                    "ID=" + lot.getLotId() +
                                    " | LotId=" + lot.getLotId() +
                                    " | Qty=" + lot.getQty() +
                                    " | ExpDate=" + expDateStr +
                                    " | ReceivedAt=" + receivedStr);
                            lotIndex++;
                        }
                    }
                }
            }
        }

        System.out.println("\n=== SUMMARY ===");
        System.out.println("Total Bays: " + totalBays);
        System.out.println("Occupied Bays: " + occupiedBays);
        System.out.println("Empty Bays: " + (totalBays - occupiedBays));
        System.out.println("Occupancy Rate: " + String.format("%.1f", (double) occupiedBays / totalBays * 100) + "%");

        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }
}
