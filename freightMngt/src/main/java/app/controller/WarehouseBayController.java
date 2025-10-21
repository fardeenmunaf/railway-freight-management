package app.controller;

import app.model.WarehouseBay;
import app.repositories.WarehouseBayRepository;
import app.repositories.Repositories;
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
    private final WarehouseBayRepository repository;

    /**
     * Default constructor that initializes the controller with the warehouse bay repository.
     */
    public WarehouseBayController() {
        this.repository = Repositories.getInstance().getWarehouseBayRepository();
    }

    /**
     * Creates and adds a new warehouse bay to the system.
     *
     * @param warehouseBay the WarehouseBay object to be created
     * @return the created WarehouseBay object
     */
    public WarehouseBay createWarehouseBay(WarehouseBay warehouseBay) {
        if (warehouseBay != null) {
            repository.addWarehouseBay(warehouseBay);
        }
        return warehouseBay;
    }

    /**
     * Retrieves all warehouse bays from the system.
     *
     * @return a list of all WarehouseBay objects
     */
    public List<WarehouseBay> getAllWarehouseBays() {
        return repository.getAllWarehouseBays();
    }

    /**
     * Retrieves a warehouse bay by its unique identifier.
     *
     * @param bayId the unique identifier of the warehouse bay
     * @return an Optional containing the WarehouseBay if found, or empty if not found
     */
    public Optional<WarehouseBay> getWarehouseBayById(Integer bayId) {
        WarehouseBay warehouseBay = repository.getWarehouseBayById(bayId);
        return Optional.ofNullable(warehouseBay);
    }

    /**
     * Updates an existing warehouse bay in the system.
     *
     * @param bayId the unique identifier of the warehouse bay to update
     * @param updatedWarehouseBay the WarehouseBay object with updated information
     * @return true if the warehouse bay was updated successfully, false otherwise
     */
    public boolean updateWarehouseBay(Integer bayId, WarehouseBay updatedWarehouseBay) {
        return repository.updateWarehouseBay(bayId, updatedWarehouseBay);
    }

    /**
     * Deletes a warehouse bay from the system.
     *
     * @param bayId the unique identifier of the warehouse bay to delete
     * @return true if the warehouse bay was deleted successfully, false otherwise
     */
    public boolean deleteWarehouseBay(Integer bayId) {
        return repository.deleteWarehouseBay(bayId);
    }

    /**
     * Gets the next available warehouse bay ID.
     *
     * @return the next warehouse bay ID to be used
     */
    public Integer getNextWarehouseBayId() {
        return repository.getNextWarehouseBayId();
    }

    /**
     * Clears all warehouse bays from the system.
     */
    public void clearAllWarehouseBays() {
        repository.clear();
    }
}
