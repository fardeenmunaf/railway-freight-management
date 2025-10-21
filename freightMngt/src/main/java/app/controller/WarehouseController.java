package app.controller;

import app.model.Warehouse;
import app.repositories.WarehouseRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Warehouse entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Warehouse objects.
 * This controller manages warehouse data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WarehouseController {
    private final WarehouseRepository repository;

    /**
     * Default constructor that initializes the controller with the warehouse repository.
     */
    public WarehouseController() {
        this.repository = Repositories.getInstance().getWarehouseRepository();
    }

    /**
     * Creates and adds a new warehouse to the system.
     *
     * @param warehouse the Warehouse object to be created
     * @return the created Warehouse object
     */
    public Warehouse createWarehouse(Warehouse warehouse) {
        if (warehouse != null) {
            repository.addWarehouse(warehouse);
        }
        return warehouse;
    }

    /**
     * Retrieves all warehouses from the system.
     *
     * @return a list of all Warehouse objects
     */
    public List<Warehouse> getAllWarehouses() {
        return repository.getAllWarehouses();
    }

    /**
     * Retrieves a warehouse by its unique identifier.
     *
     * @param warehouseId the unique identifier of the warehouse
     * @return an Optional containing the Warehouse if found, or empty if not found
     */
    public Optional<Warehouse> getWarehouseById(Integer warehouseId) {
        Warehouse warehouse = repository.getWarehouseById(warehouseId);
        return Optional.ofNullable(warehouse);
    }

    /**
     * Retrieves a warehouse by its name.
     *
     * @param name the name of the warehouse
     * @return an Optional containing the Warehouse if found, or empty if not found
     */
    public Optional<Warehouse> getWarehouseByName(String name) {
        Warehouse warehouse = repository.getWarehouseByName(name);
        return Optional.ofNullable(warehouse);
    }

    /**
     * Updates an existing warehouse in the system.
     *
     * @param warehouseId the unique identifier of the warehouse to update
     * @param updatedWarehouse the Warehouse object with updated information
     * @return true if the warehouse was updated successfully, false otherwise
     */
    public boolean updateWarehouse(Integer warehouseId, Warehouse updatedWarehouse) {
        return repository.updateWarehouse(warehouseId, updatedWarehouse);
    }

    /**
     * Deletes a warehouse from the system.
     *
     * @param warehouseId the unique identifier of the warehouse to delete
     * @return true if the warehouse was deleted successfully, false otherwise
     */
    public boolean deleteWarehouse(Integer warehouseId) {
        return repository.deleteWarehouse(warehouseId);
    }

    /**
     * Gets the next available warehouse ID.
     *
     * @return the next warehouse ID to be used
     */
    public Integer getNextWarehouseId() {
        return repository.getNextWarehouseId();
    }

    /**
     * Clears all warehouses from the system.
     */
    public void clearAllWarehouses() {
        repository.clear();
    }
}
