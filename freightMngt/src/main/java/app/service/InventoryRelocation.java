package app.service;

import app.controller.InventoryBalanceController;
import app.controller.InventoryLotController;
import app.controller.InventoryMoveController;
import app.controller.WarehouseBayController;
import app.model.InventoryBalance;
import app.model.InventoryLot;
import app.model.InventoryMove;
import app.model.WarehouseBay;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for relocating inventory lots between warehouse bays.
 * This service manages the relocation operation, including validations, balance updates,
 * and creation of audit trail records (InventoryMove).
 */
public class InventoryRelocation {

    private final InventoryBalanceController balanceController;
    private final InventoryMoveController moveController;
    private final InventoryLotController lotController;
    private final WarehouseBayController bayController;

    /**
     * Constructs a new InventoryRelocation service.
     * Initializes the required controllers for managing relocations.
     */
    public InventoryRelocation() {
        this.balanceController = new InventoryBalanceController();
        this.moveController = new InventoryMoveController();
        this.lotController = new InventoryLotController();
        this.bayController = new WarehouseBayController();
    }

    /**
     * Relocates a lot from its current bay to a target bay.
     * This method validates the operation, updates balances, and creates an audit record.
     *
     * @param lotId the ID of the lot to relocate
     * @param targetBayId the ID of the destination bay
     * @param reason the reason for the relocation
     * @return true if relocation was successful, false otherwise
     */
    public boolean relocateLot(Integer lotId, Integer targetBayId, String reason) {

        // Validation 1: Check if lot exists
        InventoryLot lotToMove = lotController.getInventoryLotById(lotId);
        if (lotToMove == null) {
            return false;
        }

        // Validation 2: Check if target bay exists
        WarehouseBay targetBay = findBayById(targetBayId);
        if (targetBay == null) {
            return false;
        }

        // Validation 3: Find balance containing the lot
        InventoryBalance sourceBalance = findBalanceContainingLot(lotId);
        if (sourceBalance == null) {
            return false;
        }

        // Validation 4: Check if lot is already in target bay
        if (sourceBalance.getBay().getBayId().equals(targetBayId)) {
            return false;
        }

        // Validation 5: Check if target bay has capacity for this specific lot
        if (!hasCapacityForLot(targetBay, lotToMove)) {
            return false;
        }

        // Validation 6: Check SKU compatibility
        Integer lotSku = lotToMove.getSku();
        if (!isBayCompatibleWithSku(targetBay, lotSku)) {
            return false;
        }

        // Store fromBay for audit trail
        WarehouseBay fromBay = sourceBalance.getBay();

        // Step 1: Remove lot from source balance
        List<InventoryLot> sourceLots = new ArrayList<>(sourceBalance.getLots());
        sourceLots.remove(lotToMove);

        // Recalculate source balance quantity
        Integer newSourceQty = 0;
        for (InventoryLot lot : sourceLots) {
            newSourceQty += lot.getQty();
        }
        sourceBalance.setQty(newSourceQty);

        // Update or delete source balance
        if (sourceLots.isEmpty()) {
            balanceController.deleteInventoryBalance(sourceBalance.getBalanceId());
        } else {
            sourceBalance.setLots(sourceLots);
        }

        // Step 2: Add lot to target balance
        InventoryBalance targetBalance = findBalanceByBay(targetBayId);

        if (targetBalance != null) {
            // Target bay has existing balance - insert lot in FEFO position
            List<InventoryLot> targetLots = new ArrayList<>(targetBalance.getLots());

            // Find correct FEFO insertion position without re-sorting existing lots
            int insertPosition = findFEFOInsertPosition(targetLots, lotToMove);
            targetLots.add(insertPosition, lotToMove);

            Integer newTargetQty = targetBalance.getQty() + lotToMove.getQty();
            targetBalance.setLots(targetLots);
            targetBalance.setQty(newTargetQty);

            // Update balance - lots are already in correct FEFO order
            balanceController.updateInventoryBalance(targetBalance.getBalanceId(), targetBalance);
        } else {
            // Target bay is empty - create new balance
            Integer newBalanceId = balanceController.getNextBalanceId();
            List<InventoryLot> newLots = new ArrayList<>();
            newLots.add(lotToMove);

            InventoryBalance newBalance = new InventoryBalance(
                newBalanceId,
                targetBay,
                newLots,
                lotToMove.getQty()
            );

            balanceController.createInventoryBalance(newBalance);
        }

        // Step 3: Create audit trail (InventoryMove)
        Integer moveId = moveController.getNextInventoryMoveId();
        InventoryMove inventoryMove = new InventoryMove(moveId, lotToMove, fromBay, targetBay, lotToMove.getQty(), reason, LocalDateTime.now());

        moveController.createInventoryMove(inventoryMove);

        return true;
    }

    /**
     * Finds the balance that contains a specific lot.
     *
     * @param lotId the ID of the lot to find
     * @return the InventoryBalance containing the lot, or null if not found
     */
    private InventoryBalance findBalanceContainingLot(Integer lotId) {
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();

        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null) {
                for (InventoryLot lot : balance.getLots()) {
                    if (lot.getLotId().equals(lotId)) {
                        return balance;
                    }
                }
            }
        }

        return null;
    }

    /**
     * Finds a balance associated with a specific bay.
     *
     * @param bayId the ID of the bay
     * @return the InventoryBalance in the bay, or null if bay is empty
     */
    private InventoryBalance findBalanceByBay(Integer bayId) {
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();

        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(bayId)) {
                return balance;
            }
        }

        return null;
    }

    /**
     * Checks if a bay has capacity for a specific lot.
     * Each lot occupies 1 space in the bay, regardless of its item quantity.
     *
     * @param bay the warehouse bay to check
     * @param lotToAdd the lot to be added to the bay
     * @return true if there is capacity for one more lot, false otherwise
     */
    private boolean hasCapacityForLot(WarehouseBay bay, InventoryLot lotToAdd) {
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        Integer totalLotsOccupied = 0;

        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(bay.getBayId())) {
                // Count total lots (each lot occupies 1 space)
                totalLotsOccupied += balance.getLots().size();
            }
        }

        // Check if adding one more lot would exceed capacity
        return (bay.getCapacityBox() - totalLotsOccupied) >= 1;
    }

    /**
     * Checks if a bay is compatible with a specific SKU.
     * A bay is compatible if it's empty or already contains the same SKU.
     *
     * @param bay the warehouse bay to check
     * @param sku the SKU to check compatibility with
     * @return true if compatible, false otherwise
     */
    private boolean isBayCompatibleWithSku(WarehouseBay bay, Integer sku) {
        InventoryBalance existingBalance = findBalanceByBay(bay.getBayId());

        // Bay is empty - compatible with any SKU
        if (existingBalance == null || existingBalance.getLots().isEmpty()) {
            return true;
        }

        // Bay has lots - check if SKU matches
        Integer existingSku = existingBalance.getLots().get(0).getSku();
        return sku.equals(existingSku);
    }

    /**
     * Finds a warehouse bay by its ID.
     *
     * @param bayId the ID of the bay to find
     * @return the WarehouseBay if found, or null if not found
     */
    private WarehouseBay findBayById(Integer bayId) {
        List<WarehouseBay> allBays = bayController.getAllWarehouseBays();

        for (WarehouseBay bay : allBays) {
            if (bay.getBayId().equals(bayId)) {
                return bay;
            }
        }

        return null;
    }

    /**
     * Finds the correct FEFO insertion position for a lot in an existing list.
     * This method maintains FEFO order without re-sorting existing lots.
     *
     * @param existingLots the current list of lots in the bay
     * @param newLot the lot to be inserted
     * @return the index where the new lot should be inserted
     */
    private int findFEFOInsertPosition(List<InventoryLot> existingLots, InventoryLot newLot) {
        for (int i = 0; i < existingLots.size(); i++) {
            InventoryLot currentLot = existingLots.get(i);

            // Handle null expiration dates
            // Lots with null expDate are considered "never expire" and should be at the end
            if (newLot.getExpDate() == null && currentLot.getExpDate() == null) {
                // Both null, compare by receivedDate (FIFO)
                if (newLot.getReceivedDate().isBefore(currentLot.getReceivedDate())) {
                    return i;
                }
                // If receivedDate is also equal, compare by lotId (tie-breaker)
                if (newLot.getReceivedDate().isEqual(currentLot.getReceivedDate()) &&
                    newLot.getLotId() < currentLot.getLotId()) {
                    return i;
                }
            } else if (newLot.getExpDate() == null) {
                // newLot has no expiry, should go after lots with expiry
                // Continue to next lot
                continue;
            } else if (currentLot.getExpDate() == null) {
                // currentLot has no expiry, newLot has expiry, so newLot goes before
                return i;
            } else {
                // Both have expiration dates, compare normally
                // Compare by expDate first (FEFO - First Expired First Out)
                if (newLot.getExpDate().isBefore(currentLot.getExpDate())) {
                    return i;
                }

                // If expDate is equal, compare by receivedDate (FIFO - First In First Out)
                if (newLot.getExpDate().isEqual(currentLot.getExpDate())) {
                    if (newLot.getReceivedDate().isBefore(currentLot.getReceivedDate())) {
                        return i;
                    }

                    // If receivedDate is also equal, compare by lotId (tie-breaker)
                    if (newLot.getReceivedDate().isEqual(currentLot.getReceivedDate()) &&
                        newLot.getLotId() < currentLot.getLotId()) {
                        return i;
                    }
                }
            }
        }

        // If newLot has latest expiry date or receivedDate, insert at the end
        return existingLots.size();
    }
}
