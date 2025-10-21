package app.controller;

import app.model.LocomotiveEletric;
import app.repositories.LocomotiveEletricRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing LocomotiveEletric entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for LocomotiveEletric objects.
 * This controller manages electric locomotive data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveEletricController {
    private final LocomotiveEletricRepository repository;

    /**
     * Default constructor that initializes the controller with the electric locomotive repository.
     */
    public LocomotiveEletricController() {
        this.repository = Repositories.getInstance().getLocomotiveEletricRepository();
    }

    /**
     * Creates and adds a new electric locomotive to the system.
     *
     * @param locomotiveEletric the LocomotiveEletric object to be created
     * @return the created LocomotiveEletric object
     */
    public LocomotiveEletric createLocomotiveEletric(LocomotiveEletric locomotiveEletric) {
        if (locomotiveEletric != null) {
            repository.addLocomotiveEletric(locomotiveEletric);
        }
        return locomotiveEletric;
    }

    /**
     * Retrieves all electric locomotives from the system.
     *
     * @return a list of all LocomotiveEletric objects
     */
    public List<LocomotiveEletric> getAllLocomotiveEletrics() {
        return repository.getAllLocomotiveEletrics();
    }

    /**
     * Retrieves an electric locomotive by its unique identifier.
     *
     * @param locomotiveId the unique identifier of the electric locomotive
     * @return an Optional containing the LocomotiveEletric if found, or empty if not found
     */
    public Optional<LocomotiveEletric> getLocomotiveEletricById(Integer locomotiveId) {
        LocomotiveEletric locomotiveEletric = repository.getLocomotiveEletricById(locomotiveId);
        return Optional.ofNullable(locomotiveEletric);
    }

    /**
     * Updates an existing electric locomotive in the system.
     *
     * @param locomotiveId the unique identifier of the electric locomotive to update
     * @param updatedLocomotiveEletric the LocomotiveEletric object with updated information
     * @return true if the electric locomotive was updated successfully, false otherwise
     */
    public boolean updateLocomotiveEletric(Integer locomotiveId, LocomotiveEletric updatedLocomotiveEletric) {
        return repository.updateLocomotiveEletric(locomotiveId, updatedLocomotiveEletric);
    }

    /**
     * Deletes an electric locomotive from the system.
     *
     * @param locomotiveId the unique identifier of the electric locomotive to delete
     * @return true if the electric locomotive was deleted successfully, false otherwise
     */
    public boolean deleteLocomotiveEletric(Integer locomotiveId) {
        return repository.deleteLocomotiveEletric(locomotiveId);
    }

    /**
     * Gets the next available electric locomotive ID.
     *
     * @return the next electric locomotive ID to be used
     */
    public Integer getNextLocomotiveEletricId() {
        return repository.getNextLocomotiveEletricId();
    }

    /**
     * Clears all electric locomotives from the system.
     */
    public void clearAllLocomotiveEletrics() {
        repository.clear();
    }
}
