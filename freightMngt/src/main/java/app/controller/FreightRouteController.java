package app.controller;

import app.model.*;
import app.model.FreightRoute.RouteType;
import app.repositories.FacilityRepository;
import app.repositories.FreightRouteRepository;
import app.repositories.LineSegmentRepository;
import app.repositories.Repositories;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing FreightRoute entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for FreightRoute objects.
 * This controller manages route creation, validation, and segment management in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FreightRouteController {
    private final FreightRouteRepository freightRouteRepository;
    private final FacilityRepository facilityRepository;
    private final LineSegmentRepository lineSegmentRepository;

    /**
     * Default constructor that initializes the controller with the freight route repository.
     */
    public FreightRouteController() {
        this.freightRouteRepository = Repositories.getInstance().getFreightRouteRepository();
        this.facilityRepository = Repositories.getInstance().getFacilityRepository();
        this.lineSegmentRepository = Repositories.getInstance().getLineSegmentRepository();
    }

    /**
     * Creates and adds a new freight route to the system.
     * Validates that the start and end facilities exist.
     *
     * @param freightRoute the FreightRoute object to be created
     * @return the created FreightRoute object
     * @throws IllegalArgumentException if referenced facilities do not exist
     */
    public FreightRoute createFreightRoute(FreightRoute freightRoute) {
        if (freightRoute != null) {
            validateRouteReferences(freightRoute);
            freightRouteRepository.addFreightRoute(freightRoute);
        }
        return freightRoute;
    }

    /**
     * Creates a simple route (direct path from start to end).
     *
     * @param routeId the unique identifier for the route
     * @param routeName the name of the route
     * @param startFacilityId the ID of the start facility
     * @param endFacilityId the ID of the end facility
     * @param notes additional notes about the route
     * @return the created simple route
     * @throws IllegalArgumentException if facilities do not exist
     */
    public FreightRoute createSimpleRoute(Integer routeId, String routeName,
                                         Integer startFacilityId, Integer endFacilityId, String notes) {
        Facility startFacility = facilityRepository.getFacilityById(startFacilityId);
        Facility endFacility = facilityRepository.getFacilityById(endFacilityId);

        if (startFacility == null) {
            throw new IllegalArgumentException("Start facility with ID " + startFacilityId + " does not exist");
        }
        if (endFacility == null) {
            throw new IllegalArgumentException("End facility with ID " + endFacilityId + " does not exist");
        }

        FreightRoute route = new FreightRoute(routeId, routeName, RouteType.SIMPLE,
                                             startFacility, endFacility, notes);
        freightRouteRepository.addFreightRoute(route);
        return route;
    }

    /**
     * Creates a complex route with multiple ordered segments.
     *
     * @param routeId the unique identifier for the route
     * @param routeName the name of the route
     * @param startFacilityId the ID of the start facility
     * @param endFacilityId the ID of the end facility
     * @param segmentDefinitions list of segment definitions (fromFacilityId, toFacilityId, lineSegmentId)
     * @param notes additional notes about the route
     * @return the created complex route with segments
     * @throws IllegalArgumentException if facilities or line segments do not exist
     */
    public FreightRoute createComplexRoute(Integer routeId, String routeName,
                                          Integer startFacilityId, Integer endFacilityId,
                                          List<SegmentDefinition> segmentDefinitions, String notes) {
        Facility startFacility = facilityRepository.getFacilityById(startFacilityId);
        Facility endFacility = facilityRepository.getFacilityById(endFacilityId);

        if (startFacility == null) {
            throw new IllegalArgumentException("Start facility with ID " + startFacilityId + " does not exist");
        }
        if (endFacility == null) {
            throw new IllegalArgumentException("End facility with ID " + endFacilityId + " does not exist");
        }

        FreightRoute route = new FreightRoute(routeId, routeName, RouteType.COMPLEX,
                                             startFacility, endFacility, notes);

        // Add segments in order
        if (segmentDefinitions != null) {
            for (int i = 0; i < segmentDefinitions.size(); i++) {
                SegmentDefinition def = segmentDefinitions.get(i);
                FreightRouteSegment segment = createRouteSegment(route, i + 1, def);
                route.addRouteSegment(segment);
            }
        }

        freightRouteRepository.addFreightRoute(route);
        return route;
    }

    /**
     * Helper method to create a route segment.
     */
    private FreightRouteSegment createRouteSegment(FreightRoute route, int sequenceOrder,
                                                   SegmentDefinition def) {
        Facility fromFacility = facilityRepository.getFacilityById(def.fromFacilityId);
        Facility toFacility = facilityRepository.getFacilityById(def.toFacilityId);
        LineSegment lineSegment = lineSegmentRepository.getLineSegmentById(def.lineSegmentId);

        if (fromFacility == null) {
            throw new IllegalArgumentException("From facility with ID " + def.fromFacilityId + " does not exist");
        }
        if (toFacility == null) {
            throw new IllegalArgumentException("To facility with ID " + def.toFacilityId + " does not exist");
        }
        if (lineSegment == null) {
            throw new IllegalArgumentException("Line segment with ID " + def.lineSegmentId + " does not exist");
        }

        return new FreightRouteSegment(def.segmentId, route, sequenceOrder,
                                      fromFacility, toFacility, lineSegment, def.notes);
    }

    /**
     * Retrieves all freight routes from the system.
     *
     * @return a list of all FreightRoute objects
     */
    public List<FreightRoute> getAllFreightRoutes() {
        return freightRouteRepository.getAllFreightRoutes();
    }

    /**
     * Retrieves a freight route by its unique identifier.
     *
     * @param routeId the unique identifier of the route
     * @return an Optional containing the FreightRoute if found, or empty if not found
     */
    public Optional<FreightRoute> getFreightRouteById(Integer routeId) {
        FreightRoute route = freightRouteRepository.getFreightRouteById(routeId);
        return Optional.ofNullable(route);
    }

    /**
     * Retrieves a freight route by its name.
     *
     * @param routeName the name of the route
     * @return an Optional containing the FreightRoute if found, or empty if not found
     */
    public Optional<FreightRoute> getFreightRouteByName(String routeName) {
        FreightRoute route = freightRouteRepository.getFreightRouteByName(routeName);
        return Optional.ofNullable(route);
    }

    /**
     * Retrieves all routes of a specific type.
     *
     * @param routeType the route type to filter by
     * @return a list of routes with the specified type
     */
    public List<FreightRoute> getFreightRoutesByType(RouteType routeType) {
        return freightRouteRepository.getFreightRoutesByType(routeType);
    }

    /**
     * Retrieves all simple routes.
     *
     * @return a list of simple freight routes
     */
    public List<FreightRoute> getSimpleRoutes() {
        return freightRouteRepository.getSimpleRoutes();
    }

    /**
     * Retrieves all complex routes.
     *
     * @return a list of complex freight routes
     */
    public List<FreightRoute> getComplexRoutes() {
        return freightRouteRepository.getComplexRoutes();
    }

    /**
     * Updates an existing freight route in the system.
     *
     * @param routeId the unique identifier of the route to update
     * @param updatedRoute the FreightRoute object with updated information
     * @return true if the route was updated successfully, false otherwise
     */
    public boolean updateFreightRoute(Integer routeId, FreightRoute updatedRoute) {
        validateRouteReferences(updatedRoute);
        return freightRouteRepository.updateFreightRoute(routeId, updatedRoute);
    }

    /**
     * Deletes a freight route from the system.
     *
     * @param routeId the unique identifier of the route to delete
     * @return true if the route was deleted successfully, false otherwise
     */
    public boolean deleteFreightRoute(Integer routeId) {
        return freightRouteRepository.deleteFreightRoute(routeId);
    }

    /**
     * Gets the next available freight route ID.
     *
     * @return the next route ID to be used
     */
    public Integer getNextFreightRouteId() {
        return freightRouteRepository.getNextFreightRouteId();
    }

    /**
     * Checks if a route with the given name exists.
     *
     * @param routeName the route name
     * @return true if a route with this name exists, false otherwise
     */
    public boolean containsRouteName(String routeName) {
        return freightRouteRepository.containsRouteName(routeName);
    }

    /**
     * Validates that all referenced facilities in a route exist.
     *
     * @param route the route to validate
     * @throws IllegalArgumentException if any referenced facility does not exist
     */
    private void validateRouteReferences(FreightRoute route) {
        if (route == null) {
            return;
        }

        if (route.getStartFacility() != null) {
            Integer startId = route.getStartFacility().getFacilityId();
            if (startId != null && facilityRepository.getFacilityById(startId) == null) {
                throw new IllegalArgumentException("Start facility with ID " + startId + " does not exist");
            }
        }

        if (route.getEndFacility() != null) {
            Integer endId = route.getEndFacility().getFacilityId();
            if (endId != null && facilityRepository.getFacilityById(endId) == null) {
                throw new IllegalArgumentException("End facility with ID " + endId + " does not exist");
            }
        }

        // Validate segments if present
        for (FreightRouteSegment segment : route.getRouteSegments()) {
            validateSegmentReferences(segment);
        }
    }

    /**
     * Validates that all referenced entities in a segment exist.
     *
     * @param segment the segment to validate
     * @throws IllegalArgumentException if any referenced entity does not exist
     */
    private void validateSegmentReferences(FreightRouteSegment segment) {
        if (segment.getFromFacility() != null) {
            Integer fromId = segment.getFromFacility().getFacilityId();
            if (fromId != null && facilityRepository.getFacilityById(fromId) == null) {
                throw new IllegalArgumentException("From facility with ID " + fromId + " does not exist");
            }
        }

        if (segment.getToFacility() != null) {
            Integer toId = segment.getToFacility().getFacilityId();
            if (toId != null && facilityRepository.getFacilityById(toId) == null) {
                throw new IllegalArgumentException("To facility with ID " + toId + " does not exist");
            }
        }

        if (segment.getLineSegment() != null) {
            Integer lineSegmentId = segment.getLineSegment().getSegmentId();
            if (lineSegmentId != null && lineSegmentRepository.getLineSegmentById(lineSegmentId) == null) {
                throw new IllegalArgumentException("Line segment with ID " + lineSegmentId + " does not exist");
            }
        }
    }

    /**
     * Gets a formatted route summary with all stations in sequence.
     *
     * @param routeId the unique identifier of the route
     * @return a formatted string with the route summary, or null if route not found
     */
    public String getRouteSummary(Integer routeId) {
        FreightRoute route = freightRouteRepository.getFreightRouteById(routeId);
        if (route == null) {
            return null;
        }
        return route.getRouteSummary();
    }

    /**
     * Clears all freight routes from the system.
     */
    public void clearAllFreightRoutes() {
        freightRouteRepository.clear();
    }

    /**
     * Helper class to define a route segment when creating complex routes.
     */
    public static class SegmentDefinition {
        public Integer segmentId;
        public Integer fromFacilityId;
        public Integer toFacilityId;
        public Integer lineSegmentId;
        public String notes;

        public SegmentDefinition(Integer segmentId, Integer fromFacilityId, Integer toFacilityId,
                               Integer lineSegmentId, String notes) {
            this.segmentId = segmentId;
            this.fromFacilityId = fromFacilityId;
            this.toFacilityId = toFacilityId;
            this.lineSegmentId = lineSegmentId;
            this.notes = notes;
        }
    }
}

