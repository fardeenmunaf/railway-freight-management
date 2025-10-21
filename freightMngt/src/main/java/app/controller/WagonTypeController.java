package app.controller;

import app.model.WagonType;
import java.util.ArrayList;
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
    private List<WagonType> wagonTypes;

    /**
     * Default constructor that initializes the wagon type list.
     */
    public WagonTypeController() {
        this.wagonTypes = new ArrayList<>();
    }

    /**
     * Creates and adds a new wagon type to the system.
     *
     * @param wagonType the WagonType object to be created
     * @return the created WagonType object
     */
    public WagonType createWagonType(WagonType wagonType) {
        if (wagonType != null) {
            wagonTypes.add(wagonType);
        }
        return wagonType;
    }

    /**
     * Retrieves all wagon types from the system.
     *
     * @return a list of all WagonType objects
     */
    public List<WagonType> getAllWagonTypes() {
        return new ArrayList<>(wagonTypes);
    }

    /**
     * Retrieves a wagon type by its unique identifier.
     *
     * @param wagonTypeId the unique identifier of the wagon type
     * @return an Optional containing the WagonType if found, or empty if not found
     */
    public Optional<WagonType> getWagonTypeById(Integer wagonTypeId) {
        return wagonTypes.stream()
                .filter(wagonType -> wagonType.getWagonTypeId().equals(wagonTypeId))
                .findFirst();
    }

    /**
     * Updates an existing wagon type in the system.
     *
     * @param wagonTypeId the unique identifier of the wagon type to update
     * @param updatedWagonType the WagonType object with updated information
     * @return true if the wagon type was updated successfully, false otherwise
     */
    public boolean updateWagonType(Integer wagonTypeId, WagonType updatedWagonType) {
        Optional<WagonType> existingWagonType = getWagonTypeById(wagonTypeId);
        if (existingWagonType.isPresent()) {
            int index = wagonTypes.indexOf(existingWagonType.get());
            wagonTypes.set(index, updatedWagonType);
            return true;
        }
        return false;
    }

    /**
     * Deletes a wagon type from the system.
     *
     * @param wagonTypeId the unique identifier of the wagon type to delete
     * @return true if the wagon type was deleted successfully, false otherwise
     */
    public boolean deleteWagonType(Integer wagonTypeId) {
        return wagonTypes.removeIf(wagonType -> wagonType.getWagonTypeId().equals(wagonTypeId));
    }
}

