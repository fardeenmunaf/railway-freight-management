package app.repositories;

import app.model.RoutePoint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository for managing RoutePoint entities in memory.
 * Provides CRUD operations and query methods for route points.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class RoutePointRepository {

    private final Map<Integer, RoutePoint> routePoints;
    private Integer nextId;

    /**
     * Constructor that initializes the repository with an empty map.
     */
    public RoutePointRepository() {
        this.routePoints = new HashMap<>();
        this.nextId = 1;
    }

    /**
     * Adds a new route point to the repository.
     *
     * @param routePoint the route point to add
     * @return the added route point
     */
    public RoutePoint addRoutePoint(RoutePoint routePoint) {
        if (routePoint.getRoutePointId() == null) {
            routePoint.setRoutePointId(nextId++);
        } else {
            if (routePoint.getRoutePointId() >= nextId) {
                nextId = routePoint.getRoutePointId() + 1;
            }
        }
        routePoints.put(routePoint.getRoutePointId(), routePoint);
        return routePoint;
    }

    /**
     * Gets a route point by its ID.
     *
     * @param routePointId the ID of the route point to retrieve
     * @return the route point, or null if not found
     */
    public RoutePoint getRoutePointById(Integer routePointId) {
        return routePoints.get(routePointId);
    }

    /**
     * Gets all route points.
     *
     * @return a list of all route points
     */
    public List<RoutePoint> getAllRoutePoints() {
        return new ArrayList<>(routePoints.values());
    }

    /**
     * Updates an existing route point.
     *
     * @param routePointId the ID of the route point to update
     * @param updatedRoutePoint the updated route point information
     * @return the updated route point, or null if not found
     */
    public RoutePoint updateRoutePoint(Integer routePointId, RoutePoint updatedRoutePoint) {
        if (routePoints.containsKey(routePointId)) {
            updatedRoutePoint.setRoutePointId(routePointId);
            routePoints.put(routePointId, updatedRoutePoint);
            return updatedRoutePoint;
        }
        return null;
    }

    /**
     * Deletes a route point by its ID.
     *
     * @param routePointId the ID of the route point to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteRoutePoint(Integer routePointId) {
        return routePoints.remove(routePointId) != null;
    }

    /**
     * Gets all route points for a specific train dispatch.
     *
     * @param dispatchId the ID of the train dispatch
     * @return a list of route points for the dispatch, ordered by sequence
     */
    public List<RoutePoint> getRoutePointsByDispatch(Integer dispatchId) {
        return routePoints.values().stream()
                .filter(rp -> rp.getTrainDispatch() != null &&
                        rp.getTrainDispatch().getDispatchId().equals(dispatchId))
                .sorted((rp1, rp2) -> rp1.getSequenceOrder().compareTo(rp2.getSequenceOrder()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all route points that have crossing operations.
     *
     * @return a list of route points with crossing operations
     */
    public List<RoutePoint> getRoutePointsWithCrossings() {
        return routePoints.values().stream()
                .filter(rp -> rp.getCrossingOperation() != null &&
                        !"NONE".equals(rp.getCrossingOperation()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all route points on single-line segments.
     *
     * @return a list of route points on single-line segments
     */
    public List<RoutePoint> getRoutePointsOnSingleLineSegments() {
        return routePoints.values().stream()
                .filter(rp -> Boolean.TRUE.equals(rp.getIsSingleLineSegment()))
                .collect(Collectors.toList());
    }

    /**
     * Deletes all route points for a specific dispatch.
     *
     * @param dispatchId the ID of the train dispatch
     * @return the number of deleted route points
     */
    public int deleteRoutePointsByDispatch(Integer dispatchId) {
        List<Integer> idsToDelete = routePoints.values().stream()
                .filter(rp -> rp.getTrainDispatch() != null &&
                        rp.getTrainDispatch().getDispatchId().equals(dispatchId))
                .map(RoutePoint::getRoutePointId)
                .collect(Collectors.toList());

        idsToDelete.forEach(routePoints::remove);
        return idsToDelete.size();
    }

    /**
     * Clears all route points from the repository.
     */
    public void clearAll() {
        routePoints.clear();
        nextId = 1;
    }

    /**
     * Gets the count of all route points.
     *
     * @return the total number of route points
     */
    public int getCount() {
        return routePoints.size();
    }
}

