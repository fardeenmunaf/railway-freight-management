package app.controller;

import app.model.InventoryBalance;
import app.repositories.InventoryBalanceRepository;
import app.repositories.Repositories;
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
    private final InventoryBalanceRepository repository;

    /**
     * Default constructor that initializes the controller with the inventory balance repository.
     */
    public InventoryBalanceController() {
        this.repository = Repositories.getInstance().getInventoryBalanceRepository();
    }

    /**
     * Creates and adds a new inventory balance to the system.
     *
     * @param inventoryBalance the InventoryBalance object to be created
     * @return the created InventoryBalance object
     */
    public InventoryBalance createInventoryBalance(InventoryBalance inventoryBalance) {
        if (inventoryBalance != null) {
            repository.addInventoryBalance(inventoryBalance);
        }
        return inventoryBalance;
    }

    /**
     * Retrieves all inventory balances from the system.
     *
     * @return a list of all InventoryBalance objects
     */
    public List<InventoryBalance> getAllInventoryBalances() {
        return repository.getAllInventoryBalances();
    }

    /**
     * Retrieves an inventory balance by its unique identifier.
     *
     * @param balanceId the unique identifier of the inventory balance
     * @return an Optional containing the InventoryBalance if found, or empty if not found
     */
    public Optional<InventoryBalance> getInventoryBalanceById(Integer balanceId) {
        InventoryBalance balance = repository.getInventoryBalanceById(balanceId);
        return Optional.ofNullable(balance);
    }

    /**
     * Updates an existing inventory balance in the system.
     *
     * @param balanceId the unique identifier of the inventory balance to update
     * @param updatedInventoryBalance the InventoryBalance object with updated information
     * @return true if the inventory balance was updated successfully, false otherwise
     */
    public boolean updateInventoryBalance(Integer balanceId, InventoryBalance updatedInventoryBalance) {
        return repository.updateInventoryBalance(balanceId, updatedInventoryBalance);
    }

    /**
     * Deletes an inventory balance from the system.
     *
     * @param balanceId the unique identifier of the inventory balance to delete
     * @return true if the inventory balance was deleted successfully, false otherwise
     */
    public boolean deleteInventoryBalance(Integer balanceId) {
        return repository.deleteInventoryBalance(balanceId);
    }

    /**
     * Gets the next available balance ID.
     *
     * @return the next balance ID to be used
     */
    public Integer getNextBalanceId() {
        return repository.getNextBalanceId();
    }

    /**
     * Clears all inventory balances from the system.
     */
    public void clearAllInventoryBalances() {
        repository.clear();
    }
}
