package app.controller;

import app.model.WarehouseAisle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing WarehouseAisle entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for WarehouseAisle objects.
 * This controller manages warehouse aisle data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WarehouseAisleController {
    private List<WarehouseAisle> warehouseAisles;

    /**
     * Default constructor that initializes the warehouse aisle list.
     */
    public WarehouseAisleController() {
        this.warehouseAisles = new ArrayList<>();
    }

    /**
     * Creates and adds a new warehouse aisle to the system.
     *
     * @param warehouseAisle the WarehouseAisle object to be created
     * @return the created WarehouseAisle object
     */
    public WarehouseAisle createWarehouseAisle(WarehouseAisle warehouseAisle) {
        if (warehouseAisle != null) {
            warehouseAisles.add(warehouseAisle);
        }
        return warehouseAisle;
    }

    /**
     * Retrieves all warehouse aisles from the system.
     *
     * @return a list of all WarehouseAisle objects
     */
    public List<WarehouseAisle> getAllWarehouseAisles() {
        return new ArrayList<>(warehouseAisles);
    }

    /**
     * Retrieves a warehouse aisle by its unique identifier.
     *
     * @param warehouseAisleId the unique identifier of the warehouse aisle
     * @return an Optional containing the WarehouseAisle if found, or empty if not found
     */
    public Optional<WarehouseAisle> getWarehouseAisleById(Integer warehouseAisleId) {
        return warehouseAisles.stream()
                .filter(warehouseAisle -> warehouseAisle.getWarehouseAisleId().equals(warehouseAisleId))
                .findFirst();
    }

    /**
     * Updates an existing warehouse aisle in the system.
     *
     * @param warehouseAisleId the unique identifier of the warehouse aisle to update
     * @param updatedWarehouseAisle the WarehouseAisle object with updated information
     * @return true if the warehouse aisle was updated successfully, false otherwise
     */
    public boolean updateWarehouseAisle(Integer warehouseAisleId, WarehouseAisle updatedWarehouseAisle) {
        Optional<WarehouseAisle> existingWarehouseAisle = getWarehouseAisleById(warehouseAisleId);
        if (existingWarehouseAisle.isPresent()) {
            int index = warehouseAisles.indexOf(existingWarehouseAisle.get());
            warehouseAisles.set(index, updatedWarehouseAisle);
            return true;
        }
        return false;
    }

    /**
     * Deletes a warehouse aisle from the system.
     *
     * @param warehouseAisleId the unique identifier of the warehouse aisle to delete
     * @return true if the warehouse aisle was deleted successfully, false otherwise
     */
    public boolean deleteWarehouseAisle(Integer warehouseAisleId) {
        return warehouseAisles.removeIf(warehouseAisle -> warehouseAisle.getWarehouseAisleId().equals(warehouseAisleId));
    }
}

