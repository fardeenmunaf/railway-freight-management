package app.controller;

import app.model.QuarantineHold;
import java.util.ArrayList;
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
    private List<QuarantineHold> quarantineHolds;

    /**
     * Default constructor that initializes the quarantine hold list.
     */
    public QuarantineHoldController() {
        this.quarantineHolds = new ArrayList<>();
    }

    /**
     * Creates and adds a new quarantine hold to the system.
     *
     * @param quarantineHold the QuarantineHold object to be created
     * @return the created QuarantineHold object
     */
    public QuarantineHold createQuarantineHold(QuarantineHold quarantineHold) {
        if (quarantineHold != null) {
            quarantineHolds.add(quarantineHold);
        }
        return quarantineHold;
    }

    /**
     * Retrieves all quarantine holds from the system.
     *
     * @return a list of all QuarantineHold objects
     */
    public List<QuarantineHold> getAllQuarantineHolds() {
        return new ArrayList<>(quarantineHolds);
    }

    /**
     * Retrieves a quarantine hold by its unique identifier.
     *
     * @param qholdId the unique identifier of the quarantine hold
     * @return an Optional containing the QuarantineHold if found, or empty if not found
     */
    public Optional<QuarantineHold> getQuarantineHoldById(Integer qholdId) {
        return quarantineHolds.stream()
                .filter(quarantineHold -> quarantineHold.getQholdId().equals(qholdId))
                .findFirst();
    }

    /**
     * Updates an existing quarantine hold in the system.
     *
     * @param qholdId the unique identifier of the quarantine hold to update
     * @param updatedQuarantineHold the QuarantineHold object with updated information
     * @return true if the quarantine hold was updated successfully, false otherwise
     */
    public boolean updateQuarantineHold(Integer qholdId, QuarantineHold updatedQuarantineHold) {
        Optional<QuarantineHold> existingQuarantineHold = getQuarantineHoldById(qholdId);
        if (existingQuarantineHold.isPresent()) {
            int index = quarantineHolds.indexOf(existingQuarantineHold.get());
            quarantineHolds.set(index, updatedQuarantineHold);
            return true;
        }
        return false;
    }

    /**
     * Deletes a quarantine hold from the system.
     *
     * @param qholdId the unique identifier of the quarantine hold to delete
     * @return true if the quarantine hold was deleted successfully, false otherwise
     */
    public boolean deleteQuarantineHold(Integer qholdId) {
        return quarantineHolds.removeIf(quarantineHold -> quarantineHold.getQholdId().equals(qholdId));
    }
}
