package app.controller;

import app.model.InventoryLot;
import app.repositories.InventoryLotRepository;
import app.repositories.Repositories;

import java.util.List;

/**
 * Controller class for managing InventoryLot entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for InventoryLot objects.
 * This controller manages inventory lot data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class InventoryLotController {
    private final InventoryLotRepository repository;

    /**
     * Default constructor that initializes the controller with the inventory lot repository.
     */
    public InventoryLotController() {
        this.repository = Repositories.getInstance().getInventoryLotRepository();
    }

    /**
     * Creates and adds a new inventory lot to the system.
     *
     * @param inventoryLot the InventoryLot object to be created
     * @return the created InventoryLot object
     */
    public InventoryLot createInventoryLot(InventoryLot inventoryLot) {
        if (inventoryLot != null) {
            repository.addInventoryLot(inventoryLot);
        }
        return inventoryLot;
    }

    /**
     * Retrieves all inventory lots from the system.
     *
     * @return a list of all InventoryLot objects
     */
    public List<InventoryLot> getAllInventoryLots() {
        return repository.getAllInventoryLots();
    }

    /**
     * Retrieves an inventory lot by its unique identifier.
     *
     * @param lotId the unique identifier of the inventory lot
     * @return the InventoryLot if found, or null if not found
     */
    public InventoryLot getInventoryLotById(Integer lotId) {
        return repository.getInventoryLotById(lotId);
    }

    /**
     * Updates an existing inventory lot in the system.
     *
     * @param lotId the unique identifier of the inventory lot to update
     * @param updatedInventoryLot the InventoryLot object with updated information
     * @return true if the inventory lot was updated successfully, false otherwise
     */
    public boolean updateInventoryLot(Integer lotId, InventoryLot updatedInventoryLot) {
        if (updatedInventoryLot == null) {
            return false;
        }
        return repository.updateInventoryLot(lotId, updatedInventoryLot);
    }

    /**
     * Deletes an inventory lot from the system.
     *
     * @param lotId the unique identifier of the inventory lot to delete
     * @return true if the inventory lot was deleted successfully, false otherwise
     */
    public boolean deleteInventoryLot(Integer lotId) {
        return repository.deleteInventoryLot(lotId);
    }
}
