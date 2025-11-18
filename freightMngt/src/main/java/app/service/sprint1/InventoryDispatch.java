package app.service.sprint1;

import app.controller.InventoryBalanceController;
import app.model.InventoryBalance;
import app.model.InventoryLot;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for dispatching inventory from warehouse bays.
 * This service manages the dispatch operation following FEFO/FIFO rules,
 * consuming stock from the front of bay lists and supporting partial dispatch
 * across multiple balances.
 */
public class InventoryDispatch {

    private final InventoryBalanceController balanceController;

    /**
     * Constructs a new InventoryDispatch service.
     * Initializes the required controller for managing inventory balances.
     */
    public InventoryDispatch() {
        this.balanceController = new InventoryBalanceController();
    }

    /**
     * Dispatches a specified quantity of a SKU from warehouse inventory.
     * This method follows FEFO/FIFO rules by consuming lots from the front of each balance's list.
     * If insufficient stock is available, no dispatch occurs and the method returns false.
     *
     * @param sku the SKU identifier to dispatch
     * @param quantityToDispatch the total quantity of items to dispatch
     * @return true if the dispatch was successful, false if insufficient stock
     */
    public boolean dispatchStock(Integer sku, Integer quantityToDispatch) {
        // Get all balances with this SKU that are assigned to bays
        List<InventoryBalance> balancesWithSku = getBalancesWithSku(sku);

        if (balancesWithSku.isEmpty()) {
            return false;
        }

        // Calculate total available stock
        Integer totalAvailable = 0;
        for (InventoryBalance balance : balancesWithSku) {
            totalAvailable += balance.getQty();
        }

        // If insufficient stock, do not dispatch anything
        if (totalAvailable < quantityToDispatch) {
            return false;
        }

        // Proceed with dispatch
        Integer remainingToDispatch = quantityToDispatch;

        for (InventoryBalance balance : balancesWithSku) {
            if (remainingToDispatch <= 0) {
                break;
            }

            List<InventoryLot> lots = new ArrayList<>(balance.getLots());
            List<InventoryLot> lotsToRemove = new ArrayList<>();

            for (InventoryLot lot : lots) {
                if (remainingToDispatch <= 0) {
                    break;
                }

                Integer lotQty = lot.getQty();

                if (lotQty <= remainingToDispatch) {
                    // Consume entire lot
                    lotsToRemove.add(lot);
                    remainingToDispatch -= lotQty;
                } else {
                    // Consume partial lot
                    Integer newQty = lotQty - remainingToDispatch;
                    lot.setQty(newQty);
                    remainingToDispatch = 0;
                }
            }

            // Update balance
            if (!lotsToRemove.isEmpty()) {
                lots.removeAll(lotsToRemove);
                balance.setLots(lots);
            }

            // Recalculate total quantity
            Integer newTotalQty = 0;
            for (InventoryLot lot : balance.getLots()) {
                newTotalQty += lot.getQty();
            }
            balance.setQty(newTotalQty);

            // If balance has no more lots, delete it (bay remains but balance is removed)
            if (balance.getLots().isEmpty()) {
                balanceController.deleteInventoryBalance(balance.getBalanceId());
            } else {
                balanceController.updateInventoryBalance(balance.getBalanceId(), balance);
            }
        }

        return true;
    }

    /**
     * Retrieves all inventory balances that contain the specified SKU and are assigned to bays.
     * The balances are sorted by FEFO/FIFO rules to ensure correct dispatch priority:
     * 1. Balances with lots having expiry dates come first (sorted by earliest expiry - FEFO)
     * 2. Balances with lots without expiry dates come after (sorted by earliest received - FIFO)
     * 3. If dates are equal, sort by balanceId ASC (tie-breaker)
     *
     * @param sku the SKU identifier to search for
     * @return a list of InventoryBalance objects ordered by FEFO/FIFO rules
     */
    public List<InventoryBalance> getBalancesWithSku(Integer sku) {
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        List<InventoryBalance> result = new ArrayList<>();

        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && !balance.getLots().isEmpty()) {
                Integer balanceSku = balance.getLots().get(0).getSku();
                if (sku.equals(balanceSku)) {
                    result.add(balance);
                }
            }
        }

        // Sort by FEFO/FIFO rules using Comparator (more efficient than bubble sort)
        result.sort(this::compareBalances);

        return result;
    }

    /**
     * Compares two balances for sorting based on FEFO/FIFO rules.
     * Applies FEFO/FIFO rules based on the first lot in each balance.
     *
     * @param b1 the first balance to compare
     * @param b2 the second balance to compare
     * @return negative if b1 comes before b2, positive if b1 comes after b2, 0 if equal
     */
    private int compareBalances(InventoryBalance b1, InventoryBalance b2) {
        InventoryLot firstLot1 = b1.getLots().get(0);
        InventoryLot firstLot2 = b2.getLots().get(0);

        boolean b1HasExpiry = firstLot1.getExpDate() != null;
        boolean b2HasExpiry = firstLot2.getExpDate() != null;

        // Both have expiry dates - sort by expiry date ASC (FEFO - earliest first)
        if (b1HasExpiry && b2HasExpiry) {
            int expComparison = firstLot1.getExpDate().compareTo(firstLot2.getExpDate());
            if (expComparison != 0) {
                return expComparison;
            }
            // Tie-break by balanceId ASC
            return b1.getBalanceId().compareTo(b2.getBalanceId());
        }

        // Balances with expiry date come first
        if (b1HasExpiry) {
            return -1; // b1 comes before b2
        }

        if (b2HasExpiry) {
            return 1; // b2 comes before b1
        }

        // Both have no expiry date - sort by receivedDate ASC (FIFO - oldest first)
        int receivedComparison = firstLot1.getReceivedDate().compareTo(firstLot2.getReceivedDate());
        if (receivedComparison != 0) {
            return receivedComparison;
        }

        // Tie-break by balanceId ASC
        return b1.getBalanceId().compareTo(b2.getBalanceId());
    }
}
