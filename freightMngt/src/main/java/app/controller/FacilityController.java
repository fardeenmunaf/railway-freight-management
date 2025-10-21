package app.controller;

import app.model.Facility;
import app.repositories.FacilityRepository;
import app.repositories.Repositories;
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
    private final FacilityRepository repository;

    /**
     * Default constructor that initializes the controller with the facility repository.
     */
    public FacilityController() {
        this.repository = Repositories.getInstance().getFacilityRepository();
    }

    /**
     * Creates and adds a new facility to the system.
     *
     * @param facility the Facility object to be created
     * @return the created Facility object
     */
    public Facility createFacility(Facility facility) {
        if (facility != null) {
            repository.addFacility(facility);
        }
        return facility;
    }

    /**
     * Retrieves all facilities from the system.
     *
     * @return a list of all Facility objects
     */
    public List<Facility> getAllFacilities() {
        return repository.getAllFacilities();
    }

    /**
     * Retrieves a facility by its unique identifier.
     *
     * @param facilityId the unique identifier of the facility
     * @return an Optional containing the Facility if found, or empty if not found
     */
    public Optional<Facility> getFacilityById(Integer facilityId) {
        Facility facility = repository.getFacilityById(facilityId);
        return Optional.ofNullable(facility);
    }

    /**
     * Updates an existing facility in the system.
     *
     * @param facilityId the unique identifier of the facility to update
     * @param updatedFacility the Facility object with updated information
     * @return true if the facility was updated successfully, false otherwise
     */
    public boolean updateFacility(Integer facilityId, Facility updatedFacility) {
        return repository.updateFacility(facilityId, updatedFacility);
    }

    /**
     * Deletes a facility from the system.
     *
     * @param facilityId the unique identifier of the facility to delete
     * @return true if the facility was deleted successfully, false otherwise
     */
    public boolean deleteFacility(Integer facilityId) {
        return repository.deleteFacility(facilityId);
    }

    /**
     * Gets the next available facility ID.
     *
     * @return the next facility ID to be used
     */
    public Integer getNextFacilityId() {
        return repository.getNextFacilityId();
    }

    /**
     * Clears all facilities from the system.
     */
    public void clearAllFacilities() {
        repository.clear();
    }
}
