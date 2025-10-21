package app.repositories;

import app.model.InventoryBalance;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing inventory balances in the freight management system.
 * This class provides methods to add, retrieve, and manage inventory balance entities.
 * It maintains a collection of all inventory balances and allows searching by balance ID.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class InventoryBalanceRepository {

    private final List<InventoryBalance> inventoryBalances = new ArrayList<>();

    /**
     * Adds a new inventory balance to the repository if it doesn't already exist.
     *
     * @param inventoryBalance the inventory balance to add
     */
    public void addInventoryBalance(InventoryBalance inventoryBalance) {
        if (inventoryBalance != null && inventoryBalance.getBalanceId() != null) {
            if (!containsInventoryBalance(inventoryBalance.getBalanceId())) {
                inventoryBalances.add(inventoryBalance);
            }
        }
    }

    /**
     * Retrieves all inventory balances in the repository.
     *
     * @return a new list containing all inventory balances
     */
    public List<InventoryBalance> getAllInventoryBalances() {
        return new ArrayList<>(inventoryBalances);
    }

    /**
     * Retrieves a specific inventory balance by its ID.
     *
     * @param balanceId the inventory balance identifier
     * @return the matching inventory balance, or null if not found
     */
    public InventoryBalance getInventoryBalanceById(Integer balanceId) {
        for (InventoryBalance balance : inventoryBalances) {
            if (balanceId.equals(balance.getBalanceId())) {
                return balance;
            }
        }
        return null;
    }

    /**
     * Updates an existing inventory balance in the repository.
     *
     * @param balanceId the inventory balance identifier
     * @param updatedInventoryBalance the updated inventory balance
     * @return true if the inventory balance was updated successfully, false otherwise
     */
    public boolean updateInventoryBalance(Integer balanceId, InventoryBalance updatedInventoryBalance) {
        InventoryBalance existingBalance = getInventoryBalanceById(balanceId);
        if (existingBalance != null) {
            int index = inventoryBalances.indexOf(existingBalance);
            inventoryBalances.set(index, updatedInventoryBalance);
            return true;
        }
        return false;
    }

    /**
     * Deletes an inventory balance from the repository.
     *
     * @param balanceId the inventory balance identifier
     * @return true if the inventory balance was deleted successfully, false otherwise
     */
    public boolean deleteInventoryBalance(Integer balanceId) {
        return inventoryBalances.removeIf(balance -> balanceId.equals(balance.getBalanceId()));
    }

    /**
     * Gets the next available inventory balance ID.
     * Finds the maximum balance ID currently in use and returns the next sequential ID.
     *
     * @return the next available inventory balance ID (max current ID + 1)
     */
    public Integer getNextBalanceId() {
        int maxId = 0;
        for (InventoryBalance balance : inventoryBalances) {
            if (balance.getBalanceId() != null && balance.getBalanceId() > maxId) {
                maxId = balance.getBalanceId();
            }
        }
        return maxId + 1;
    }

    /**
     * Checks if an inventory balance exists in the repository by its ID.
     *
     * @param balanceId the inventory balance identifier
     * @return true if the inventory balance exists, false otherwise
     */
    private boolean containsInventoryBalance(Integer balanceId) {
        return getInventoryBalanceById(balanceId) != null;
    }

    /**
     * Clears all inventory balances from the repository.
     */
    public void clear() {
        inventoryBalances.clear();
    }
}

