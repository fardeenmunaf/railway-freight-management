package app.controller;

import app.model.Warehouse;
import java.util.ArrayList;
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
    private List<Warehouse> warehouses;

    /**
     * Default constructor that initializes the warehouse list.
     */
    public WarehouseController() {
        this.warehouses = new ArrayList<>();
    }

    /**
     * Creates and adds a new warehouse to the system.
     *
     * @param warehouse the Warehouse object to be created
     * @return the created Warehouse object
     */
    public Warehouse createWarehouse(Warehouse warehouse) {
        if (warehouse != null) {
            warehouses.add(warehouse);
        }
        return warehouse;
    }

    /**
     * Retrieves all warehouses from the system.
     *
     * @return a list of all Warehouse objects
     */
    public List<Warehouse> getAllWarehouses() {
        return new ArrayList<>(warehouses);
    }

    /**
     * Retrieves a warehouse by its unique identifier.
     *
     * @param warehouseId the unique identifier of the warehouse
     * @return an Optional containing the Warehouse if found, or empty if not found
     */
    public Optional<Warehouse> getWarehouseById(Integer warehouseId) {
        return warehouses.stream()
                .filter(warehouse -> warehouse.getWarehouseId().equals(warehouseId))
                .findFirst();
    }

    /**
     * Updates an existing warehouse in the system.
     *
     * @param warehouseId the unique identifier of the warehouse to update
     * @param updatedWarehouse the Warehouse object with updated information
     * @return true if the warehouse was updated successfully, false otherwise
     */
    public boolean updateWarehouse(Integer warehouseId, Warehouse updatedWarehouse) {
        Optional<Warehouse> existingWarehouse = getWarehouseById(warehouseId);
        if (existingWarehouse.isPresent()) {
            int index = warehouses.indexOf(existingWarehouse.get());
            warehouses.set(index, updatedWarehouse);
            return true;
        }
        return false;
    }

    /**
     * Deletes a warehouse from the system.
     *
     * @param warehouseId the unique identifier of the warehouse to delete
     * @return true if the warehouse was deleted successfully, false otherwise
     */
    public boolean deleteWarehouse(Integer warehouseId) {
        return warehouses.removeIf(warehouse -> warehouse.getWarehouseId().equals(warehouseId));
    }
}

