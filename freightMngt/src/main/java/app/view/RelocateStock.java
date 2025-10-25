package app.view;

import app.model.InventoryBalance;
import app.model.InventoryLot;
import app.model.WarehouseBay;
import app.repositories.Repositories;
import app.service.InventoryRelocation;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * View class responsible for handling the inventory relocation user interface.
 * This class provides a console-based interface for users to relocate inventory lots
 * between warehouse bays with proper validation and feedback.
 * <p>
 * Features include:
 * - Display of available inventory lots with details
 * - Display of available warehouse bays with capacity information
 * - Input validation for lot ID, bay ID, and relocation reason
 * - Relocation execution with success/failure feedback
 * - Error handling with user-friendly messages
 * - Option to return to main menu at any time
 */
public class RelocateStock {

    /**
     * Displays the inventory relocation interface and handles user interaction.
     * This method provides a complete workflow for relocating inventory lots:
     * <p>
     * 1. Shows available inventory lots with their current locations
     * 2. Shows available warehouse bays with capacity information
     * 3. Prompts user for lot ID, target bay ID, and relocation reason
     * 4. Validates inputs and executes the relocation
     * 5. Displays success or failure messages with appropriate details
     * 6. Handles various error conditions with appropriate messages
     * <p>
     * The user can type 'b' at any input prompt to return to the main menu.
     *
     * @param scanner the Scanner object used for reading user input
     */
    public static void display(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                RELOCATE INVENTORY STOCK");
        System.out.println("=".repeat(60));
        System.out.println("Type 'b' at any time to return to menu.\n");

        Repositories repositories = Repositories.getInstance();
        InventoryRelocation relocationService = new InventoryRelocation();

        displayInventoryLots(repositories);

        try {
            System.out.print("\nEnter lot ID to relocate: ");
            String lotInput = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(lotInput)) return;
            int lotId = Integer.parseInt(lotInput);

            displayWarehouseBays(repositories);

            System.out.print("\nEnter target bay ID: ");
            String bayInput = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(bayInput)) return;
            int targetBayId = Integer.parseInt(bayInput);

            System.out.print("Enter relocation reason: ");
            String reason = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(reason)) return;

            if (reason.isEmpty()) {
                System.out.println("Error: Relocation reason cannot be empty.");
                return;
            }

            boolean success = relocationService.relocateLot(lotId, targetBayId, reason);

            System.out.println("\n" + "=".repeat(60));
            if (success) {
                System.out.println("              RELOCATION SUCCESSFUL");
                System.out.println("=".repeat(60));
                System.out.printf("Lot ID %d has been successfully relocated to Bay ID %d%n", lotId, targetBayId);
                System.out.printf("Reason: %s%n", reason);
                System.out.println("An audit trail record has been created.");
            } else {
                System.out.println("              RELOCATION FAILED");
                System.out.println("=".repeat(60));
                System.out.println("The relocation could not be completed. Possible reasons:");
                System.out.println("• Lot ID does not exist");
                System.out.println("• Target bay ID does not exist");
                System.out.println("• Lot is already in the target bay");
                System.out.println("• Target bay has no capacity for this lot");
                System.out.println("• Target bay is not compatible with the lot's SKU");
            }
            System.out.println("=".repeat(60));

        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numeric IDs.");
        } catch (Exception e) {
            System.out.println("Unexpected error: " + e.getMessage());
        }

        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }

    /**
     * Displays all available inventory lots with their details and current locations.
     *
     * @param repositories the repositories instance for data access
     */
    private static void displayInventoryLots(Repositories repositories) {
        System.out.println("AVAILABLE INVENTORY LOTS:");
        System.out.println("-".repeat(80));
        System.out.printf("%-6s %-8s %-8s %-12s %-15s %-15s%n",
                "Lot ID", "SKU", "Qty", "Exp Date", "Received Date", "Current Bay");
        System.out.println("-".repeat(80));

        List<InventoryBalance> balances = repositories.getInventoryBalanceRepository().getAllInventoryBalances();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        if (balances != null && !balances.isEmpty()) {
            balances.stream()
                    .filter(balance -> balance.getLots() != null && !balance.getLots().isEmpty())
                    .flatMap(balance -> balance.getLots().stream()
                            .map(lot -> new LotWithBay(lot, balance.getBay())))
                    .sorted(Comparator.comparing(lwb -> lwb.lot.getLotId()))
                    .forEach(lwb -> {
                        InventoryLot lot = lwb.lot;
                        WarehouseBay bay = lwb.bay;
                        System.out.printf("%-6d %-8d %-8d %-12s %-15s Bay %-10d%n",
                                lot.getLotId(),
                                lot.getSku(),
                                lot.getQty(),
                                lot.getExpDate() != null ? lot.getExpDate().format(formatter) : "No expiry",
                                lot.getReceivedDate() != null ? lot.getReceivedDate().format(formatter) : "Unknown",
                                bay != null ? bay.getBayId() : -1);
                    });
        } else {
            System.out.println("   (no inventory lots found)");
        }
    }

    /**
     * Displays all available warehouse bays with their capacity information.
     *
     * @param repositories the repositories instance for data access
     */
    private static void displayWarehouseBays(Repositories repositories) {
        System.out.println("\nAVAILABLE WAREHOUSE BAYS:");
        System.out.println("-".repeat(60));
        System.out.printf("%-8s %-15s %-12s %-15s%n",
                "Bay ID", "Aisle", "Bay No", "Capacity");
        System.out.println("-".repeat(60));

        List<WarehouseBay> bays = repositories.getWarehouseBayRepository().getAllWarehouseBays();
        List<InventoryBalance> balances = repositories.getInventoryBalanceRepository().getAllInventoryBalances();

        if (bays != null && !bays.isEmpty()) {
            bays.stream()
                    .sorted(Comparator.comparing(WarehouseBay::getBayId))
                    .forEach(bay -> {
                        // Calculate current occupancy
                        int currentLots = balances.stream()
                                .filter(balance -> balance.getBay() != null &&
                                       balance.getBay().getBayId().equals(bay.getBayId()))
                                .mapToInt(balance -> balance.getLots().size())
                                .sum();

                        System.out.printf("%-8d %-15s %-12d %d/%d lots%n",
                                bay.getBayId(),
                                bay.getAisle() != null ? bay.getAisle().getAisleId() : "Unknown",
                                bay.getBayNo(),
                                currentLots,
                                bay.getCapacityBox());
                    });
        } else {
            System.out.println("   (no warehouse bays found)");
        }
    }

    /**
     * Helper class to associate a lot with its current bay.
     */
    private static class LotWithBay {
        final InventoryLot lot;
        final WarehouseBay bay;

        LotWithBay(InventoryLot lot, WarehouseBay bay) {
            this.lot = lot;
            this.bay = bay;
        }
    }
}
