package app.view;

import app.model.InventoryBalance;
import app.model.InventoryLot;

import java.util.List;
import java.util.Scanner;

/**
 * View class responsible for handling the inventory dispatch user interface.
 * This class provides a console-based interface for users to dispatch stock
 * from inventory using FEFO/FIFO rules.
 */
public class InventoryDispatch {

    /**
     * Displays the inventory dispatch interface and handles user interaction.
     * Prompts user for SKU and quantity, shows planned allocation preview,
     * and executes dispatch operation.
     *
     * @param scanner the Scanner object used for reading user input
     */
    public static void display(Scanner scanner) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("              INVENTORY DISPATCH (FEFO/FIFO)");
        System.out.println("=".repeat(60));
        System.out.println("Type 'b' at any time to return to menu.\n");

        app.service.sprint1.InventoryDispatch dispatchService = new app.service.sprint1.InventoryDispatch();

        try {
            System.out.print("Enter SKU (e.g., SKU00001): ");
            String skuInput = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(skuInput)) return;

            System.out.print("Enter quantity to dispatch: ");
            String qtyInput = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(qtyInput)) return;

            Integer skuNum = Integer.parseInt(skuInput.substring(3)); // SKU00001 -> 1
            Integer quantity = Integer.parseInt(qtyInput);

            boolean hasStock = showAllocationPreview(dispatchService, skuNum, quantity, skuInput);

            if (!hasStock) {
                System.out.println("\nCannot proceed - no stock available for this SKU.");
                System.out.println("Press Enter to return to main menu...");
                scanner.nextLine();
                return;
            }

            System.out.print("\nProceed with dispatch? (y/n): ");
            String confirm = scanner.nextLine().trim();
            if ("b".equalsIgnoreCase(confirm)) return;

            if (!"y".equalsIgnoreCase(confirm)) {
                System.out.println("Dispatch cancelled.");
                return;
            }

            boolean success = dispatchService.dispatchStock(skuNum, quantity);

            System.out.println("\n" + "=".repeat(50));
            System.out.println("           DISPATCH RESULT");
            System.out.println("=".repeat(50));
            System.out.printf("SKU %s: %s%n", skuInput,
                    success ? "Dispatched successfully" : "Dispatch failed - insufficient stock");

            if (success) {
                showBaysAfterDispatch(dispatchService, skuNum, skuInput);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid number format.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("\nPress Enter to return to main menu...");
        scanner.nextLine();
    }

    /**
     * Shows allocation preview before dispatch confirmation.
     * @return true if stock is available, false otherwise
     */
    private static boolean showAllocationPreview(app.service.sprint1.InventoryDispatch dispatchService,
                                                 Integer skuNum, Integer quantity, String skuInput) {

        System.out.println("\n" + "=".repeat(50));
        System.out.println("           PLANNED ALLOCATION PREVIEW");
        System.out.println("=".repeat(50));
        System.out.printf("SKU %s (requested: %d):%n", skuInput, quantity);

        List<InventoryBalance> balances = dispatchService.getBalancesWithSku(skuNum);

        if (balances.isEmpty()) {
            System.out.println("   No stock available for this SKU");
            return false;
        }

        int totalAvailable = 0;
        int remaining = quantity;

        for (InventoryBalance balance : balances) {
            totalAvailable += balance.getQty();

            if (remaining > 0) {
                int balanceContribution = Math.min(balance.getQty(), remaining);
                int lotCount = calculateLotsNeeded(balance.getLots(), balanceContribution);

                System.out.printf("   Bay %s: %d units from %d lots%n",
                        ("(" + balance.getBay().getAisle().getAisleNo() + "-" +balance.getBay().getBayNo() + ")"), balanceContribution, lotCount);

                remaining -= balanceContribution;
            }
        }

        if (totalAvailable < quantity) {
            System.out.printf("   Partial fulfillment: %d/%d units available%n",
                    totalAvailable, quantity);
        } else {
            System.out.printf("   Full allocation: %d units%n", quantity);
        }

        return true;
    }

    /**
     * Calculates how many lots would be needed to fulfill a quantity.
     */
    private static int calculateLotsNeeded(List<InventoryLot> lots, int quantityNeeded) {
        int remaining = quantityNeeded;
        int lotCount = 0;

        for (InventoryLot lot : lots) {
            if (remaining <= 0) break;
            lotCount++;
            remaining -= lot.getQty();
        }

        return lotCount;
    }

    /**
     * Shows the state of bays after dispatch operation.
     */
    private static void showBaysAfterDispatch(app.service.sprint1.InventoryDispatch dispatchService,
                                              Integer skuNum, String skuInput) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("           BAYS STATE AFTER DISPATCH");
        System.out.println("=".repeat(50));
        System.out.printf("SKU %s remaining stock:%n", skuInput);

        List<InventoryBalance> remainingBalances = dispatchService.getBalancesWithSku(skuNum);

        if (remainingBalances.isEmpty()) {
            System.out.println("   No remaining stock - all bays cleared for this SKU");
            return;
        }

        int totalRemaining = 0;
        for (InventoryBalance balance : remainingBalances) {
            totalRemaining += balance.getQty();

            System.out.printf("   Bay %d: %d units in %d lots%n",
                    balance.getBay().getBayId(),
                    balance.getQty(),
                    balance.getLots().size());
        }

        System.out.printf("   Total remaining: %d units across %d bays%n",
                totalRemaining, remainingBalances.size());
    }

}
