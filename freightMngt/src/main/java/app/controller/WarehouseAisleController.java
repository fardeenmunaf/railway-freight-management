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
        return repository.getAllWarehouseAisles();
    }

    /**
     * Retrieves a warehouse aisle by its unique identifier.
     *
     * @param aisleId the unique identifier of the warehouse aisle
     * @return an Optional containing the WarehouseAisle if found, or empty if not found
     */
    public Optional<WarehouseAisle> getWarehouseAisleById(Integer aisleId) {
        WarehouseAisle warehouseAisle = repository.getWarehouseAisleById(aisleId);
        return Optional.ofNullable(warehouseAisle);
    }

    /**
     * Updates an existing warehouse aisle in the system.
     *
     * @param aisleId the unique identifier of the warehouse aisle to update
     * @param updatedWarehouseAisle the WarehouseAisle object with updated information
     * @return true if the warehouse aisle was updated successfully, false otherwise
     */
    public boolean updateWarehouseAisle(Integer aisleId, WarehouseAisle updatedWarehouseAisle) {
        return repository.updateWarehouseAisle(aisleId, updatedWarehouseAisle);
    }

    /**
     * Deletes a warehouse aisle from the system.
     *
     * @param aisleId the unique identifier of the warehouse aisle to delete
     * @return true if the warehouse aisle was deleted successfully, false otherwise
     */
    public boolean deleteWarehouseAisle(Integer aisleId) {
        return repository.deleteWarehouseAisle(aisleId);
    }

    /**
     * Gets the next available warehouse aisle ID.
     *
     * @return the next warehouse aisle ID to be used
     */
    public Integer getNextWarehouseAisleId() {
        return repository.getNextWarehouseAisleId();
    }

    /**
     * Clears all warehouse aisles from the system.
     */
    public void clearAllWarehouseAisles() {
        repository.clear();
    }
}
