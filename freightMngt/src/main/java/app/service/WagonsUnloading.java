package app.service;

import app.controller.InventoryBalanceController;
import app.controller.WarehouseBayController;
import app.model.InventoryBalance;
import app.model.InventoryLot;
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
     * Calculates the available capacity of a warehouse bay in terms of boxes/lots.
     * The available capacity is determined by subtracting the number of lots (boxes)
     * currently allocated to the bay from the bay's maximum box capacity.
     *
     * @param bay the warehouse bay to check
     * @return the available capacity in boxes/lots
     */
    public Integer getAvailableCapacity(WarehouseBay bay) {
        List<InventoryBalance> allBalances = balanceController.getAllInventoryBalances();
        Integer totalLotsOccupied = 0;

        for (InventoryBalance balance : allBalances) {
            if (balance.getBay() != null && balance.getBay().getBayId().equals(bay.getBayId())) {
                totalLotsOccupied += balance.getLots().size();
            }
        }

        return bay.getCapacityBox() - totalLotsOccupied;
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
     * Allocates a list of lots to a warehouse bay.
     * Creates a new inventory balance with positive ID.
     *
     * @param lots the list of lots to allocate
     * @param bay the warehouse bay to allocate to
     * @param qty the total quantity of items in all lots
     */
    public void allocateToBay(List<InventoryLot> lots, WarehouseBay bay, Integer qty) {
        Integer newBalanceId = balanceController.getNextBalanceId();

        InventoryBalance newBalance = new InventoryBalance(newBalanceId, bay, lots, qty);

        balanceController.createInventoryBalance(newBalance);
    }

    /**
     * Processes an inventory balance by allocating its lots to warehouse bays.
     * Each lot counts as 1 box regardless of the quantity of items inside.
     * Tries to fit as many lots as possible in each bay based on box capacity.
     *
     * @param balance the inventory balance to process and allocate
     */
    public void processBalance(InventoryBalance balance) {
        if (balance.getLots().isEmpty()) {
            return;
        }

        Integer sku = balance.getLots().get(0).getSku();
        List<InventoryLot> remainingLots = new ArrayList<>(balance.getLots());

        while (!remainingLots.isEmpty()) {
            WarehouseBay suitableBay = findSuitableBay(sku);

            if (suitableBay == null) {
                break;
            }

            Integer availableBoxCapacity = getAvailableCapacity(suitableBay);
            List<InventoryLot> lotsForThisBay = new ArrayList<>();

            for (int i = 0; i < Math.min(remainingLots.size(), availableBoxCapacity); i++) {
                lotsForThisBay.add(remainingLots.get(i));
            }

            if (!lotsForThisBay.isEmpty()) {
                Integer totalQty = 0;
                for (InventoryLot lot : lotsForThisBay) {
                    totalQty += lot.getQty();
                }
                allocateToBay(lotsForThisBay, suitableBay, totalQty);
                remainingLots.removeAll(lotsForThisBay);
            } else {
                break;
            }
        }

        if (remainingLots.isEmpty()) {
            balanceController.deleteInventoryBalance(balance.getBalanceId());
        } else {
            balance.setLots(remainingLots);
            Integer newQty = 0;
            for (InventoryLot lot : remainingLots) {
                newQty += lot.getQty();
            }
            balance.setQty(newQty);
            balanceController.updateInventoryBalance(balance.getBalanceId(), balance);
        }
    }

}