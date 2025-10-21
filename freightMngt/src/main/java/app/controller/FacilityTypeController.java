package app.controller;

import app.model.FacilityType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing FacilityType entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for FacilityType objects.
 * This controller manages facility type data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FacilityTypeController {
    private List<FacilityType> facilityTypes;

    /**
     * Default constructor that initializes the facility type list.
     */
    public FacilityTypeController() {
        this.facilityTypes = new ArrayList<>();
    }

    /**
     * Creates and adds a new facility type to the system.
     *
     * @param facilityType the FacilityType object to be created
     * @return the created FacilityType object
     */
    public FacilityType createFacilityType(FacilityType facilityType) {
        if (facilityType != null) {
            facilityTypes.add(facilityType);
        }
        return facilityType;
    }

    /**
     * Retrieves all facility types from the system.
     *
     * @return a list of all FacilityType objects
     */
    public List<FacilityType> getAllFacilityTypes() {
        return new ArrayList<>(facilityTypes);
    }

    /**
     * Retrieves a facility type by its unique identifier.
     *
     * @param facilityTypeId the unique identifier of the facility type
     * @return an Optional containing the FacilityType if found, or empty if not found
     */
    public Optional<FacilityType> getFacilityTypeById(Integer facilityTypeId) {
        return facilityTypes.stream()
                .filter(facilityType -> facilityType.getFacilityTypeId().equals(facilityTypeId))
                .findFirst();
    }

    /**
     * Updates an existing facility type in the system.
     *
     * @param facilityTypeId the unique identifier of the facility type to update
     * @param updatedFacilityType the FacilityType object with updated information
     * @return true if the facility type was updated successfully, false otherwise
     */
    public boolean updateFacilityType(Integer facilityTypeId, FacilityType updatedFacilityType) {
        Optional<FacilityType> existingFacilityType = getFacilityTypeById(facilityTypeId);
        if (existingFacilityType.isPresent()) {
            int index = facilityTypes.indexOf(existingFacilityType.get());
            facilityTypes.set(index, updatedFacilityType);
            return true;
        }
        return false;
    }

    /**
     * Deletes a facility type from the system.
     *
     * @param facilityTypeId the unique identifier of the facility type to delete
     * @return true if the facility type was deleted successfully, false otherwise
     */
    public boolean deleteFacilityType(Integer facilityTypeId) {
        return facilityTypes.removeIf(facilityType -> facilityType.getFacilityTypeId().equals(facilityTypeId));
    }
}

