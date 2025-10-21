package app.controller;

import app.model.LocomotiveType;
import app.repositories.LocomotiveTypeRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing LocomotiveType entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for LocomotiveType objects.
 * This controller manages locomotive type data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveTypeController {
    private final LocomotiveTypeRepository repository;

    /**
     * Default constructor that initializes the controller with the locomotive type repository.
     */
    public LocomotiveTypeController() {
        this.repository = Repositories.getInstance().getLocomotiveTypeRepository();
    }

    /**
     * Creates and adds a new locomotive type to the system.
     *
     * @param locomotiveType the LocomotiveType object to be created
     * @return the created LocomotiveType object
     */
    public LocomotiveType createLocomotiveType(LocomotiveType locomotiveType) {
        if (locomotiveType != null) {
            repository.addLocomotiveType(locomotiveType);
        }
        return locomotiveType;
    }

    /**
     * Retrieves all locomotive types from the system.
     *
     * @return a list of all LocomotiveType objects
     */
    public List<LocomotiveType> getAllLocomotiveTypes() {
        return repository.getAllLocomotiveTypes();
    }

    /**
     * Retrieves a locomotive type by its unique identifier.
     *
     * @param locomotiveTypeId the unique identifier of the locomotive type
     * @return an Optional containing the LocomotiveType if found, or empty if not found
     */
    public Optional<LocomotiveType> getLocomotiveTypeById(Integer locomotiveTypeId) {
        LocomotiveType locomotiveType = repository.getLocomotiveTypeById(locomotiveTypeId);
        return Optional.ofNullable(locomotiveType);
    }

    /**
     * Updates an existing locomotive type in the system.
     *
     * @param locomotiveTypeId the unique identifier of the locomotive type to update
     * @param updatedLocomotiveType the LocomotiveType object with updated information
     * @return true if the locomotive type was updated successfully, false otherwise
     */
    public boolean updateLocomotiveType(Integer locomotiveTypeId, LocomotiveType updatedLocomotiveType) {
        return repository.updateLocomotiveType(locomotiveTypeId, updatedLocomotiveType);
    }

    /**
     * Deletes a locomotive type from the system.
     *
     * @param locomotiveTypeId the unique identifier of the locomotive type to delete
     * @return true if the locomotive type was deleted successfully, false otherwise
     */
    public boolean deleteLocomotiveType(Integer locomotiveTypeId) {
        return repository.deleteLocomotiveType(locomotiveTypeId);
    }

    /**
     * Gets the next available locomotive type ID.
     *
     * @return the next locomotive type ID to be used
     */
    public Integer getNextLocomotiveTypeId() {
        return repository.getNextTypeId();
    }

    /**
     * Clears all locomotive types from the system.
     */
    public void clearAllLocomotiveTypes() {
        repository.clear();
    }
}
