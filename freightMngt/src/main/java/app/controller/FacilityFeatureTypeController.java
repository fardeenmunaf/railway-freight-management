/**
 * Controller class for managing FacilityFeatureType entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for FacilityFeatureType objects.
 * This controller manages facility feature type data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FacilityFeatureTypeController {
    private List<FacilityFeatureType> facilityFeatureTypes;

    /**
     * Default constructor that initializes the facility feature type list.
     */
    public FacilityFeatureTypeController() {
        this.facilityFeatureTypes = new ArrayList<>();
    }

    /**
     * Creates and adds a new facility feature type to the system.
     *
     * @param facilityFeatureType the FacilityFeatureType object to be created
     * @return the created FacilityFeatureType object
     */
    public FacilityFeatureType createFacilityFeatureType(FacilityFeatureType facilityFeatureType) {
        if (facilityFeatureType != null) {
            facilityFeatureTypes.add(facilityFeatureType);
        }
        return facilityFeatureType;
    }

    /**
     * Retrieves all facility feature types from the system.
     *
     * @return a list of all FacilityFeatureType objects
     */
    public List<FacilityFeatureType> getAllFacilityFeatureTypes() {
        return new ArrayList<>(facilityFeatureTypes);
    }

    /**
     * Retrieves a facility feature type by its unique identifier.
     *
     * @param facilityFeatureTypeId the unique identifier of the facility feature type
     * @return an Optional containing the FacilityFeatureType if found, or empty if not found
     */
    public Optional<FacilityFeatureType> getFacilityFeatureTypeById(Integer facilityFeatureTypeId) {
        return facilityFeatureTypes.stream()
                .filter(facilityFeatureType -> facilityFeatureType.getFacilityFeatureTypeId().equals(facilityFeatureTypeId))
                .findFirst();
    }

    /**
     * Updates an existing facility feature type in the system.
     *
     * @param facilityFeatureTypeId the unique identifier of the facility feature type to update
     * @param updatedFacilityFeatureType the FacilityFeatureType object with updated information
     * @return true if the facility feature type was updated successfully, false otherwise
     */
    public boolean updateFacilityFeatureType(Integer facilityFeatureTypeId, FacilityFeatureType updatedFacilityFeatureType) {
        Optional<FacilityFeatureType> existingFacilityFeatureType = getFacilityFeatureTypeById(facilityFeatureTypeId);
        if (existingFacilityFeatureType.isPresent()) {
            int index = facilityFeatureTypes.indexOf(existingFacilityFeatureType.get());
            facilityFeatureTypes.set(index, updatedFacilityFeatureType);
            return true;
        }
        return false;
    }

    /**
     * Deletes a facility feature type from the system.
     *
     * @param facilityFeatureTypeId the unique identifier of the facility feature type to delete
     * @return true if the facility feature type was deleted successfully, false otherwise
     */
    public boolean deleteFacilityFeatureType(Integer facilityFeatureTypeId) {
        return facilityFeatureTypes.removeIf(facilityFeatureType -> facilityFeatureType.getFacilityFeatureTypeId().equals(facilityFeatureTypeId));
    }
}
package app.controller;

import app.model.FacilityFeatureType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


