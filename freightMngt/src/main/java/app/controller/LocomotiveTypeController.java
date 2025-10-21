package app.controller;

import app.model.LocomotiveType;
import java.util.ArrayList;
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
    private List<LocomotiveType> locomotiveTypes;

    /**
     * Default constructor that initializes the locomotive type list.
     */
    public LocomotiveTypeController() {
        this.locomotiveTypes = new ArrayList<>();
    }

    /**
     * Creates and adds a new locomotive type to the system.
     *
     * @param locomotiveType the LocomotiveType object to be created
     * @return the created LocomotiveType object
     */
    public LocomotiveType createLocomotiveType(LocomotiveType locomotiveType) {
        if (locomotiveType != null) {
            locomotiveTypes.add(locomotiveType);
        }
        return locomotiveType;
    }

    /**
     * Retrieves all locomotive types from the system.
     *
     * @return a list of all LocomotiveType objects
     */
    public List<LocomotiveType> getAllLocomotiveTypes() {
        return new ArrayList<>(locomotiveTypes);
    }

    /**
     * Retrieves a locomotive type by its unique identifier.
     *
     * @param locomotiveTypeId the unique identifier of the locomotive type
     * @return an Optional containing the LocomotiveType if found, or empty if not found
     */
    public Optional<LocomotiveType> getLocomotiveTypeById(Integer locomotiveTypeId) {
        return locomotiveTypes.stream()
                .filter(locomotiveType -> locomotiveType.getLocomotiveTypeId().equals(locomotiveTypeId))
                .findFirst();
    }

    /**
     * Updates an existing locomotive type in the system.
     *
     * @param locomotiveTypeId the unique identifier of the locomotive type to update
     * @param updatedLocomotiveType the LocomotiveType object with updated information
     * @return true if the locomotive type was updated successfully, false otherwise
     */
    public boolean updateLocomotiveType(Integer locomotiveTypeId, LocomotiveType updatedLocomotiveType) {
        Optional<LocomotiveType> existingLocomotiveType = getLocomotiveTypeById(locomotiveTypeId);
        if (existingLocomotiveType.isPresent()) {
            int index = locomotiveTypes.indexOf(existingLocomotiveType.get());
            locomotiveTypes.set(index, updatedLocomotiveType);
            return true;
        }
        return false;
    }

    /**
     * Deletes a locomotive type from the system.
     *
     * @param locomotiveTypeId the unique identifier of the locomotive type to delete
     * @return true if the locomotive type was deleted successfully, false otherwise
     */
    public boolean deleteLocomotiveType(Integer locomotiveTypeId) {
        return locomotiveTypes.removeIf(locomotiveType -> locomotiveType.getLocomotiveTypeId().equals(locomotiveTypeId));
    }
}

