package app.controller;

import app.model.WagonType;
import app.repositories.WagonTypeRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing WagonType entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for WagonType objects.
 * This controller manages wagon type data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WagonTypeController {
    private final WagonTypeRepository repository;

    /**
     * Default constructor that initializes the controller with the wagon type repository.
     */
    public WagonTypeController() {
        this.repository = Repositories.getInstance().getWagonTypeRepository();
    }

    /**
     * Creates and adds a new wagon type to the system.
     *
     * @param wagonType the WagonType object to be created
     * @return the created WagonType object
     */
    public WagonType createWagonType(WagonType wagonType) {
        if (wagonType != null) {
            repository.addWagonType(wagonType);
        }
        return wagonType;
    }

    /**
     * Retrieves all wagon types from the system.
     *
     * @return a list of all WagonType objects
     */
    public List<WagonType> getAllWagonTypes() {
        return repository.getAllWagonTypes();
    }

    /**
     * Retrieves a wagon type by its unique identifier.
     *
     * @param wagonTypeId the unique identifier of the wagon type
     * @return an Optional containing the WagonType if found, or empty if not found
     */
    public Optional<WagonType> getWagonTypeById(Integer wagonTypeId) {
        WagonType wagonType = repository.getWagonTypeById(wagonTypeId);
        return Optional.ofNullable(wagonType);
    }

    /**
     * Updates an existing wagon type in the system.
     *
     * @param wagonTypeId the unique identifier of the wagon type to update
     * @param updatedWagonType the WagonType object with updated information
     * @return true if the wagon type was updated successfully, false otherwise
     */
    public boolean updateWagonType(Integer wagonTypeId, WagonType updatedWagonType) {
        return repository.updateWagonType(wagonTypeId, updatedWagonType);
    }

    /**
     * Deletes a wagon type from the system.
     *
     * @param wagonTypeId the unique identifier of the wagon type to delete
     * @return true if the wagon type was deleted successfully, false otherwise
     */
    public boolean deleteWagonType(Integer wagonTypeId) {
        return repository.deleteWagonType(wagonTypeId);
    }

    /**
     * Gets the next available wagon type ID.
     *
     * @return the next wagon type ID to be used
     */
    public Integer getNextWagonTypeId() {
        return repository.getNextWagonTypeId();
    }

    /**
     * Clears all wagon types from the system.
     */
    public void clearAllWagonTypes() {
        repository.clear();
    }
}

