package app.controller;

import app.model.Freight;
import app.model.Freight.FreightStatus;
import app.model.FreightRoute;
import app.repositories.FreightRepository;
import app.repositories.FreightRouteRepository;
import app.repositories.Repositories;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Freight entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Freight objects.
 * This controller manages freight data and route assignments in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FreightController {
    private final FreightRepository freightRepository;
    private final FreightRouteRepository freightRouteRepository;

    /**
     * Default constructor that initializes the controller with the freight repository.
     */
    public FreightController() {
        this.freightRepository = Repositories.getInstance().getFreightRepository();
        this.freightRouteRepository = Repositories.getInstance().getFreightRouteRepository();
    }

    /**
     * Creates and adds a new freight to the system.
     *
     * @param freight the Freight object to be created
     * @return the created Freight object
     */
    public Freight createFreight(Freight freight) {
        if (freight != null) {
            freightRepository.addFreight(freight);
        }
        return freight;
    }

    /**
     * Retrieves all freights from the system.
     *
     * @return a list of all Freight objects
     */
    public List<Freight> getAllFreights() {
        return freightRepository.getAllFreights();
    }

    /**
     * Retrieves a freight by its unique identifier.
     *
     * @param freightId the unique identifier of the freight
     * @return an Optional containing the Freight if found, or empty if not found
     */
    public Optional<Freight> getFreightById(Integer freightId) {
        Freight freight = freightRepository.getFreightById(freightId);
        return Optional.ofNullable(freight);
    }

    /**
     * Retrieves all freights with a specific status.
     *
     * @param status the freight status to filter by
     * @return a list of freights with the specified status
     */
    public List<Freight> getFreightsByStatus(FreightStatus status) {
        return freightRepository.getFreightsByStatus(status);
    }

    /**
     * Retrieves all pending freights (that can be assigned a route).
     *
     * @return a list of pending freights
     */
    public List<Freight> getPendingFreights() {
        return freightRepository.getPendingFreights();
    }

    /**
     * Retrieves all freights without an assigned route.
     *
     * @return a list of freights without routes
     */
    public List<Freight> getFreightsWithoutRoute() {
        return freightRepository.getFreightsWithoutRoute();
    }

    /**
     * Updates an existing freight in the system.
     *
     * @param freightId the unique identifier of the freight to update
     * @param updatedFreight the Freight object with updated information
     * @return true if the freight was updated successfully, false otherwise
     */
    public boolean updateFreight(Integer freightId, Freight updatedFreight) {
        return freightRepository.updateFreight(freightId, updatedFreight);
    }

    /**
     * Deletes a freight from the system.
     *
     * @param freightId the unique identifier of the freight to delete
     * @return true if the freight was deleted successfully, false otherwise
     */
    public boolean deleteFreight(Integer freightId) {
        return freightRepository.deleteFreight(freightId);
    }

    /**
     * Gets the next available freight ID.
     *
     * @return the next freight ID to be used
     */
    public Integer getNextFreightId() {
        return freightRepository.getNextFreightId();
    }

    /**
     * Assigns a route to a pending freight.
     * Business rule: Only pending freights can be assigned a route.
     *
     * @param freightId the unique identifier of the freight
     * @param routeId the unique identifier of the route
     * @return true if the route was assigned successfully, false otherwise
     * @throws IllegalStateException if the freight is not pending
     * @throws IllegalArgumentException if the freight or route does not exist
     */
    public boolean assignRouteToFreight(Integer freightId, Integer routeId) {
        // Validate freight exists
        Freight freight = freightRepository.getFreightById(freightId);
        if (freight == null) {
            throw new IllegalArgumentException("Freight with ID " + freightId + " does not exist");
        }

        // Validate freight is pending
        if (!freight.isPending()) {
            throw new IllegalStateException("Cannot assign route to freight with status: " + freight.getStatus() +
                                          ". Only PENDING freights can be assigned a route.");
        }

        // Validate route exists
        FreightRoute route = freightRouteRepository.getFreightRouteById(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Route with ID " + routeId + " does not exist");
        }

        // Validate route is valid
        if (!route.isValid()) {
            throw new IllegalArgumentException("Route with ID " + routeId + " is not valid");
        }

        // Assign the route
        freight.setAssignedRoute(route);
        return freightRepository.updateFreight(freightId, freight);
    }

    /**
     * Removes the assigned route from a freight.
     *
     * @param freightId the unique identifier of the freight
     * @return true if the route was removed successfully, false otherwise
     */
    public boolean removeRouteFromFreight(Integer freightId) {
        Freight freight = freightRepository.getFreightById(freightId);
        if (freight == null) {
            return false;
        }

        freight.setAssignedRoute(null);
        return freightRepository.updateFreight(freightId, freight);
    }

    /**
     * Updates the status of a freight.
     *
     * @param freightId the unique identifier of the freight
     * @param newStatus the new status to set
     * @return true if the status was updated successfully, false otherwise
     */
    public boolean updateFreightStatus(Integer freightId, FreightStatus newStatus) {
        Freight freight = freightRepository.getFreightById(freightId);
        if (freight == null) {
            return false;
        }

        freight.setStatus(newStatus);
        return freightRepository.updateFreight(freightId, freight);
    }

    /**
     * Clears all freights from the system.
     */
    public void clearAllFreights() {
        freightRepository.clear();
    }
}

