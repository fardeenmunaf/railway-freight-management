package app.controller;

import app.model.LocomotiveDiesel;
import app.repositories.LocomotiveDieselRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing LocomotiveDiesel entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for LocomotiveDiesel objects.
 * This controller manages diesel locomotive data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveDieselController {
    private final LocomotiveDieselRepository repository;

    /**
     * Default constructor that initializes the controller with the diesel locomotive repository.
     */
    public LocomotiveDieselController() {
        this.repository = Repositories.getInstance().getLocomotiveDieselRepository();
    }

    /**
     * Creates and adds a new diesel locomotive to the system.
     *
     * @param locomotiveDiesel the LocomotiveDiesel object to be created
     * @return the created LocomotiveDiesel object
     */
    public LocomotiveDiesel createLocomotiveDiesel(LocomotiveDiesel locomotiveDiesel) {
        if (locomotiveDiesel != null) {
            repository.addLocomotiveDiesel(locomotiveDiesel);
        }
        return locomotiveDiesel;
    }

    /**
     * Retrieves all diesel locomotives from the system.
     *
     * @return a list of all LocomotiveDiesel objects
     */
    public List<LocomotiveDiesel> getAllLocomotiveDiesels() {
        return repository.getAllLocomotiveDiesels();
    }

    /**
     * Retrieves a diesel locomotive by its unique identifier.
     *
     * @param locomotiveId the unique identifier of the diesel locomotive
     * @return an Optional containing the LocomotiveDiesel if found, or empty if not found
     */
    public Optional<LocomotiveDiesel> getLocomotiveDieselById(Integer locomotiveId) {
        LocomotiveDiesel locomotiveDiesel = repository.getLocomotiveDieselById(locomotiveId);
        return Optional.ofNullable(locomotiveDiesel);
    }

    /**
     * Updates an existing diesel locomotive in the system.
     *
     * @param locomotiveId the unique identifier of the diesel locomotive to update
     * @param updatedLocomotiveDiesel the LocomotiveDiesel object with updated information
     * @return true if the diesel locomotive was updated successfully, false otherwise
     */
    public boolean updateLocomotiveDiesel(Integer locomotiveId, LocomotiveDiesel updatedLocomotiveDiesel) {
        return repository.updateLocomotiveDiesel(locomotiveId, updatedLocomotiveDiesel);
    }

    /**
     * Deletes a diesel locomotive from the system.
     *
     * @param locomotiveId the unique identifier of the diesel locomotive to delete
     * @return true if the diesel locomotive was deleted successfully, false otherwise
     */
    public boolean deleteLocomotiveDiesel(Integer locomotiveId) {
        return repository.deleteLocomotiveDiesel(locomotiveId);
    }

    /**
     * Gets the next available diesel locomotive ID.
     *
     * @return the next diesel locomotive ID to be used
     */
    public Integer getNextLocomotiveDieselId() {
        return repository.getNextLocomotiveDieselId();
    }

    /**
     * Clears all diesel locomotives from the system.
     */
    public void clearAllLocomotiveDiesels() {
        repository.clear();
    }
}
