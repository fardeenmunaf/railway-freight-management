package app.model;

/**
 * Represents a wagon in a parking queue in the freight management system.
 * Each entry tracks a wagon's position within a parking track queue,
 * including its sequential position and current status.
 * This class is essential for managing wagon positioning and track utilization.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class ParkingQueue {

    private Integer parkingQueueId;
    private ParkingTrack parkingTrack;
    private Wagon wagon;
    private Integer positionNo;
    private String status;

    /**
     * Default constructor that creates a ParkingQueue with null values.
     * Initializes all fields to null.
     */

    public ParkingQueue() {
        this.parkingQueueId = null;
        this.parkingTrack = null;
        this.wagon = null;
        this.positionNo = null;
        this.status = null;
    }

    /**
     * Parameterized constructor that creates a ParkingQueue with specified values.
     *
     * @param parkingQueueId the unique identifier for this parking queue entry
     * @param parkingTrack the parking track where the wagon is queued
     * @param wagon the wagon in the queue
     * @param positionNo the position number of the wagon in the queue
     * @param status the current status of the wagon in the queue
     */

    public ParkingQueue(Integer parkingQueueId, ParkingTrack parkingTrack, Wagon wagon, Integer positionNo, String status) {
        this.parkingQueueId = parkingQueueId;
        this.parkingTrack = parkingTrack;
        this.wagon = wagon;
        this.positionNo = positionNo;
        this.status = status;
    }

    /**
     * Gets the unique identifier of this parking queue entry.
     *
     * @return the parking queue identifier as an Integer
     */

    public Integer getParkingQueueId() {
        return parkingQueueId;
    }

    /**
     * Sets the unique identifier of this parking queue entry.
     *
     * @param parkingQueueId the parking queue identifier to set
     */

    public void setParkingQueueId(Integer parkingQueueId) {
        this.parkingQueueId = parkingQueueId;
    }

    /**
     * Gets the parking track where the wagon is queued.
     *
     * @return the parking track as a ParkingTrack object
     */

    public ParkingTrack getParkingTrack() {
        return parkingTrack;
    }

    /**
     * Sets the parking track where the wagon is queued.
     *
     * @param parkingTrack the parking track to set
     */

    public void setParkingTrack(ParkingTrack parkingTrack) {
        this.parkingTrack = parkingTrack;
    }

    /**
     * Gets the wagon in the queue.
     *
     * @return the wagon as a Wagon object
     */

    public Wagon getWagon() {
        return wagon;
    }

    /**
     * Sets the wagon in the queue.
     *
     * @param wagon the wagon to set
     */

    public void setWagon(Wagon wagon) {
        this.wagon = wagon;
    }

    /**
     * Gets the position number of the wagon in the queue.
     *
     * @return the position number as an Integer
     */

    public Integer getPositionNo() {
        return positionNo;
    }

    /**
     * Sets the position number of the wagon in the queue.
     *
     * @param positionNo the position number to set
     */

    public void setPositionNo(Integer positionNo) {
        this.positionNo = positionNo;
    }

    /**
     * Gets the current status of the wagon in the queue.
     *
     * @return the status as a String
     */

    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the wagon in the queue.
     *
     * @param status the status to set
     */

    public void setStatus(String status) {
        this.status = status;
    }
}
