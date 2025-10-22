package app.repositories;

import app.model.Warehouse;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing warehouses in the freight management system.
 * This class provides methods to add, retrieve, and manage warehouse entities.
 * It maintains a collection of all warehouses and allows searching by warehouse ID or name.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WarehouseRepository {

    private final List<Warehouse> warehouses = new ArrayList<>();

    /**
     * Adds a new warehouse to the repository if it doesn't already exist.
     *
     * @param warehouse the warehouse to add
     */

    public void addWarehouse(Warehouse warehouse) {
        if (warehouse != null && warehouse.getWarehouseId() != null) {
            if (!containsWarehouse(warehouse.getWarehouseId())) {
                warehouses.add(warehouse);
            }
        }
    }

    /**
     * Retrieves all warehouses in the repository.
     *
     * @return a new list containing all warehouses
     */

    public List<Warehouse> getAllWarehouses() {
        return new ArrayList<>(warehouses);
    }

    /**
     * Retrieves a specific warehouse by its ID.
     *
     * @param warehouseId the warehouse identifier
     * @return the matching warehouse, or null if not found
     */

    public Warehouse getWarehouseById(Integer warehouseId) {
        for (Warehouse warehouse : warehouses) {
            if (warehouseId.equals(warehouse.getWarehouseId())) {
                return warehouse;
            }
        }
        return null;
    }

    /**
     * Retrieves a specific warehouse by its name.
     *
     * @param name the warehouse name
     * @return the matching warehouse, or null if not found
     */

    public Warehouse getWarehouseByName(String name) {
        for (Warehouse warehouse : warehouses) {
            if (name != null && name.equals(warehouse.getName())) {
                return warehouse;
            }
        }
        return null;
    }

    /**
     * Updates an existing warehouse in the repository.
     *
     * @param warehouseId the warehouse identifier
     * @param updatedWarehouse the updated warehouse
     * @return true if the warehouse was updated successfully, false otherwise
     */
    public boolean updateWarehouse(Integer warehouseId, Warehouse updatedWarehouse) {
        Warehouse existingWarehouse = getWarehouseById(warehouseId);
        if (existingWarehouse != null) {
            int index = warehouses.indexOf(existingWarehouse);
            warehouses.set(index, updatedWarehouse);
            return true;
        }
        return false;
    }

    /**
     * Deletes a warehouse from the repository.
     *
     * @param warehouseId the warehouse identifier
     * @return true if the warehouse was deleted successfully, false otherwise
     */
    public boolean deleteWarehouse(Integer warehouseId) {
        return warehouses.removeIf(warehouse -> warehouseId.equals(warehouse.getWarehouseId()));
    }

    /**
     * Gets the next available warehouse ID.
     * Finds the maximum warehouse ID currently in use and returns the next sequential ID.
     *
     * @return the next available warehouse ID (max current ID + 1)
     */
    public Integer getNextWarehouseId() {
        int maxId = 0;
        for (Warehouse warehouse : warehouses) {
            if (warehouse.getWarehouseId() != null && warehouse.getWarehouseId() > maxId) {
                maxId = warehouse.getWarehouseId();
            }
        }
        return maxId + 1;
    }

    /**
     * Checks if a warehouse exists in the repository.
     *
     * @param warehouseId the warehouse identifier
     * @return true if the warehouse exists, false otherwise
     */

    private boolean containsWarehouse(Integer warehouseId) {
        return getWarehouseById(warehouseId) != null;
    }

    /**
     * Checks if a warehouse with the given name exists in the repository.
     *
     * @param name the warehouse name
     * @return true if a warehouse with this name exists, false otherwise
     */
    public boolean containsName(String name) {
        return getWarehouseByName(name) != null;
    }

    /**
     * Clears all warehouses from the repository.
     */

    public void clear() {
        warehouses.clear();
    }
}
