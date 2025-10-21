package app.controller;

import app.model.WarehouseAisle;
import app.repositories.WarehouseAisleRepository;
import app.repositories.Repositories;
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
    private final WarehouseAisleRepository repository;

    /**
     * Default constructor that initializes the controller with the warehouse aisle repository.
     */
    public WarehouseAisleController() {
        this.repository = Repositories.getInstance().getWarehouseAisleRepository();
    }

    /**
     * Creates and adds a new warehouse aisle to the system.
     *
     * @param warehouseAisle the WarehouseAisle object to be created
     * @return the created WarehouseAisle object
     */
    public WarehouseAisle createWarehouseAisle(WarehouseAisle warehouseAisle) {
        if (warehouseAisle != null) {
            repository.addWarehouseAisle(warehouseAisle);
        }
        return warehouseAisle;
    }

    /**
     * Retrieves all warehouse aisles from the system.
     *
     * @return a list of all WarehouseAisle objects
     */
    public List<WarehouseAisle> getAllWarehouseAisles() {
        return repository.getAllAisles();
    }

    /**
     * Retrieves a warehouse aisle by warehouse ID and aisle number.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number
     * @return an Optional containing the WarehouseAisle if found, or empty if not found
     */
    public Optional<WarehouseAisle> getWarehouseAisle(Integer warehouseId, Integer aisleNo) {
        WarehouseAisle warehouseAisle = repository.getAisle(warehouseId, aisleNo);
        return Optional.ofNullable(warehouseAisle);
    }

    /**
     * Updates an existing warehouse aisle in the system.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number
     * @param updatedWarehouseAisle the WarehouseAisle object with updated information
     * @return true if the warehouse aisle was updated successfully, false otherwise
     */
    public boolean updateWarehouseAisle(Integer warehouseId, Integer aisleNo, WarehouseAisle updatedWarehouseAisle) {
        return repository.updateWarehouseAisle(warehouseId, aisleNo, updatedWarehouseAisle);
    }

    /**
     * Deletes a warehouse aisle from the system.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number
     * @return true if the warehouse aisle was deleted successfully, false otherwise
     */
    public boolean deleteWarehouseAisle(Integer warehouseId, Integer aisleNo) {
        return repository.deleteWarehouseAisle(warehouseId, aisleNo);
    }

    /**
     * Gets the next available warehouse aisle number for a specific warehouse.
     *
     * @param warehouseId the warehouse identifier
     * @return the next warehouse aisle number to be used
     */
    public Integer getNextAisleNo(Integer warehouseId) {
        return repository.getNextAisleNo(warehouseId);
    }

    /**
     * Clears all warehouse aisles from the system.
     */
    public void clearAllWarehouseAisles() {
        repository.clear();
    }
}
