package app.controller;

import app.model.Locomotive;
import app.repositories.LocomotiveRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Locomotive entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Locomotive objects.
 * This controller manages locomotive data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveController {
    private final LocomotiveRepository repository;

    /**
     * Default constructor that initializes the controller with the locomotive repository.
     */
    public LocomotiveController() {
        this.repository = Repositories.getInstance().getLocomotiveRepository();
    }

    /**
     * Creates and adds a new locomotive to the system.
     *
     * @param locomotive the Locomotive object to be created
     * @return the created Locomotive object
     */
    public Locomotive createLocomotive(Locomotive locomotive) {
        if (locomotive != null) {
            repository.addLocomotive(locomotive);
        }
        return locomotive;
    }

    /**
     * Retrieves all locomotives from the system.
     *
     * @return a list of all Locomotive objects
     */
    public List<Locomotive> getAllLocomotives() {
        return repository.getAllLocomotives();
    }

    /**
     * Retrieves a locomotive by its unique identifier.
     *
     * @param locomotiveId the unique identifier of the locomotive
     * @return an Optional containing the Locomotive if found, or empty if not found
     */
    public Optional<Locomotive> getLocomotiveById(Integer locomotiveId) {
        Locomotive locomotive = repository.getLocomotiveById(locomotiveId);
        return Optional.ofNullable(locomotive);
    }

    /**
     * Updates an existing locomotive in the system.
     *
     * @param locomotiveId the unique identifier of the locomotive to update
     * @param updatedLocomotive the Locomotive object with updated information
     * @return true if the locomotive was updated successfully, false otherwise
     */
    public boolean updateLocomotive(Integer locomotiveId, Locomotive updatedLocomotive) {
        return repository.updateLocomotive(locomotiveId, updatedLocomotive);
    }

    /**
     * Deletes a locomotive from the system.
     *
     * @param locomotiveId the unique identifier of the locomotive to delete
     * @return true if the locomotive was deleted successfully, false otherwise
     */
    public boolean deleteLocomotive(Integer locomotiveId) {
        return repository.deleteLocomotive(locomotiveId);
    }

    /**
     * Gets the next available locomotive ID.
     *
     * @return the next locomotive ID to be used
     */
    public Integer getNextLocomotiveId() {
        return repository.getNextLocomotiveId();
    }

    /**
     * Clears all locomotives from the system.
     */
    public void clearAllLocomotives() {
        repository.clear();
    }
}
