package app.controller;

import app.model.QuarantineHold;
import app.repositories.QuarantineHoldRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing QuarantineHold entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for QuarantineHold objects.
 * This controller manages quarantine hold data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class QuarantineHoldController {
    private final QuarantineHoldRepository repository;

    /**
     * Default constructor that initializes the controller with the quarantine hold repository.
     */
    public QuarantineHoldController() {
        this.repository = Repositories.getInstance().getQuarantineHoldRepository();
    }

    /**
     * Creates and adds a new quarantine hold to the system.
     *
     * @param quarantineHold the QuarantineHold object to be created
     * @return the created QuarantineHold object
     */
    public QuarantineHold createQuarantineHold(QuarantineHold quarantineHold) {
        if (quarantineHold != null) {
            repository.addQuarantineHold(quarantineHold);
        }
        return quarantineHold;
    }

    /**
     * Retrieves all quarantine holds from the system.
     *
     * @return a list of all QuarantineHold objects
     */
    public List<QuarantineHold> getAllQuarantineHolds() {
        return repository.getAllQuarantineHolds();
    }

    /**
     * Retrieves a quarantine hold by its unique identifier.
     *
     * @param quarantineHoldId the unique identifier of the quarantine hold
     * @return an Optional containing the QuarantineHold if found, or empty if not found
     */
    public Optional<QuarantineHold> getQuarantineHoldById(Integer quarantineHoldId) {
        QuarantineHold quarantineHold = repository.getQuarantineHoldById(quarantineHoldId);
        return Optional.ofNullable(quarantineHold);
    }

    /**
     * Updates an existing quarantine hold in the system.
     *
     * @param quarantineHoldId the unique identifier of the quarantine hold to update
     * @param updatedQuarantineHold the QuarantineHold object with updated information
     * @return true if the quarantine hold was updated successfully, false otherwise
     */
    public boolean updateQuarantineHold(Integer quarantineHoldId, QuarantineHold updatedQuarantineHold) {
        return repository.updateQuarantineHold(quarantineHoldId, updatedQuarantineHold);
    }

    /**
     * Deletes a quarantine hold from the system.
     *
     * @param quarantineHoldId the unique identifier of the quarantine hold to delete
     * @return true if the quarantine hold was deleted successfully, false otherwise
     */
    public boolean deleteQuarantineHold(Integer quarantineHoldId) {
        return repository.deleteQuarantineHold(quarantineHoldId);
    }

    /**
     * Gets the next available quarantine hold ID.
     *
     * @return the next quarantine hold ID to be used
     */
    public Integer getNextQuarantineHoldId() {
        return repository.getNextHoldId();
    }

    /**
     * Clears all quarantine holds from the system.
     */
    public void clearAllQuarantineHolds() {
        repository.clear();
    }
}
