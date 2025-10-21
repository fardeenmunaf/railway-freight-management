package app.controller;

import app.model.InventoryLot;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing InventoryLot entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for InventoryLot objects.
 * This controller manages inventory lot data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class InventoryLotController {
    private List<InventoryLot> inventoryLots;

    /**
     * Default constructor that initializes the inventory lot list.
     */
    public InventoryLotController() {
        this.inventoryLots = new ArrayList<>();
    }

    /**
     * Creates and adds a new inventory lot to the system.
     *
     * @param inventoryLot the InventoryLot object to be created
     * @return the created InventoryLot object
     */
    public InventoryLot createInventoryLot(InventoryLot inventoryLot) {
        if (inventoryLot != null) {
            inventoryLots.add(inventoryLot);
        }
        return inventoryLot;
    }

    /**
     * Retrieves all inventory lots from the system.
     *
     * @return a list of all InventoryLot objects
     */
    public List<InventoryLot> getAllInventoryLots() {
        return new ArrayList<>(inventoryLots);
    }

    /**
     * Retrieves an inventory lot by its unique identifier.
     *
     * @param inventoryLotId the unique identifier of the inventory lot
     * @return an Optional containing the InventoryLot if found, or empty if not found
     */
    public Optional<InventoryLot> getInventoryLotById(Integer inventoryLotId) {
        return inventoryLots.stream()
                .filter(inventoryLot -> inventoryLot.getInventoryLotId().equals(inventoryLotId))
                .findFirst();
    }

    /**
     * Updates an existing inventory lot in the system.
     *
     * @param inventoryLotId the unique identifier of the inventory lot to update
     * @param updatedInventoryLot the InventoryLot object with updated information
     * @return true if the inventory lot was updated successfully, false otherwise
     */
    public boolean updateInventoryLot(Integer inventoryLotId, InventoryLot updatedInventoryLot) {
        Optional<InventoryLot> existingInventoryLot = getInventoryLotById(inventoryLotId);
        if (existingInventoryLot.isPresent()) {
            int index = inventoryLots.indexOf(existingInventoryLot.get());
            inventoryLots.set(index, updatedInventoryLot);
            return true;
        }
        return false;
    }

    /**
     * Deletes an inventory lot from the system.
     *
     * @param inventoryLotId the unique identifier of the inventory lot to delete
     * @return true if the inventory lot was deleted successfully, false otherwise
     */
    public boolean deleteInventoryLot(Integer inventoryLotId) {
        return inventoryLots.removeIf(inventoryLot -> inventoryLot.getInventoryLotId().equals(inventoryLotId));
    }
}

