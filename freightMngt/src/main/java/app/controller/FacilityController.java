package app.controller;

import app.model.Facility;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Facility entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Facility objects.
 * This controller manages facility data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FacilityController {
    private List<Facility> facilities;

    /**
     * Default constructor that initializes the facility list.
     */
    public FacilityController() {
        this.facilities = new ArrayList<>();
    }

    /**
     * Creates and adds a new facility to the system.
     *
     * @param facility the Facility object to be created
     * @return the created Facility object
     */
    public Facility createFacility(Facility facility) {
        if (facility != null) {
            facilities.add(facility);
        }
        return facility;
    }

    /**
     * Retrieves all facilities from the system.
     *
     * @return a list of all Facility objects
     */
    public List<Facility> getAllFacilities() {
        return new ArrayList<>(facilities);
    }

    /**
     * Retrieves a facility by its unique identifier.
     *
     * @param facilityId the unique identifier of the facility
     * @return an Optional containing the Facility if found, or empty if not found
     */
    public Optional<Facility> getFacilityById(Integer facilityId) {
        return facilities.stream()
                .filter(facility -> facility.getFacilityId().equals(facilityId))
                .findFirst();
    }

    /**
     * Updates an existing facility in the system.
     *
     * @param facilityId the unique identifier of the facility to update
     * @param updatedFacility the Facility object with updated information
     * @return true if the facility was updated successfully, false otherwise
     */
    public boolean updateFacility(Integer facilityId, Facility updatedFacility) {
        Optional<Facility> existingFacility = getFacilityById(facilityId);
        if (existingFacility.isPresent()) {
            int index = facilities.indexOf(existingFacility.get());
            facilities.set(index, updatedFacility);
            return true;
        }
        return false;
    }

    /**
     * Deletes a facility from the system.
     *
     * @param facilityId the unique identifier of the facility to delete
     * @return true if the facility was deleted successfully, false otherwise
     */
    public boolean deleteFacility(Integer facilityId) {
        return facilities.removeIf(facility -> facility.getFacilityId().equals(facilityId));
    }
}

