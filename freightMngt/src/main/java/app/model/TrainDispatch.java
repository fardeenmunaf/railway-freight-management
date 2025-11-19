package app.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a train dispatch in the freight management system.
 * A dispatch contains a train service, planned departure time, route information,
 * and crossing operations for single-line segments.
 * This class supports both automatic and manual route definition.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainDispatch {

    private Integer dispatchId;
    private TrainService trainService;
    private LocalDateTime plannedDepartureTime;
    private List<RoutePoint> route;
    private String routeType; // "MANUAL" or "AUTOMATIC"
    private String status; // "PLANNED", "DISPATCHED", "IN_TRANSIT", "COMPLETED", "CANCELLED"
    private Double totalDistanceKm;
    private Double estimatedTravelTimeMinutes;

    /**
     * Default constructor that creates a TrainDispatch with null/default values.
     * Initializes the route list as empty.
     */
    public TrainDispatch() {
        this.dispatchId = null;
        this.trainService = null;
        this.plannedDepartureTime = null;
        this.route = new ArrayList<>();
        this.routeType = "MANUAL";
        this.status = "PLANNED";
        this.totalDistanceKm = 0.0;
        this.estimatedTravelTimeMinutes = 0.0;
    }

    /**
     * Parameterized constructor that creates a TrainDispatch with specified values.
     *
     * @param dispatchId the unique identifier for this dispatch
     * @param trainService the train service being dispatched
     * @param plannedDepartureTime the planned departure date and time
     * @param route the list of route points defining the train's path
     * @param routeType the type of route ("MANUAL" or "AUTOMATIC")
     * @param status the current status of the dispatch
     * @param totalDistanceKm the total distance of the route in kilometers
     * @param estimatedTravelTimeMinutes the estimated total travel time in minutes
     */
    public TrainDispatch(Integer dispatchId, TrainService trainService, LocalDateTime plannedDepartureTime,
                        List<RoutePoint> route, String routeType, String status,
                        Double totalDistanceKm, Double estimatedTravelTimeMinutes) {
        this.dispatchId = dispatchId;
        this.trainService = trainService;
        this.plannedDepartureTime = plannedDepartureTime;
        this.route = route != null ? route : new ArrayList<>();
        this.routeType = routeType;
        this.status = status;
        this.totalDistanceKm = totalDistanceKm;
        this.estimatedTravelTimeMinutes = estimatedTravelTimeMinutes;
    }

    /**
     * Gets the unique identifier of this dispatch.
     *
     * @return the dispatch identifier as an Integer
     */
    public Integer getDispatchId() {
        return dispatchId;
    }

    /**
     * Sets the unique identifier of this dispatch.
     *
     * @param dispatchId the dispatch identifier to set
     */
    public void setDispatchId(Integer dispatchId) {
        this.dispatchId = dispatchId;
    }

    /**
     * Gets the train service being dispatched.
     *
     * @return the train service as a TrainService object
     */
    public TrainService getTrainService() {
        return trainService;
    }

    /**
     * Sets the train service being dispatched.
     *
     * @param trainService the train service to set
     */
    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }

    /**
     * Gets the planned departure time.
     *
     * @return the planned departure time as a LocalDateTime
     */
    public LocalDateTime getPlannedDepartureTime() {
        return plannedDepartureTime;
    }

    /**
     * Sets the planned departure time.
     *
     * @param plannedDepartureTime the planned departure time to set
     */
    public void setPlannedDepartureTime(LocalDateTime plannedDepartureTime) {
        this.plannedDepartureTime = plannedDepartureTime;
    }

    /**
     * Gets the route points defining the train's path.
     *
     * @return the list of route points
     */
    public List<RoutePoint> getRoute() {
        return route;
    }

    /**
     * Sets the route points defining the train's path.
     *
     * @param route the route points to set
     */
    public void setRoute(List<RoutePoint> route) {
        this.route = route;
    }

    /**
     * Adds a route point to the dispatch route.
     *
     * @param routePoint the route point to add
     */
    public void addRoutePoint(RoutePoint routePoint) {
        this.route.add(routePoint);
    }

    /**
     * Gets the type of route.
     *
     * @return the route type ("MANUAL" or "AUTOMATIC")
     */
    public String getRouteType() {
        return routeType;
    }

    /**
     * Sets the type of route.
     *
     * @param routeType the route type to set
     */
    public void setRouteType(String routeType) {
        this.routeType = routeType;
    }

    /**
     * Gets the current status of this dispatch.
     *
     * @return the status as a String
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of this dispatch.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the total distance of the route.
     *
     * @return the total distance in kilometers
     */
    public Double getTotalDistanceKm() {
        return totalDistanceKm;
    }

    /**
     * Sets the total distance of the route.
     *
     * @param totalDistanceKm the total distance to set
     */
    public void setTotalDistanceKm(Double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }

    /**
     * Gets the estimated total travel time.
     *
     * @return the estimated travel time in minutes
     */
    public Double getEstimatedTravelTimeMinutes() {
        return estimatedTravelTimeMinutes;
    }

    /**
     * Sets the estimated total travel time.
     *
     * @param estimatedTravelTimeMinutes the estimated travel time to set
     */
    public void setEstimatedTravelTimeMinutes(Double estimatedTravelTimeMinutes) {
        this.estimatedTravelTimeMinutes = estimatedTravelTimeMinutes;
    }

    /**
     * Gets the estimated arrival time based on departure time and travel time.
     *
     * @return the estimated arrival time as a LocalDateTime
     */
    public LocalDateTime getEstimatedArrivalTime() {
        if (plannedDepartureTime != null && estimatedTravelTimeMinutes != null) {
            return plannedDepartureTime.plusMinutes(estimatedTravelTimeMinutes.longValue());
        }
        return null;
    }

    @Override
    public String toString() {
        return "TrainDispatch{" +
                "dispatchId=" + dispatchId +
                ", trainService=" + (trainService != null ? trainService.getServiceId() : "null") +
                ", plannedDepartureTime=" + plannedDepartureTime +
                ", routeType='" + routeType + '\'' +
                ", status='" + status + '\'' +
                ", totalDistanceKm=" + totalDistanceKm +
                ", estimatedTravelTimeMinutes=" + estimatedTravelTimeMinutes +
                ", routePoints=" + route.size() +
                '}';
    }
}

