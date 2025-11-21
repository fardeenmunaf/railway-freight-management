package app.service.sprint2;

import app.model.*;
import app.repositories.*;
import app.service.sprint1.TravelTimeFeature;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class for train dispatch scheduling operations.
 * Handles route planning, speed calculations, travel time estimation,
 * and crossing operation planning for train dispatches.
 *
 * <p>Version 1.1: Integrated with TravelTimeFeature for optimized route calculations
 * using Dijkstra's algorithm instead of manual segment-by-segment calculation.</p>
 *
 * @author G113 2DK
 * @version 1.1
 */
public class TrainDispatchService {

    private final TrainDispatchRepository dispatchRepository;
    private final RoutePointRepository routePointRepository;
    private final TrainServiceCompositionRepository compositionRepository;
    private final LineSegmentRepository lineSegmentRepository;
    private final LineSegmentFacilityRepository lineSegmentFacilityRepository;
    private final FacilityRepository facilityRepository;
    private final LocomotiveRepository locomotiveRepository;
    private final TravelTimeFeature.TravelTimeCalculator travelTimeCalculator;

    /**
     * Constructor with dependencies.
     *
     * @param dispatchRepository the dispatch repository
     * @param routePointRepository the route point repository
     * @param compositionRepository the train service composition repository
     * @param lineSegmentRepository the line segment repository
     * @param lineSegmentFacilityRepository the line segment facility repository
     * @param facilityRepository the facility repository
     * @param locomotiveRepository the locomotive repository
     */
    public TrainDispatchService(TrainDispatchRepository dispatchRepository,
                               RoutePointRepository routePointRepository,
                               TrainServiceCompositionRepository compositionRepository,
                               LineSegmentRepository lineSegmentRepository,
                               LineSegmentFacilityRepository lineSegmentFacilityRepository,
                               FacilityRepository facilityRepository,
                               LocomotiveRepository locomotiveRepository) {
        this.dispatchRepository = dispatchRepository;
        this.routePointRepository = routePointRepository;
        this.compositionRepository = compositionRepository;
        this.lineSegmentRepository = lineSegmentRepository;
        this.lineSegmentFacilityRepository = lineSegmentFacilityRepository;
        this.facilityRepository = facilityRepository;
        this.locomotiveRepository = locomotiveRepository;

        // Initialize TravelTimeCalculator for optimized route calculations
        this.travelTimeCalculator = TravelTimeFeature.create(
            facilityRepository,
            lineSegmentRepository,
            lineSegmentFacilityRepository,
            locomotiveRepository
        );
    }

    /**
     * Creates a new train dispatch with manual route definition.
     *
     * @param trainService the train service to dispatch
     * @param plannedDepartureTime the planned departure time
     * @param routeDefinition list of facility IDs defining the route
     * @return the created train dispatch
     */
    public TrainDispatch createManualDispatch(TrainService trainService,
                                             LocalDateTime plannedDepartureTime,
                                             List<Integer> routeDefinition) {
        // Create the dispatch
        TrainDispatch dispatch = new TrainDispatch();
        dispatch.setTrainService(trainService);
        dispatch.setPlannedDepartureTime(plannedDepartureTime);
        dispatch.setRouteType("MANUAL");
        dispatch.setStatus("PLANNED");

        // Save dispatch first to get ID
        dispatch = dispatchRepository.addTrainDispatch(dispatch);

        // Build route points
        List<RoutePoint> routePoints = buildRoutePoints(dispatch, routeDefinition, plannedDepartureTime);
        dispatch.setRoute(routePoints);

        // Calculate totals
        calculateRouteTotals(dispatch);

        // Update dispatch with calculated values
        dispatchRepository.updateTrainDispatch(dispatch.getDispatchId(), dispatch);

        return dispatch;
    }

    /**
     * Builds route points from a list of facility IDs using TravelTimeFeature for optimized calculations.
     * Falls back to manual calculation if no locomotive is found.
     *
     * @param dispatch the train dispatch
     * @param facilityIds list of facility IDs
     * @param startTime the departure time
     * @return list of route points
     */
    private List<RoutePoint> buildRoutePoints(TrainDispatch dispatch, List<Integer> facilityIds, LocalDateTime startTime) {
        List<RoutePoint> routePoints = new ArrayList<>();
        double cumulativeDistance = 0.0;
        double cumulativeTime = 0.0;
        LocalDateTime currentTime = startTime;

        // Try to get locomotive ID, if available use TravelTimeCalculator
        Integer locomotiveId = getLocomotiveFromTrainService(dispatch.getTrainService());
        boolean useTravelTimeCalculator = (locomotiveId != null);

        for (int i = 0; i < facilityIds.size(); i++) {
            Facility facility = facilityRepository.getFacilityById(facilityIds.get(i));
            if (facility == null) continue;

            RoutePoint point = new RoutePoint();
            point.setTrainDispatch(dispatch);
            point.setSequenceOrder(i + 1);
            point.setFacility(facility);

            if (i == 0) {
                // First point - departure
                point.setDistanceFromStartKm(0.0);
                point.setCumulativeTravelTimeMinutes(0.0);
                point.setEstimatedArrivalTime(startTime);
                point.setEstimatedDepartureTime(startTime);
                point.setCrossingOperation("NONE");
            } else {
                // Subsequent points - calculate travel time
                Integer previousFacilityId = facilityIds.get(i - 1);
                Facility previousFacility = facilityRepository.getFacilityById(previousFacilityId);

                boolean calculated = false;

                // Try using TravelTimeCalculator if locomotive available
                if (useTravelTimeCalculator && locomotiveId != null) {
                    try {
                        // Calculate travel time using Dijkstra's algorithm
                        double travelTimeMinutes = travelTimeCalculator.calculateTravelTimeMinutes(
                            previousFacilityId,
                            facility.getFacilityId(),
                            locomotiveId
                        );

                        // Get the calculated distance from the last calculation
                        double segmentDistance = travelTimeCalculator.getDistanceKm();

                        cumulativeDistance += segmentDistance;
                        cumulativeTime += travelTimeMinutes;
                        currentTime = currentTime.plusMinutes((long) travelTimeMinutes);

                        point.setEstimatedArrivalTime(currentTime);
                        calculated = true;

                    } catch (TravelTimeFeature.NoDirectConnectionException e) {
                        // No direct connection - fall through to manual calculation
                        System.out.println("⚠ No connection found via TravelTime, using manual calculation");
                    } catch (TravelTimeFeature.InvalidSpeedException e) {
                        // Invalid speed - fall through to manual calculation
                        System.out.println("⚠ Invalid speed in TravelTime, using manual calculation");
                    }
                }

                // Manual fallback calculation
                if (!calculated) {
                    LineSegment segment = findSegmentBetweenFacilities(previousFacility, facility);

                    if (segment != null) {
                        // Calculate speed and time for this segment
                        double segmentSpeed = calculateSegmentSpeed(dispatch.getTrainService(), segment);
                        double segmentDistance = segment.getLengthKm() != null ? segment.getLengthKm() : 0.0;
                        double segmentTime = (segmentDistance / segmentSpeed) * 60.0; // Convert to minutes

                        cumulativeDistance += segmentDistance;
                        cumulativeTime += segmentTime;
                        currentTime = currentTime.plusMinutes((long) segmentTime);

                        point.setEstimatedArrivalTime(currentTime);
                    } else {
                        // No segment found - use default
                        point.setEstimatedArrivalTime(currentTime);
                        point.setNotes("No segment connection found");
                    }
                }

                // Find direct segment for crossing operation analysis
                LineSegment segment = findSegmentBetweenFacilities(previousFacility, facility);

                if (segment != null) {
                    routePoints.get(i - 1).setSegmentToNext(segment);

                    // Check if single-line segment and plan crossing if needed
                    boolean isSingleLine = checkIfSingleLine(segment);
                    point.setIsSingleLineSegment(isSingleLine);

                    if (isSingleLine) {
                        planCrossingOperation(point);
                    } else {
                        point.setCrossingOperation("NONE");
                    }
                } else {
                    point.setCrossingOperation("NONE");
                }

                // Add stop time at intermediate stations (e.g., 5 minutes)
                int stopTime = (i < facilityIds.size() - 1) ? 5 : 0;
                if (stopTime > 0) {
                    currentTime = currentTime.plusMinutes(stopTime);
                    cumulativeTime += stopTime;
                }
                point.setEstimatedDepartureTime(currentTime);
            }

            point.setDistanceFromStartKm(cumulativeDistance);
            point.setCumulativeTravelTimeMinutes(cumulativeTime);

            routePoints.add(routePointRepository.addRoutePoint(point));
        }

        return routePoints;
    }

    /**
     * Gets locomotive ID from train service composition.
     *
     * @param trainService the train service
     * @return the locomotive ID, or null if no locomotive found
     */
    private Integer getLocomotiveFromTrainService(TrainService trainService) {
        List<TrainServiceComposition> compositions =
            compositionRepository.getCompositionsByTrainService(trainService.getServiceId());

        for (TrainServiceComposition comp : compositions) {
            if (comp.getServiceLocomotive() != null &&
                comp.getServiceLocomotive().getLocomotive() != null) {
                return comp.getServiceLocomotive().getLocomotive().getLocomotiveId();
            }
        }

        return null; // No locomotive found - will use manual calculation
    }

    /**
     * Calculates the effective speed for a train on a specific segment.
     * Considers track speed limit, locomotive max speed, and wagon weight.
     *
     * @param trainService the train service
     * @param segment the line segment
     * @return the calculated speed in km/h
     */
    public double calculateSegmentSpeed(TrainService trainService, LineSegment segment) {
        if (trainService == null || segment == null) {
            return 80.0; // Default speed
        }

        // Get track speed limit
        double trackSpeedLimit = segment.getSpeedLimitKmh() != null ? segment.getSpeedLimitKmh() : 120.0;

        // Get locomotive information from train composition
        List<TrainServiceComposition> compositions = compositionRepository.getCompositionsByTrainService(trainService.getServiceId());

        double locomotiveMaxSpeed = 120.0; // Default
        double locomotivePower = 1000.0; // Default in kW
        double totalWagonWeight = 0.0;
        int wagonCount = 0;

        for (TrainServiceComposition comp : compositions) {
            if (comp.getServiceLocomotive() != null && comp.getServiceLocomotive().getLocomotive() != null) {
                Locomotive loco = comp.getServiceLocomotive().getLocomotive();
                if (loco.getMaxSpeed() != null) {
                    locomotiveMaxSpeed = Math.max(locomotiveMaxSpeed, loco.getMaxSpeed());
                }
                if (loco.getPower() != null) {
                    locomotivePower += loco.getPower();
                }
            }
            if (comp.getWagon() != null && comp.getWagon().getWagonModel() != null) {
                WagonModel model = comp.getWagon().getWagonModel();
                if (model.getTareWeight() != null) {
                    totalWagonWeight += model.getTareWeight();
                }
                if (model.getPayloadWeight() != null) {
                    // Assume 70% loaded on average
                    totalWagonWeight += model.getPayloadWeight() * 0.7;
                }
                wagonCount++;
            }
        }

        // Calculate speed reduction based on weight-to-power ratio
        double weightToPowerRatio = totalWagonWeight / locomotivePower;
        double speedReductionFactor = 1.0;

        if (weightToPowerRatio > 5.0) {
            speedReductionFactor = 0.7; // Heavy load
        } else if (weightToPowerRatio > 3.0) {
            speedReductionFactor = 0.85; // Medium load
        } else {
            speedReductionFactor = 0.95; // Light load
        }

        // Return the minimum of track limit, locomotive limit (with reduction), and operational speed
        double effectiveLocomotiveSpeed = locomotiveMaxSpeed * speedReductionFactor;
        return Math.min(trackSpeedLimit, effectiveLocomotiveSpeed);
    }

    /**
     * Finds a line segment connecting two facilities.
     *
     * @param facility1 the first facility
     * @param facility2 the second facility
     * @return the connecting line segment, or null if not found
     */
    private LineSegment findSegmentBetweenFacilities(Facility facility1, Facility facility2) {
        if (facility1 == null || facility2 == null) return null;

        List<LineSegmentFacility> links1 = lineSegmentFacilityRepository.getLinksByFacility(facility1.getFacilityId());
        List<LineSegmentFacility> links2 = lineSegmentFacilityRepository.getLinksByFacility(facility2.getFacilityId());

        for (LineSegmentFacility link1 : links1) {
            for (LineSegmentFacility link2 : links2) {
                if (link1.getLineSegment() != null && link2.getLineSegment() != null &&
                    link1.getLineSegment().getSegmentId().equals(link2.getLineSegment().getSegmentId())) {
                    return link1.getLineSegment();
                }
            }
        }

        return null;
    }

    /**
     * Checks if a segment is single-line (no trackNumber or trackNumber = 1).
     *
     * @param segment the line segment
     * @return true if single-line, false otherwise
     */
    private boolean checkIfSingleLine(LineSegment segment) {
        if (segment == null) return false;
        Integer trackNumber = segment.getTrackNumber();
        return trackNumber == null || trackNumber <= 1;
    }

    /**
     * Plans crossing operation for a route point on a single-line segment.
     *
     * @param point the route point
     */
    private void planCrossingOperation(RoutePoint point) {
        // Simple crossing logic - can be enhanced with real-time scheduling
        // For now, we'll mark it as requiring a crossing check
        point.setCrossingOperation("WAIT_FOR_CROSSING");
        point.setExpectedWaitTimeMinutes(10); // Assume 10-minute wait for crossing
        point.setNotes("Single-line segment - crossing operation required");
    }

    /**
     * Calculates and sets total distance and travel time for a dispatch.
     *
     * @param dispatch the train dispatch
     */
    private void calculateRouteTotals(TrainDispatch dispatch) {
        if (dispatch.getRoute() == null || dispatch.getRoute().isEmpty()) {
            dispatch.setTotalDistanceKm(0.0);
            dispatch.setEstimatedTravelTimeMinutes(0.0);
            return;
        }

        RoutePoint lastPoint = dispatch.getRoute().get(dispatch.getRoute().size() - 1);
        dispatch.setTotalDistanceKm(lastPoint.getDistanceFromStartKm());
        dispatch.setEstimatedTravelTimeMinutes(lastPoint.getCumulativeTravelTimeMinutes());
    }

    /**
     * Updates the status of a dispatch.
     *
     * @param dispatchId the dispatch ID
     * @param newStatus the new status
     * @return the updated dispatch
     */
    public TrainDispatch updateDispatchStatus(Integer dispatchId, String newStatus) {
        TrainDispatch dispatch = dispatchRepository.getTrainDispatchById(dispatchId);
        if (dispatch != null) {
            dispatch.setStatus(newStatus);
            return dispatchRepository.updateTrainDispatch(dispatchId, dispatch);
        }
        return null;
    }

    /**
     * Gets all dispatches with a specific status.
     *
     * @param status the status to filter by
     * @return list of dispatches
     */
    public List<TrainDispatch> getDispatchesByStatus(String status) {
        return dispatchRepository.getDispatchesByStatus(status);
    }

    /**
     * Gets a dispatch by ID.
     *
     * @param dispatchId the dispatch ID
     * @return the dispatch, or null if not found
     */
    public TrainDispatch getDispatchById(Integer dispatchId) {
        return dispatchRepository.getTrainDispatchById(dispatchId);
    }

    /**
     * Gets all route points with crossing operations.
     *
     * @return list of route points with crossings
     */
    public List<RoutePoint> getRoutePointsWithCrossings() {
        return routePointRepository.getRoutePointsWithCrossings();
    }

    /**
     * Updates crossing operation for a route point.
     *
     * @param routePointId the route point ID
     * @param operation the crossing operation
     * @param waitTime the expected wait time
     * @return the updated route point
     */
    public RoutePoint updateCrossingOperation(Integer routePointId, String operation, Integer waitTime) {
        RoutePoint point = routePointRepository.getRoutePointById(routePointId);
        if (point != null) {
            point.setCrossingOperation(operation);
            point.setExpectedWaitTimeMinutes(waitTime);
            return routePointRepository.updateRoutePoint(routePointId, point);
        }
        return null;
    }
}

