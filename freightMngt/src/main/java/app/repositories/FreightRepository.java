package app.repositories;

import app.model.Freight;
import app.model.Freight.FreightStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing freights in the freight management system.
 * This class provides methods to add, retrieve, and manage freight entities.
 * It maintains a collection of all freights and allows searching by freight ID or status.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FreightRepository {

    private final List<Freight> freights = new ArrayList<>();

    /**
     * Adds a new freight to the repository if it doesn't already exist.
     *
     * @param freight the freight to add
     */
    public void addFreight(Freight freight) {
        if (freight != null && freight.getFreightId() != null) {
            if (!containsFreight(freight.getFreightId())) {
                freights.add(freight);
            }
        }
    }

    /**
     * Retrieves all freights in the repository.
     *
     * @return a new list containing all freights
     */
    public List<Freight> getAllFreights() {
        return new ArrayList<>(freights);
    }

    /**
     * Retrieves a specific freight by its ID.
     *
     * @param freightId the freight identifier
     * @return the matching freight, or null if not found
     */
    public Freight getFreightById(Integer freightId) {
        for (Freight freight : freights) {
            if (freightId.equals(freight.getFreightId())) {
                return freight;
            }
        }
        return null;
    }

    /**
     * Retrieves all freights with a specific status.
     *
     * @param status the freight status to filter by
     * @return a list of freights with the specified status
     */
    public List<Freight> getFreightsByStatus(FreightStatus status) {
        return freights.stream()
                .filter(freight -> freight.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all pending freights (that can be assigned a route).
     *
     * @return a list of pending freights
     */
    public List<Freight> getPendingFreights() {
        return getFreightsByStatus(FreightStatus.PENDING);
    }

    /**
     * Retrieves all freights without an assigned route.
     *
     * @return a list of freights without routes
     */
    public List<Freight> getFreightsWithoutRoute() {
        return freights.stream()
                .filter(freight -> freight.getAssignedRoute() == null)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing freight in the repository.
     *
     * @param freightId the freight identifier
     * @param updatedFreight the updated freight
     * @return true if the freight was updated successfully, false otherwise
     */
    public boolean updateFreight(Integer freightId, Freight updatedFreight) {
        Freight existingFreight = getFreightById(freightId);
        if (existingFreight != null) {
            int index = freights.indexOf(existingFreight);
            freights.set(index, updatedFreight);
            return true;
        }
        return false;
    }

    /**
     * Deletes a freight from the repository.
     *
     * @param freightId the freight identifier
     * @return true if the freight was deleted successfully, false otherwise
     */
    public boolean deleteFreight(Integer freightId) {
        return freights.removeIf(freight -> freightId.equals(freight.getFreightId()));
    }

    /**
     * Gets the next available freight ID.
     * Finds the maximum freight ID currently in use and returns the next sequential ID.
     *
     * @return the next available freight ID (max current ID + 1)
     */
    public Integer getNextFreightId() {
        int maxId = 0;
        for (Freight freight : freights) {
            if (freight.getFreightId() != null && freight.getFreightId() > maxId) {
                maxId = freight.getFreightId();
            }
        }
        return maxId + 1;
    }

    /**
     * Checks if a freight exists in the repository by its ID.
     *
     * @param freightId the freight identifier
     * @return true if the freight exists, false otherwise
     */
    private boolean containsFreight(Integer freightId) {
        return getFreightById(freightId) != null;
    }

    /**
     * Clears all freights from the repository.
     */
    public void clear() {
        freights.clear();
    }
}

