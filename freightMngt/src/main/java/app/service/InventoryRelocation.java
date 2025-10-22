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
        System.out.println("[DEBUG] Starting relocation: Lot " + lotId + " to Bay " + targetBayId);

        // Validation 1: Check if lot exists
        InventoryLot lotToMove = lotController.getInventoryLotById(lotId);
        if (lotToMove == null) {
            System.out.println("[DEBUG] FAILED: Lot " + lotId + " does not exist");
            return false;
        }
        System.out.println("[DEBUG] ✓ Lot " + lotId + " found");

        // Validation 2: Check if target bay exists
        WarehouseBay targetBay = findBayById(targetBayId);
        if (targetBay == null) {
            System.out.println("[DEBUG] FAILED: Target bay " + targetBayId + " does not exist");
            return false;
        }
        System.out.println("[DEBUG] ✓ Target bay " + targetBayId + " found");

        // Validation 3: Find balance containing the lot
        InventoryBalance sourceBalance = findBalanceContainingLot(lotId);
        if (sourceBalance == null) {
            System.out.println("[DEBUG] FAILED: Lot " + lotId + " is not assigned to any balance");
            return false;
        }
        System.out.println("[DEBUG] ✓ Found source balance " + sourceBalance.getBalanceId() + " in Bay " + sourceBalance.getBay().getBayId());

        // Validation 4: Check if lot is already in target bay
        if (sourceBalance.getBay().getBayId().equals(targetBayId)) {
            System.out.println("[DEBUG] FAILED: Lot " + lotId + " is already in target bay " + targetBayId);
            return false;
        }
        System.out.println("[DEBUG] ✓ Lot is in different bay");

        // Validation 5: Check if target bay has capacity
        if (!hasCapacityForLot(targetBay)) {
            System.out.println("[DEBUG] FAILED: Target bay " + targetBayId + " has no capacity");
            return false;
        }
        System.out.println("[DEBUG] ✓ Target bay has capacity");

        // Validation 6: Check SKU compatibility
        Integer lotSku = lotToMove.getSku();
        if (!isBayCompatibleWithSku(targetBay, lotSku)) {
            System.out.println("[DEBUG] FAILED: Target bay " + targetBayId + " is not compatible with SKU " + lotSku);
            return false;
        }
        System.out.println("[DEBUG] ✓ SKU " + lotSku + " is compatible with target bay");

        System.out.println("[DEBUG] All validations passed. Proceeding with relocation...");

        // Store fromBay for audit trail
        WarehouseBay fromBay = sourceBalance.getBay();

        // ...existing code...

        // Step 1: Remove lot from source balance
        List<InventoryLot> sourceLots = new ArrayList<>(sourceBalance.getLots());
        sourceLots.remove(lotToMove);

        // Recalculate source balance quantity
        Integer newSourceQty = 0;
        for (InventoryLot lot : sourceLots) {
            newSourceQty += lot.getBoxCode();
        }
        sourceBalance.setQty(newSourceQty);

        // Update or delete source balance
        if (sourceLots.isEmpty()) {
            balanceController.deleteInventoryBalance(sourceBalance.getBalanceId());
        } else {
            sourceBalance.setLots(sourceLots);
            balanceController.updateInventoryBalance(sourceBalance.getBalanceId(), sourceBalance);
        }

        // Step 2: Add lot to target balance
        InventoryBalance targetBalance = findBalanceByBay(targetBayId);

        if (targetBalance != null) {
            // Target bay has existing balance - add lot to it
            List<InventoryLot> targetLots = new ArrayList<>(targetBalance.getLots());
            targetLots.add(lotToMove);

            Integer newTargetQty = targetBalance.getQty() + lotToMove.getBoxCode();
            targetBalance.setLots(targetLots);
            targetBalance.setQty(newTargetQty);

            // Controller automatically sorts lots in FEFO/FIFO order
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
                lotToMove.getBoxCode()
            );

            balanceController.createInventoryBalance(newBalance);
        }

        // Step 3: Create audit trail (InventoryMove)
        Integer moveId = moveController.getNextInventoryMoveId();
        InventoryMove inventoryMove = new InventoryMove(moveId, lotToMove, fromBay, targetBay, lotToMove.getBoxCode(), reason, LocalDateTime.now());

        moveController.createInventoryMove(inventoryMove);

        System.out.println("Relocation successful: Lot " + lotId + " moved from Bay " + fromBay.getBayId() + " to Bay " + targetBayId);
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
     * Checks if a bay has capacity for one more lot.
     *
     * @param bay the warehouse bay to check
     * @return true if there is capacity, false otherwise
     */
    private boolean hasCapacityForLot(WarehouseBay bay) {
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        Integer totalLotsOccupied = 0;

        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(bay.getBayId())) {
                totalLotsOccupied += balance.getLots().size();
            }
        }

        return (bay.getCapacityBox() - totalLotsOccupied) > 0;
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
}
