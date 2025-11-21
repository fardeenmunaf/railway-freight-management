package app.controller;

import app.model.RoutePoint;
import app.model.TrainDispatch;
import app.model.TrainService;
import app.repositories.Repositories;
import app.service.sprint2.TrainDispatchService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller for managing train dispatch operations.
 * Provides interface between the view layer and business logic for dispatch scheduling.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainDispatchController {

    private final TrainDispatchService dispatchService;
    private final Repositories repositories;

    /**
     * Constructor that initializes the controller with repositories.
     */
    public TrainDispatchController() {
        this.repositories = Repositories.getInstance();
        this.dispatchService = new TrainDispatchService(
                repositories.getTrainDispatchRepository(),
                repositories.getRoutePointRepository(),
                repositories.getTrainServiceCompositionRepository(),
                repositories.getLineSegmentRepository(),
                repositories.getLineSegmentFacilityRepository(),
                repositories.getFacilityRepository(),
                repositories.getLocomotiveRepository()
        );
    }

    /**
     * Creates a manual train dispatch with specified route.
     *
     * @param trainServiceId the ID of the train service
     * @param plannedDepartureTime the planned departure time
     * @param routeDefinition list of facility IDs defining the route
     * @return the created dispatch
     */
    public TrainDispatch createManualDispatch(Integer trainServiceId,
                                             LocalDateTime plannedDepartureTime,
                                             List<Integer> routeDefinition) {
        TrainService trainService = repositories.getTrainServiceRepository().getTrainServiceById(trainServiceId);
        if (trainService == null) {
            throw new IllegalArgumentException("Train service not found: " + trainServiceId);
        }

        return dispatchService.createManualDispatch(trainService, plannedDepartureTime, routeDefinition);
    }

    /**
     * Gets a dispatch by ID.
     *
     * @param dispatchId the dispatch ID
     * @return the dispatch, or null if not found
     */
    public TrainDispatch getDispatchById(Integer dispatchId) {
        return dispatchService.getDispatchById(dispatchId);
    }

    /**
     * Gets all dispatches.
     *
     * @return list of all dispatches
     */
    public List<TrainDispatch> getAllDispatches() {
        return repositories.getTrainDispatchRepository().getAllTrainDispatches();
    }

    /**
     * Gets dispatches by status.
     *
     * @param status the status to filter by
     * @return list of dispatches with the specified status
     */
    public List<TrainDispatch> getDispatchesByStatus(String status) {
        return dispatchService.getDispatchesByStatus(status);
    }

    /**
     * Updates the status of a dispatch.
     *
     * @param dispatchId the dispatch ID
     * @param newStatus the new status
     * @return the updated dispatch
     */
    public TrainDispatch updateDispatchStatus(Integer dispatchId, String newStatus) {
        return dispatchService.updateDispatchStatus(dispatchId, newStatus);
    }

    /**
     * Gets the route for a specific dispatch.
     *
     * @param dispatchId the dispatch ID
     * @return list of route points
     */
    public List<RoutePoint> getRouteForDispatch(Integer dispatchId) {
        return repositories.getRoutePointRepository().getRoutePointsByDispatch(dispatchId);
    }

    /**
     * Gets all route points with crossing operations.
     *
     * @return list of route points requiring crossings
     */
    public List<RoutePoint> getRoutePointsWithCrossings() {
        return dispatchService.getRoutePointsWithCrossings();
    }

    /**
     * Updates crossing operation for a route point.
     *
     * @param routePointId the route point ID
     * @param operation the crossing operation type
     * @param waitTime the expected wait time in minutes
     * @return the updated route point
     */
    public RoutePoint updateCrossingOperation(Integer routePointId, String operation, Integer waitTime) {
        return dispatchService.updateCrossingOperation(routePointId, operation, waitTime);
    }

    /**
     * Dispatches a planned train (changes status from PLANNED to DISPATCHED).
     *
     * @param dispatchId the dispatch ID
     * @return the updated dispatch
     */
    public TrainDispatch dispatchTrain(Integer dispatchId) {
        TrainDispatch dispatch = dispatchService.getDispatchById(dispatchId);
        if (dispatch == null) {
            throw new IllegalArgumentException("Dispatch not found: " + dispatchId);
        }
        if (!"PLANNED".equals(dispatch.getStatus())) {
            throw new IllegalStateException("Dispatch must be in PLANNED status to dispatch");
        }
        return dispatchService.updateDispatchStatus(dispatchId, "DISPATCHED");
    }

    /**
     * Gets all train services available for dispatch.
     *
     * @return list of train services
     */
    public List<TrainService> getAvailableTrainServices() {
        return repositories.getTrainServiceRepository().getAllTrainServices();
    }

    /**
     * Calculates the effective speed for a segment.
     *
     * @param trainServiceId the train service ID
     * @param segmentId the line segment ID
     * @return the calculated speed in km/h
     */
    public double calculateSegmentSpeed(Integer trainServiceId, Integer segmentId) {
        TrainService trainService = repositories.getTrainServiceRepository().getTrainServiceById(trainServiceId);
        var segment = repositories.getLineSegmentRepository().getLineSegmentById(segmentId);
        return dispatchService.calculateSegmentSpeed(trainService, segment);
    }
}

