package app.repositories;

import app.model.FreightRoute;
import app.model.FreightRoute.RouteType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Repository for managing freight routes in the freight management system.
 * This class provides methods to add, retrieve, and manage freight route entities.
 * It maintains a collection of all freight routes and allows searching by route ID or type.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FreightRouteRepository {

    private final List<FreightRoute> freightRoutes = new ArrayList<>();

    /**
     * Adds a new freight route to the repository if it doesn't already exist.
     *
     * @param freightRoute the freight route to add
     */
    public void addFreightRoute(FreightRoute freightRoute) {
        if (freightRoute != null && freightRoute.getRouteId() != null) {
            if (!containsFreightRoute(freightRoute.getRouteId())) {
                freightRoutes.add(freightRoute);
            }
        }
    }

    /**
     * Retrieves all freight routes in the repository.
     *
     * @return a new list containing all freight routes
     */
    public List<FreightRoute> getAllFreightRoutes() {
        return new ArrayList<>(freightRoutes);
    }

    /**
     * Retrieves a specific freight route by its ID.
     *
     * @param routeId the route identifier
     * @return the matching freight route, or null if not found
     */
    public FreightRoute getFreightRouteById(Integer routeId) {
        for (FreightRoute route : freightRoutes) {
            if (routeId.equals(route.getRouteId())) {
                return route;
            }
        }
        return null;
    }

    /**
     * Retrieves a specific freight route by its name.
     *
     * @param routeName the route name
     * @return the matching freight route, or null if not found
     */
    public FreightRoute getFreightRouteByName(String routeName) {
        for (FreightRoute route : freightRoutes) {
            if (routeName != null && routeName.equals(route.getRouteName())) {
                return route;
            }
        }
        return null;
    }

    /**
     * Retrieves all freight routes of a specific type.
     *
     * @param routeType the route type to filter by
     * @return a list of freight routes with the specified type
     */
    public List<FreightRoute> getFreightRoutesByType(RouteType routeType) {
        return freightRoutes.stream()
                .filter(route -> route.getRouteType() == routeType)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all simple routes.
     *
     * @return a list of simple freight routes
     */
    public List<FreightRoute> getSimpleRoutes() {
        return getFreightRoutesByType(RouteType.SIMPLE);
    }

    /**
     * Retrieves all complex routes.
     *
     * @return a list of complex freight routes
     */
    public List<FreightRoute> getComplexRoutes() {
        return getFreightRoutesByType(RouteType.COMPLEX);
    }

    /**
     * Updates an existing freight route in the repository.
     *
     * @param routeId the route identifier
     * @param updatedRoute the updated freight route
     * @return true if the route was updated successfully, false otherwise
     */
    public boolean updateFreightRoute(Integer routeId, FreightRoute updatedRoute) {
        FreightRoute existingRoute = getFreightRouteById(routeId);
        if (existingRoute != null) {
            int index = freightRoutes.indexOf(existingRoute);
            freightRoutes.set(index, updatedRoute);
            return true;
        }
        return false;
    }

    /**
     * Deletes a freight route from the repository.
     *
     * @param routeId the route identifier
     * @return true if the route was deleted successfully, false otherwise
     */
    public boolean deleteFreightRoute(Integer routeId) {
        return freightRoutes.removeIf(route -> routeId.equals(route.getRouteId()));
    }

    /**
     * Gets the next available freight route ID.
     * Finds the maximum route ID currently in use and returns the next sequential ID.
     *
     * @return the next available route ID (max current ID + 1)
     */
    public Integer getNextFreightRouteId() {
        int maxId = 0;
        for (FreightRoute route : freightRoutes) {
            if (route.getRouteId() != null && route.getRouteId() > maxId) {
                maxId = route.getRouteId();
            }
        }
        return maxId + 1;
    }

    /**
     * Checks if a freight route exists in the repository by its ID.
     *
     * @param routeId the route identifier
     * @return true if the route exists, false otherwise
     */
    private boolean containsFreightRoute(Integer routeId) {
        return getFreightRouteById(routeId) != null;
    }

    /**
     * Checks if a freight route with the given name exists in the repository.
     *
     * @param routeName the route name
     * @return true if a route with this name exists, false otherwise
     */
    public boolean containsRouteName(String routeName) {
        return getFreightRouteByName(routeName) != null;
    }

    /**
     * Clears all freight routes from the repository.
     */
    public void clear() {
        freightRoutes.clear();
    }
}

