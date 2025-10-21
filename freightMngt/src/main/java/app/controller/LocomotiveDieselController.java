package app.controller;

import app.model.LocomotiveDiesel;
import java.util.ArrayList;
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
    private List<LocomotiveDiesel> locomotiveDiesels;

    /**
     * Default constructor that initializes the locomotive diesel list.
     */
    public LocomotiveDieselController() {
        this.locomotiveDiesels = new ArrayList<>();
    }

    /**
     * Creates and adds a new diesel locomotive to the system.
     *
     * @param locomotiveDiesel the LocomotiveDiesel object to be created
     * @return the created LocomotiveDiesel object
     */
    public LocomotiveDiesel createLocomotiveDiesel(LocomotiveDiesel locomotiveDiesel) {
        if (locomotiveDiesel != null) {
            locomotiveDiesels.add(locomotiveDiesel);
        }
        return locomotiveDiesel;
    }

    /**
     * Retrieves all diesel locomotives from the system.
     *
     * @return a list of all LocomotiveDiesel objects
     */
    public List<LocomotiveDiesel> getAllLocomotiveDiesels() {
        return new ArrayList<>(locomotiveDiesels);
    }

    /**
     * Retrieves a diesel locomotive by its unique identifier.
     *
     * @param locomotiveDieselId the unique identifier of the diesel locomotive
     * @return an Optional containing the LocomotiveDiesel if found, or empty if not found
     */
    public Optional<LocomotiveDiesel> getLocomotiveDieselById(Integer locomotiveDieselId) {
        return locomotiveDiesels.stream()
                .filter(locomotiveDiesel -> locomotiveDiesel.getLocomotiveId().equals(locomotiveDieselId))
                .findFirst();
    }

    /**
     * Updates an existing diesel locomotive in the system.
     *
     * @param locomotiveDieselId the unique identifier of the diesel locomotive to update
     * @param updatedLocomotiveDiesel the LocomotiveDiesel object with updated information
     * @return true if the diesel locomotive was updated successfully, false otherwise
     */
    public boolean updateLocomotiveDiesel(Integer locomotiveDieselId, LocomotiveDiesel updatedLocomotiveDiesel) {
        Optional<LocomotiveDiesel> existingLocomotiveDiesel = getLocomotiveDieselById(locomotiveDieselId);
        if (existingLocomotiveDiesel.isPresent()) {
            int index = locomotiveDiesels.indexOf(existingLocomotiveDiesel.get());
            locomotiveDiesels.set(index, updatedLocomotiveDiesel);
            return true;
        }
        return false;
    }

    /**
     * Deletes a diesel locomotive from the system.
     *
     * @param locomotiveDieselId the unique identifier of the diesel locomotive to delete
     * @return true if the diesel locomotive was deleted successfully, false otherwise
     */
    public boolean deleteLocomotiveDiesel(Integer locomotiveDieselId) {
        return locomotiveDiesels.removeIf(locomotiveDiesel -> locomotiveDiesel.getLocomotiveId().equals(locomotiveDieselId));
    }
}
