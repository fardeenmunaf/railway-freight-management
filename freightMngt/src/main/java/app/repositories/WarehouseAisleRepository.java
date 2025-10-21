package app.repositories;

import app.model.WarehouseAisle;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing warehouse aisles in the freight management system.
 * This class provides methods to add, retrieve, and manage warehouse aisle entities.
 * It maintains a collection of all warehouse aisles and allows searching by warehouse ID and aisle number.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WarehouseAisleRepository {

    private final List<WarehouseAisle> aisles = new ArrayList<>();

    /**
     * Adds a new warehouse aisle to the repository if it doesn't already exist.
     *
     * @param aisle the warehouse aisle to add
     */

    public void addWarehouseAisle(WarehouseAisle aisle) {
        if (aisle != null && aisle.getWarehouse() != null) {
            Integer warehouseId = aisle.getWarehouse().getWarehouseId();
            Integer aisleNo = aisle.getAisleNo();

            if (warehouseId != null && aisleNo != null) {
                if (!containsAisle(warehouseId, aisleNo)) {
                    aisles.add(aisle);
                }
            }
        }
    }

    /**
     * Retrieves all warehouse aisles in the repository.
     *
     * @return a new list containing all warehouse aisles
     */

    public List<WarehouseAisle> getAllAisles() {
        return new ArrayList<>(aisles);
    }

    /**
     * Retrieves a specific warehouse aisle by warehouse ID and aisle number.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number within the warehouse
     * @return the matching warehouse aisle, or null if not found
     */

    public WarehouseAisle getAisle(Integer warehouseId, Integer aisleNo) {
        for (WarehouseAisle aisle : aisles) {
            if (aisle.getWarehouse() != null) {
                if (warehouseId.equals(aisle.getWarehouse().getWarehouseId()) &&
                    aisleNo.equals(aisle.getAisleNo())) {
                    return aisle;
                }
            }
        }
        return null;
    }

    public Integer getNextAisleNo(Integer warehouseId) {
        int maxAisleNo = 0;
        for (WarehouseAisle aisle : aisles) {
            if (aisle.getWarehouse() != null && warehouseId.equals(aisle.getWarehouse().getWarehouseId())) {
                if (aisle.getAisleNo() != null && aisle.getAisleNo() > maxAisleNo) {
                    maxAisleNo = aisle.getAisleNo();
                }
            }
        }
        return maxAisleNo + 1;
    }

    /**
     * Checks if a warehouse aisle exists in the repository.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number within the warehouse
     * @return true if the aisle exists, false otherwise
     */

    private boolean containsAisle(Integer warehouseId, Integer aisleNo) {
        return getAisle(warehouseId, aisleNo) != null;
    }


    /**
     * Clears all warehouse aisles from the repository.
     */

    public void clear() {
        aisles.clear();
    }
}

