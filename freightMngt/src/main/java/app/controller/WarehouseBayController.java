package app.controller;

import app.model.WarehouseBay;
import app.repositories.WarehouseBayRepository;
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
     * Default constructor that initializes the controller with a new repository.
     */
    public WarehouseBayController() {
        this.repository = new WarehouseBayRepository();
    }

    /**
     * Constructor that accepts a repository instance.
     *
     * @param repository the WarehouseBayRepository to use
     */
    public WarehouseBayController(WarehouseBayRepository repository) {
        this.repository = repository;
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
     * Retrieves a warehouse bay by warehouse ID, aisle number, and bay number.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number
     * @param bayNo the bay number
     * @return an Optional containing the WarehouseBay if found, or empty if not found
     */
    public Optional<WarehouseBay> getWarehouseBay(Integer warehouseId, Integer aisleNo, Integer bayNo) {
        WarehouseBay bay = repository.getWarehouseBay(warehouseId, aisleNo, bayNo);
        return Optional.ofNullable(bay);
    }

    /**
     * Retrieves all bays for a specific aisle in a warehouse.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number
     * @return a list of all bays in the specified aisle
     */
    public List<WarehouseBay> getBaysByAisle(Integer warehouseId, Integer aisleNo) {
        return repository.getBaysByAisle(warehouseId, aisleNo);
    }

    /**
     * Updates an existing warehouse bay in the system.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number
     * @param bayNo the bay number
     * @param updatedWarehouseBay the WarehouseBay object with updated information
     * @return true if the warehouse bay was updated successfully, false otherwise
     */
    public boolean updateWarehouseBay(Integer warehouseId, Integer aisleNo, Integer bayNo, WarehouseBay updatedWarehouseBay) {
        return repository.updateWarehouseBay(warehouseId, aisleNo, bayNo, updatedWarehouseBay);
    }

    /**
     * Deletes a warehouse bay from the system.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number
     * @param bayNo the bay number
     * @return true if the warehouse bay was deleted successfully, false otherwise
     */
    public boolean deleteWarehouseBay(Integer warehouseId, Integer aisleNo, Integer bayNo) {
        return repository.deleteWarehouseBay(warehouseId, aisleNo, bayNo);
    }

    /**
     * Gets the next available bay number for a specific aisle.
     *
     * @param warehouseId the warehouse identifier
     * @param aisleNo the aisle number
     * @return the next available bay number
     */
    public Integer getNextBayNo(Integer warehouseId, Integer aisleNo) {
        return repository.getNextBayNo(warehouseId, aisleNo);
    }
}
