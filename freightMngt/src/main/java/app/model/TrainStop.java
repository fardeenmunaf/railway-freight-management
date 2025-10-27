package app.model;

import java.time.LocalDateTime;

/**
 * Represents a train stop in the freight management system.
 * Each train stop defines a scheduled or actual stop at a facility along a train's route,
 * including timing information, parking track assignment, and position in the service sequence.
 * This class tracks both planned and actual arrival/departure times for operational monitoring.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class TrainStop {

    private TrainService service;
    private Facility facility;
    private ParkingTrack parkingTrack;
    private Integer trainStopId;
    private Integer position;
    private LocalDateTime schedArr;
    private LocalDateTime schedDep;
    private Integer plannedStopTime;
    private LocalDateTime actArr;
    private LocalDateTime actDep;
    private Integer actStopTime;

    /**
     * Default constructor that creates a TrainStop with null values.
     * Initializes all fields to null.
     */

    public TrainStop() {
        this.trainStopId = null;
        this.service = null;
        this.facility = null;
        this.parkingTrack = null;
        this.position = null;
        this.schedArr = null;
        this.schedDep = null;
        this.plannedStopTime = null;
        this.actArr = null;
        this.actDep = null;
        this.actStopTime = null;
    }

    /**
     * Parameterized constructor that creates a TrainStop with specified values.
     *
     * @param trainStopId the unique identifier for this train stop
     * @param service the train service this stop belongs to
     * @param facility the facility where the stop occurs
     * @param parkingTrack the parking track assigned for this stop
     * @param position the sequential position of this stop in the service
     * @param schedArr the scheduled arrival time
     * @param schedDep the scheduled departure time
     * @param plannedStopTime the planned stop duration in minutes
     * @param actArr the actual arrival time
     * @param actDep the actual departure time
     * @param actStopTime the actual stop duration in minutes
     */

    public TrainStop(Integer trainStopId, TrainService service, Facility facility, ParkingTrack parkingTrack, Integer position,
                     LocalDateTime schedArr, LocalDateTime schedDep, Integer plannedStopTime,
                     LocalDateTime actArr, LocalDateTime actDep, Integer actStopTime) {
        this.trainStopId = trainStopId;
        this.service = service;
        this.facility = facility;
        this.parkingTrack = parkingTrack;
        this.position = position;
        this.schedArr = schedArr;
        this.schedDep = schedDep;
        this.plannedStopTime = plannedStopTime;
        this.actArr = actArr;
        this.actDep = actDep;
        this.actStopTime = actStopTime;
    }

    /**
     * Gets the unique identifier of this train stop.
     *
     * @return the train stop identifier as an Integer
     */

    public Integer getTrainStopId() {
        return trainStopId;
    }

    /**
     * Sets the unique identifier of this train stop.
     *
     * @param trainStopId the train stop identifier to set
     */

    public void setTrainStopId(Integer trainStopId) {
        this.trainStopId = trainStopId;
    }

    /**
     * Gets the train service this stop belongs to.
     *
     * @return the service as a TrainService object
     */

    public TrainService getService() {
        return service;
    }

    /**
     * Sets the train service this stop belongs to.
     *
     * @param service the service to set
     */

    public void setService(TrainService service) {
        this.service = service;
    }

    /**
     * Gets the facility where the stop occurs.
     *
     * @return the facility as a Facility object
     */

    public Facility getFacility() {
        return facility;
    }

    /**
     * Sets the facility where the stop occurs.
     *
     * @param facility the facility to set
     */

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * Gets the parking track assigned for this stop.
     *
     * @return the parking track as a ParkingTrack object
     */

    public ParkingTrack getParkingTrack() {
        return parkingTrack;
    }

    /**
     * Sets the parking track assigned for this stop.
     *
     * @param parkingTrack the parking track to set
     */

    public void setParkingTrack(ParkingTrack parkingTrack) {
        this.parkingTrack = parkingTrack;
    }

    /**
     * Gets the sequential position of this stop in the service.
     *
     * @return the position as an Integer
     */

    public Integer getPosition() {
        return position;
    }

    /**
     * Sets the sequential position of this stop in the service.
     *
     * @param position the position to set
     */

    public void setPosition(Integer position) {
        this.position = position;
    }

    /**
     * Gets the scheduled arrival time.
     *
     * @return the scheduled arrival as a LocalDateTime
     */

    public LocalDateTime getSchedArr() {
        return schedArr;
    }

    /**
     * Sets the scheduled arrival time.
     *
     * @param schedArr the scheduled arrival to set
     */

    public void setSchedArr(LocalDateTime schedArr) {
        this.schedArr = schedArr;
    }

    /**
     * Gets the scheduled departure time.
     *
     * @return the scheduled departure as a LocalDateTime
     */

    public LocalDateTime getSchedDep() {
        return schedDep;
    }

    /**
     * Sets the scheduled departure time.
     *
     * @param schedDep the scheduled departure to set
     */

    public void setSchedDep(LocalDateTime schedDep) {
        this.schedDep = schedDep;
    }

    /**
     * Gets the planned stop duration.
     *
     * @return the planned stop time as an Integer in minutes
     */

    public Integer getPlannedStopTime() {
        return plannedStopTime;
    }

    /**
     * Sets the planned stop duration.
     *
     * @param plannedStopTime the planned stop time to set in minutes
     */

    public void setPlannedStopTime(Integer plannedStopTime) {
        this.plannedStopTime = plannedStopTime;
    }

    /**
     * Gets the actual arrival time.
     *
     * @return the actual arrival as a LocalDateTime
     */

    public LocalDateTime getActArr() {
        return actArr;
    }

    /**
     * Sets the actual arrival time.
     *
     * @param actArr the actual arrival to set
     */

    public void setActArr(LocalDateTime actArr) {
        this.actArr = actArr;
    }

    /**
     * Gets the actual departure time.
     *
     * @return the actual departure as a LocalDateTime
     */

    public LocalDateTime getActDep() {
        return actDep;
    }

    /**
     * Sets the actual departure time.
     *
     * @param actDep the actual departure to set
     */

    public void setActDep(LocalDateTime actDep) {
        this.actDep = actDep;
    }

    /**
     * Gets the actual stop duration.
     *
     * @return the actual stop time as an Integer in minutes
     */

    public Integer getActStopTime() {
        return actStopTime;
    }

    /**
     * Sets the actual stop duration.
     *
     * @param actStopTime the actual stop time to set in minutes
     */

    public void setActStopTime(Integer actStopTime) {
        this.actStopTime = actStopTime;
    }
}
