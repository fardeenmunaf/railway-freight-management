package app.service;

import app.controller.InventoryBalanceController;
import app.controller.WarehouseBayController;
import app.model.InventoryBalance;
import app.model.WarehouseBay;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for unloading wagons and allocating inventory to warehouse bays.
 * This service manages the process of distributing unassigned inventory balances across
 * available warehouse bays, respecting capacity constraints and SKU segregation rules.
 */
public class WagonsUnloading {

    private final InventoryBalanceController balanceController;
    private final WarehouseBayController bayController;

    /**
     * Constructs a new WagonsUnloading service.
     * Initializes the required controllers for managing inventory balances and warehouse bays.
     */
    public WagonsUnloading() {
        this.balanceController = new InventoryBalanceController();
        this.bayController = new WarehouseBayController();
    }

    /**
     * Processes and unloads all wagons by allocating unassigned inventory balances to warehouse bays.
     * This method retrieves all inventory balances that are not yet assigned to a bay and processes
     * each one, attempting to find suitable bays for allocation.
     */
    public void unloadAllWagons() {
        List<InventoryBalance> unassignedBalances = getUnassignedBalances();

        for (InventoryBalance balance : unassignedBalances) {
            processBalance(balance);
        }
    }

    /**
     * Retrieves all inventory balances that have not been assigned to a warehouse bay.
     * An unassigned balance is identified by having a null bay reference.
     *
     * @return a list of InventoryBalance objects that are not assigned to any bay
     */
    public List<InventoryBalance> getUnassignedBalances() {
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        List<InventoryBalance> unassigned = new ArrayList<>();

        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() == null) {
                unassigned.add(balance);
            }
        }

        return unassigned;
    }

    /**
     * Retrieves all warehouse bays available in the system.
     *
     * @return a list of all WarehouseBay objects
     */
    public List<WarehouseBay> getAllBays() {
        return bayController.getAllWarehouseBays();
    }

    /**
     * Calculates the available capacity of a warehouse bay.
     * The available capacity is determined by subtracting the total quantity of inventory
     * currently allocated to the bay from the bay's maximum capacity.
     *
     * @param bay the warehouse bay to check
     * @return the available capacity in boxes, or the full capacity if the bay is empty
     */
    public Integer getAvailableCapacity(WarehouseBay bay) {
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        Integer totalOccupied = 0;

        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(bay.getBayId())) {
                totalOccupied += balance.getQty();
            }
        }

        return bay.getCapacityBox() - totalOccupied;
    }

    /**
     * Determines if a warehouse bay is suitable for storing inventory of a given SKU.
     * A bay is considered suitable if:
     * - It is empty and has available capacity, OR
     * - It already contains the same SKU and has available capacity
     * A bay cannot store multiple different SKUs (SKU segregation rule).
     *
     * @param bay the warehouse bay to evaluate
     * @param sku the SKU identifier to check compatibility with
     * @return true if the bay can accommodate the SKU, false otherwise
     */
    public boolean isBaySuitable(WarehouseBay bay, Integer sku) {
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        Integer existingSku = null;

        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(bay.getBayId())) {
                if (!balance.getLots().isEmpty()) {
                    existingSku = balance.getLots().get(0).getSku();
                    break;
                }
            }
        }

        if (existingSku == null) {
            return getAvailableCapacity(bay) > 0;
        }

        if (!existingSku.equals(sku)) {
            return false;
        }

        return getAvailableCapacity(bay) > 0;
    }

    /**
     * Finds the first suitable warehouse bay for storing inventory of a given SKU.
     * Iterates through all available warehouse bays and returns the first bay that
     * meets the suitability criteria (compatible SKU and sufficient capacity).
     *
     * @param sku the SKU identifier to find storage for
     * @return a suitable WarehouseBay if found, null if no suitable bay exists
     */
    public WarehouseBay findSuitableBay(Integer sku) {
        List<WarehouseBay> allBays = getAllBays();

        for (WarehouseBay bay : allBays) {
            if (isBaySuitable(bay, sku)) {
                return bay;
            }
        }

        return null;
    }

    /**
     * Allocates a specific quantity of inventory to a warehouse bay.
     * Creates a new inventory balance record with the specified bay, lot, and quantity.
     * The new balance is assigned a unique identifier and persisted through the controller.
     *
     * @param balance the original inventory balance being allocated
     * @param bay the warehouse bay to allocate the inventory to
     * @param qty the quantity of inventory to allocate to this bay
     */
    public void allocateToBay(InventoryBalance balance, WarehouseBay bay, Integer qty) {
        Integer newBalanceId = balanceController.getNextBalanceId();

        InventoryBalance newBalance = new InventoryBalance(
                newBalanceId,
                bay,
                balance.getLots(),
                qty
        );

        balanceController.createInventoryBalance(newBalance);
    }

    /**
     * Processes an inventory balance by allocating it to one or more warehouse bays.
     * This method attempts to distribute the entire quantity across suitable bays:
     * - Finds suitable bays that can accommodate the SKU
     * - Allocates as much quantity as possible to each bay
     * - Continues until the entire quantity is allocated or no suitable bays remain
     * - Deletes the original unassigned balance if fully allocated
     *
     * @param balance the inventory balance to process and allocate
     */
    public void processBalance(InventoryBalance balance) {
        Integer remainingQty = balance.getQty();
        Integer sku = null;

        if (!balance.getLots().isEmpty()) {
            sku = balance.getLots().get(0).getSku();
        }

        if (sku == null) {
            return;
        }

        while (remainingQty > 0) {
            WarehouseBay suitableBay = findSuitableBay(sku);

            if (suitableBay == null) {
                break;
            }

            Integer availableCapacity = getAvailableCapacity(suitableBay);
            Integer qtyToAllocate = Math.min(remainingQty, availableCapacity);

            allocateToBay(balance, suitableBay, qtyToAllocate);

            remainingQty -= qtyToAllocate;
        }

        if (remainingQty == 0) {
            balanceController.deleteInventoryBalance(balance.getBalanceId());
        }
    }

}