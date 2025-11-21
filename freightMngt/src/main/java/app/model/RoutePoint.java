package app.model;

import java.time.LocalDateTime;

/**
 * Represents a point along a train's route in the freight management system.
 * Each route point includes information about the facility (station/siding),
 * the line segment connecting to the next point, timing information,
 * and any required crossing operations.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class RoutePoint {

    private Integer routePointId;
    private TrainDispatch trainDispatch;
    private Integer sequenceOrder;
    private Facility facility; // Station or siding
    private LineSegment segmentToNext; // Segment to the next point (null for last point)
    private LocalDateTime estimatedArrivalTime;
    private LocalDateTime estimatedDepartureTime;
    private Double distanceFromStartKm;
    private Double cumulativeTravelTimeMinutes;
    private String crossingOperation; // "NONE", "WAIT_FOR_CROSSING", "SIDING_WAIT", "PRIORITY_PASS"
    private Integer expectedWaitTimeMinutes;
    private Boolean isSingleLineSegment; // True if the segment to next is single-line
    private String notes;

    /**
     * Default constructor that creates a RoutePoint with null/default values.
     * Initializes all fields to null or default values.
     */
    public RoutePoint() {
        this.routePointId = null;
        this.trainDispatch = null;
        this.sequenceOrder = null;
        this.facility = null;
        this.segmentToNext = null;
        this.estimatedArrivalTime = null;
        this.estimatedDepartureTime = null;
        this.distanceFromStartKm = 0.0;
        this.cumulativeTravelTimeMinutes = 0.0;
        this.crossingOperation = "NONE";
        this.expectedWaitTimeMinutes = 0;
        this.isSingleLineSegment = false;
        this.notes = null;
    }

    /**
     * Parameterized constructor that creates a RoutePoint with specified values.
     *
     * @param routePointId the unique identifier for this route point
     * @param trainDispatch the train dispatch this point belongs to
     * @param sequenceOrder the order of this point in the route
     * @param facility the facility (station/siding) at this point
     * @param segmentToNext the line segment connecting to the next point
     * @param estimatedArrivalTime the estimated arrival time at this point
     * @param estimatedDepartureTime the estimated departure time from this point
     * @param distanceFromStartKm the distance from route start in kilometers
     * @param cumulativeTravelTimeMinutes the cumulative travel time from start in minutes
     * @param crossingOperation the type of crossing operation required
     * @param expectedWaitTimeMinutes the expected wait time for crossing in minutes
     * @param isSingleLineSegment whether the segment to next is single-line
     * @param notes additional notes about this route point
     */
    public RoutePoint(Integer routePointId, TrainDispatch trainDispatch, Integer sequenceOrder,
                     Facility facility, LineSegment segmentToNext, LocalDateTime estimatedArrivalTime,
                     LocalDateTime estimatedDepartureTime, Double distanceFromStartKm,
                     Double cumulativeTravelTimeMinutes, String crossingOperation,
                     Integer expectedWaitTimeMinutes, Boolean isSingleLineSegment, String notes) {
        this.routePointId = routePointId;
        this.trainDispatch = trainDispatch;
        this.sequenceOrder = sequenceOrder;
        this.facility = facility;
        this.segmentToNext = segmentToNext;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.estimatedDepartureTime = estimatedDepartureTime;
        this.distanceFromStartKm = distanceFromStartKm;
        this.cumulativeTravelTimeMinutes = cumulativeTravelTimeMinutes;
        this.crossingOperation = crossingOperation;
        this.expectedWaitTimeMinutes = expectedWaitTimeMinutes;
        this.isSingleLineSegment = isSingleLineSegment;
        this.notes = notes;
    }

    /**
     * Gets the unique identifier of this route point.
     *
     * @return the route point identifier as an Integer
     */
    public Integer getRoutePointId() {
        return routePointId;
    }

    /**
     * Sets the unique identifier of this route point.
     *
     * @param routePointId the route point identifier to set
     */
    public void setRoutePointId(Integer routePointId) {
        this.routePointId = routePointId;
    }

    /**
     * Gets the train dispatch this point belongs to.
     *
     * @return the train dispatch as a TrainDispatch object
     */
    public TrainDispatch getTrainDispatch() {
        return trainDispatch;
    }

    /**
     * Sets the train dispatch this point belongs to.
     *
     * @param trainDispatch the train dispatch to set
     */
    public void setTrainDispatch(TrainDispatch trainDispatch) {
        this.trainDispatch = trainDispatch;
    }

    /**
     * Gets the sequence order of this point in the route.
     *
     * @return the sequence order as an Integer
     */
    public Integer getSequenceOrder() {
        return sequenceOrder;
    }

    /**
     * Sets the sequence order of this point in the route.
     *
     * @param sequenceOrder the sequence order to set
     */
    public void setSequenceOrder(Integer sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
    }

    /**
     * Gets the facility at this route point.
     *
     * @return the facility as a Facility object
     */
    public Facility getFacility() {
        return facility;
    }

    /**
     * Sets the facility at this route point.
     *
     * @param facility the facility to set
     */
    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * Gets the line segment connecting to the next point.
     *
     * @return the line segment as a LineSegment object
     */
    public LineSegment getSegmentToNext() {
        return segmentToNext;
    }

    /**
     * Sets the line segment connecting to the next point.
     *
     * @param segmentToNext the line segment to set
     */
    public void setSegmentToNext(LineSegment segmentToNext) {
        this.segmentToNext = segmentToNext;
    }

    /**
     * Gets the estimated arrival time at this point.
     *
     * @return the estimated arrival time as a LocalDateTime
     */
    public LocalDateTime getEstimatedArrivalTime() {
        return estimatedArrivalTime;
    }

    /**
     * Sets the estimated arrival time at this point.
     *
     * @param estimatedArrivalTime the estimated arrival time to set
     */
    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) {
        this.estimatedArrivalTime = estimatedArrivalTime;
    }

    /**
     * Gets the estimated departure time from this point.
     *
     * @return the estimated departure time as a LocalDateTime
     */
    public LocalDateTime getEstimatedDepartureTime() {
        return estimatedDepartureTime;
    }

    /**
     * Sets the estimated departure time from this point.
     *
     * @param estimatedDepartureTime the estimated departure time to set
     */
    public void setEstimatedDepartureTime(LocalDateTime estimatedDepartureTime) {
        this.estimatedDepartureTime = estimatedDepartureTime;
    }

    /**
     * Gets the distance from route start.
     *
     * @return the distance in kilometers
     */
    public Double getDistanceFromStartKm() {
        return distanceFromStartKm;
    }

    /**
     * Sets the distance from route start.
     *
     * @param distanceFromStartKm the distance to set
     */
    public void setDistanceFromStartKm(Double distanceFromStartKm) {
        this.distanceFromStartKm = distanceFromStartKm;
    }

    /**
     * Gets the cumulative travel time from route start.
     *
     * @return the cumulative travel time in minutes
     */
    public Double getCumulativeTravelTimeMinutes() {
        return cumulativeTravelTimeMinutes;
    }

    /**
     * Sets the cumulative travel time from route start.
     *
     * @param cumulativeTravelTimeMinutes the cumulative travel time to set
     */
    public void setCumulativeTravelTimeMinutes(Double cumulativeTravelTimeMinutes) {
        this.cumulativeTravelTimeMinutes = cumulativeTravelTimeMinutes;
    }

    /**
     * Gets the crossing operation type.
     *
     * @return the crossing operation as a String
     */
    public String getCrossingOperation() {
        return crossingOperation;
    }

    /**
     * Sets the crossing operation type.
     *
     * @param crossingOperation the crossing operation to set
     */
    public void setCrossingOperation(String crossingOperation) {
        this.crossingOperation = crossingOperation;
    }

    /**
     * Gets the expected wait time for crossing.
     *
     * @return the expected wait time in minutes
     */
    public Integer getExpectedWaitTimeMinutes() {
        return expectedWaitTimeMinutes;
    }

    /**
     * Sets the expected wait time for crossing.
     *
     * @param expectedWaitTimeMinutes the expected wait time to set
     */
    public void setExpectedWaitTimeMinutes(Integer expectedWaitTimeMinutes) {
        this.expectedWaitTimeMinutes = expectedWaitTimeMinutes;
    }

    /**
     * Checks if the segment to next is single-line.
     *
     * @return true if single-line, false otherwise
     */
    public Boolean getIsSingleLineSegment() {
        return isSingleLineSegment;
    }

    /**
     * Sets whether the segment to next is single-line.
     *
     * @param isSingleLineSegment true if single-line, false otherwise
     */
    public void setIsSingleLineSegment(Boolean isSingleLineSegment) {
        this.isSingleLineSegment = isSingleLineSegment;
    }

    /**
     * Gets the notes about this route point.
     *
     * @return the notes as a String
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Sets the notes about this route point.
     *
     * @param notes the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "RoutePoint{" +
                "routePointId=" + routePointId +
                ", sequenceOrder=" + sequenceOrder +
                ", facility=" + (facility != null ? facility.getName() : "null") +
                ", estimatedArrivalTime=" + estimatedArrivalTime +
                ", estimatedDepartureTime=" + estimatedDepartureTime +
                ", distanceFromStartKm=" + distanceFromStartKm +
                ", crossingOperation='" + crossingOperation + '\'' +
                ", isSingleLineSegment=" + isSingleLineSegment +
                '}';
    }
}

