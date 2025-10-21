package app.repositories;

import app.model.Facility;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing facilities in the freight management system.
 * This class provides methods to add, retrieve, and manage facility entities.
 * It maintains a collection of all facilities and allows searching by facility ID or name.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FacilityRepository {

    private final List<Facility> facilities = new ArrayList<>();

    /**
     * Adds a new facility to the repository if it doesn't already exist.
     *
     * @param facility the facility to add
     */
    public void addFacility(Facility facility) {
        if (facility != null && facility.getFacilityId() != null) {
            if (!containsFacility(facility.getFacilityId())) {
                facilities.add(facility);
            }
        }
    }

    /**
     * Retrieves all facilities in the repository.
     *
     * @return a new list containing all facilities
     */
    public List<Facility> getAllFacilities() {
        return new ArrayList<>(facilities);
    }

    /**
     * Retrieves a specific facility by its ID.
     *
     * @param facilityId the facility identifier
     * @return the matching facility, or null if not found
     */
    public Facility getFacilityById(Integer facilityId) {
        for (Facility facility : facilities) {
            if (facilityId.equals(facility.getFacilityId())) {
                return facility;
            }
        }
        return null;
    }

    /**
     * Retrieves a specific facility by its name.
     *
     * @param name the facility name
     * @return the matching facility, or null if not found
     */
    public Facility getFacilityByName(String name) {
        for (Facility facility : facilities) {
            if (name != null && name.equals(facility.getName())) {
                return facility;
            }
        }
        return null;
    }

    /**
     * Updates an existing facility in the repository.
     *
     * @param facilityId the facility identifier
     * @param updatedFacility the updated facility
     * @return true if the facility was updated successfully, false otherwise
     */
    public boolean updateFacility(Integer facilityId, Facility updatedFacility) {
        Facility existingFacility = getFacilityById(facilityId);
        if (existingFacility != null) {
            int index = facilities.indexOf(existingFacility);
            facilities.set(index, updatedFacility);
            return true;
        }
        return false;
    }

    /**
     * Deletes a facility from the repository.
     *
     * @param facilityId the facility identifier
     * @return true if the facility was deleted successfully, false otherwise
     */
    public boolean deleteFacility(Integer facilityId) {
        return facilities.removeIf(facility -> facilityId.equals(facility.getFacilityId()));
    }

    /**
     * Gets the next available facility ID.
     * Finds the maximum facility ID currently in use and returns the next sequential ID.
     *
     * @return the next available facility ID (max current ID + 1)
     */
    public Integer getNextFacilityId() {
        int maxId = 0;
        for (Facility facility : facilities) {
            if (facility.getFacilityId() != null && facility.getFacilityId() > maxId) {
                maxId = facility.getFacilityId();
            }
        }
        return maxId + 1;
    }

    /**
     * Checks if a facility exists in the repository by its ID.
     *
     * @param facilityId the facility identifier
     * @return true if the facility exists, false otherwise
     */
    private boolean containsFacility(Integer facilityId) {
        return getFacilityById(facilityId) != null;
    }

    /**
     * Checks if a facility with the given name exists in the repository.
     *
     * @param name the facility name
     * @return true if a facility with this name exists, false otherwise
     */
    public boolean containsName(String name) {
        return getFacilityByName(name) != null;
    }

    /**
     * Clears all facilities from the repository.
     */
    public void clear() {
        facilities.clear();
    }
}

