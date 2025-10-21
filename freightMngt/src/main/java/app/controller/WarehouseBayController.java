package app.controller;

import app.model.WarehouseBay;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing WarehouseBay entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for WarehouseBay objects.
 * This controller manages warehouse bay data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WarehouseBayController {
    private List<WarehouseBay> warehouseBays;

    /**
     * Default constructor that initializes the warehouse bay list.
     */
    public WarehouseBayController() {
        this.warehouseBays = new ArrayList<>();
    }

    /**
     * Creates and adds a new warehouse bay to the system.
     *
     * @param warehouseBay the WarehouseBay object to be created
     * @return the created WarehouseBay object
     */
    public WarehouseBay createWarehouseBay(WarehouseBay warehouseBay) {
        if (warehouseBay != null) {
            warehouseBays.add(warehouseBay);
        }
        return warehouseBay;
    }

    /**
     * Retrieves all warehouse bays from the system.
     *
     * @return a list of all WarehouseBay objects
     */
    public List<WarehouseBay> getAllWarehouseBays() {
        return new ArrayList<>(warehouseBays);
    }

    /**
     * Retrieves a warehouse bay by its unique identifier.
     *
     * @param bayId the unique identifier of the warehouse bay
     * @return an Optional containing the WarehouseBay if found, or empty if not found
     */
    public Optional<WarehouseBay> getWarehouseBayById(Integer bayId) {
        return warehouseBays.stream()
                .filter(warehouseBay -> warehouseBay.getBayId().equals(bayId))
                .findFirst();
    }

    /**
     * Updates an existing warehouse bay in the system.
     *
     * @param bayId the unique identifier of the warehouse bay to update
     * @param updatedWarehouseBay the WarehouseBay object with updated information
     * @return true if the warehouse bay was updated successfully, false otherwise
     */
    public boolean updateWarehouseBay(Integer bayId, WarehouseBay updatedWarehouseBay) {
        Optional<WarehouseBay> existingWarehouseBay = getWarehouseBayById(bayId);
        if (existingWarehouseBay.isPresent()) {
            int index = warehouseBays.indexOf(existingWarehouseBay.get());
            warehouseBays.set(index, updatedWarehouseBay);
            return true;
        }
        return false;
    }

    /**
     * Deletes a warehouse bay from the system.
     *
     * @param bayId the unique identifier of the warehouse bay to delete
     * @return true if the warehouse bay was deleted successfully, false otherwise
     */
    public boolean deleteWarehouseBay(Integer bayId) {
        return warehouseBays.removeIf(warehouseBay -> warehouseBay.getBayId().equals(bayId));
    }
}
