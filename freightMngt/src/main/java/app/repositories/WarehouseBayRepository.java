package app.repositories;

import app.model.WarehouseAisle;
import app.model.WarehouseBay;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing warehouse bays in the freight management system.
 * This class provides methods to add, retrieve, and manage warehouse bay entities.
 * It maintains a collection of all warehouse bays and allows searching by warehouse ID, aisle number, and bay number.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WarehouseBayRepository {

    private final List<WarehouseBay> warehouseBays = new ArrayList<>();

    /**
     * Adds a new warehouse bay to the repository if it doesn't already exist.
     * The bay is checked for duplicates based on warehouse ID, aisle number, and bay number.
     *
     * @param bay the warehouse bay to add
     */

    public void addWarehouseBay(WarehouseBay bay) {
        if (bay != null && bay.getAisle() != null && bay.getAisle().getWarehouse() != null) {
            Integer warehouseId = bay.getAisle().getWarehouse().getWarehouseId();
            Integer aisleNo = bay.getAisle().getAisleNo();
            Integer bayNo = bay.getBayNo();

            if (warehouseId != null && aisleNo != null && bayNo != null) {
                if (!containsWarehouseBay(warehouseId, aisleNo, bayNo)) {
                    warehouseBays.add(bay);
                }
            }
        }
    }

    /**
     * Retrieves all warehouse bays in the repository.
     *
     * @return a new list containing all warehouse bays
     */

    public List<WarehouseBay> getAllWarehouseBays() {
        return new ArrayList<>(warehouseBays);
    }

    /**
     * Retrieves a specific warehouse bay by warehouse ID, aisle number, and bay number.
     * The method navigates through the bay's aisle to access the warehouse information.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number within the warehouse
     * @param bayNo the bay number within the aisle
     * @return the matching warehouse bay, or null if not found
     */

    public WarehouseBay getWarehouseBay(Integer warehouseId, Integer aisleNo, Integer bayNo) {
        for (WarehouseBay bay : warehouseBays) {
            if (bay.getAisle() != null && bay.getAisle().getWarehouse() != null) {
                if (warehouseId.equals(bay.getAisle().getWarehouse().getWarehouseId()) &&
                    aisleNo.equals(bay.getAisle().getAisleNo()) &&
                    bayNo.equals(bay.getBayNo())) {
                    return bay;
                }
            }
        }
        return null;
    }

    /**
     * Checks if a warehouse bay exists in the repository.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number within the warehouse
     * @param bayNo the bay number within the aisle
     * @return true if the bay exists, false otherwise
     */

    private boolean containsWarehouseBay(Integer warehouseId, Integer aisleNo, Integer bayNo) {
        return getWarehouseBay(warehouseId, aisleNo, bayNo) != null;
    }

    /**
     * Clears all warehouse bays from the repository.
     */

    public void clear() {
        warehouseBays.clear();
    }
}
