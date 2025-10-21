package app.controller;

import app.model.InventoryBalance;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing InventoryBalance entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for InventoryBalance objects.
 * This controller manages inventory balance data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class InventoryBalanceController {
    private List<InventoryBalance> inventoryBalances;

    /**
     * Default constructor that initializes the inventory balance list.
     */
    public InventoryBalanceController() {
        this.inventoryBalances = new ArrayList<>();
    }

    /**
     * Creates and adds a new inventory balance to the system.
     *
     * @param inventoryBalance the InventoryBalance object to be created
     * @return the created InventoryBalance object
     */
    public InventoryBalance createInventoryBalance(InventoryBalance inventoryBalance) {
        if (inventoryBalance != null) {
            inventoryBalances.add(inventoryBalance);
        }
        return inventoryBalance;
    }

    /**
     * Retrieves all inventory balances from the system.
     *
     * @return a list of all InventoryBalance objects
     */
    public List<InventoryBalance> getAllInventoryBalances() {
        return new ArrayList<>(inventoryBalances);
    }

    /**
     * Retrieves an inventory balance by its unique identifier.
     *
     * @param balanceId the unique identifier of the inventory balance
     * @return an Optional containing the InventoryBalance if found, or empty if not found
     */
    public Optional<InventoryBalance> getInventoryBalanceById(Integer balanceId) {
        return inventoryBalances.stream()
                .filter(inventoryBalance -> inventoryBalance.getBalanceId().equals(balanceId))
                .findFirst();
    }

    /**
     * Updates an existing inventory balance in the system.
     *
     * @param balanceId the unique identifier of the inventory balance to update
     * @param updatedInventoryBalance the InventoryBalance object with updated information
     * @return true if the inventory balance was updated successfully, false otherwise
     */
    public boolean updateInventoryBalance(Integer balanceId, InventoryBalance updatedInventoryBalance) {
        Optional<InventoryBalance> existingInventoryBalance = getInventoryBalanceById(balanceId);
        if (existingInventoryBalance.isPresent()) {
            int index = inventoryBalances.indexOf(existingInventoryBalance.get());
            inventoryBalances.set(index, updatedInventoryBalance);
            return true;
        }
        return false;
    }

    /**
     * Deletes an inventory balance from the system.
     *
     * @param balanceId the unique identifier of the inventory balance to delete
     * @return true if the inventory balance was deleted successfully, false otherwise
     */
    public boolean deleteInventoryBalance(Integer balanceId) {
        return inventoryBalances.removeIf(inventoryBalance -> inventoryBalance.getBalanceId().equals(balanceId));
    }
}
