package app.model;

import java.time.LocalDateTime;

/**
 * Represents a train service in the freight management system.
 * This class defines a specific train service instance with scheduling information,
 * assigned personnel, and operational status. Train services track the actual
 * execution of train operations including departure and arrival times.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class TrainService {

    private Integer serviceId;
    private Train train;
    private LocalDateTime departureDate;
    private LocalDateTime arrivalDate;
    private User trainDriver;
    private String status;

    /**
     * Default constructor that creates a TrainService with null/default values.
     * Initializes all fields to null.
     */

    public TrainService() {
        this.serviceId = null;
        this.train = null;
        this.departureDate = null;
        this.arrivalDate = null;
        this.trainDriver = null;
        this.status = null;
    }

    /**
     * Parameterized constructor that creates a TrainService with specified values.
     *
     * @param serviceId     the unique identifier for this train service
     * @param train          the train assigned to this service
     * @param departureDate the scheduled departure date and time
     * @param arrivalDate   the scheduled arrival date and time
     * @param trainDriver   the user assigned as the train driver
     * @param status         the current status of the service
     */

    public TrainService(Integer serviceId, Train train, LocalDateTime departureDate, LocalDateTime arrivalDate,
                        User trainDriver, String status) {
        this.serviceId = serviceId;
        this.train = train;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.trainDriver = trainDriver;
        this.status = status;
    }

    /**
     * Gets the unique identifier of this train service.
     *
     * @return the service identifier as a Integer
     */

    public Integer getServiceId() {
        return serviceId;
    }

    /**
     * Sets the unique identifier of this train service.
     *
     * @param serviceId the service identifier to set
     */

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * Gets the train assigned to this service.
     *
     * @return the train as a Train object
     */

    public Train getTrain() {
        return train;
    }

    /**
     * Sets the train assigned to this service.
     *
     * @param train the train to set
     */

    public void setTrain(Train train) {
        this.train = train;
    }

    /**
     * Gets the scheduled departure date and time.
     *
     * @return the departure date as a LocalDateTime
     */

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    /**
     * Sets the scheduled departure date and time.
     *
     * @param departureDate the departure date to set
     */

    public void setDepartureDate(LocalDateTime departureDate) {
        this.departureDate = departureDate;
    }

    /**
     * Gets the scheduled arrival date and time.
     *
     * @return the arrival date as a LocalDateTime
     */

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    /**
     * Sets the scheduled arrival date and time.
     *
     * @param arrivalDate the arrival date to set
     */

    public void setArrivalDate(LocalDateTime arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    /**
     * Gets the user assigned as the train driver.
     *
     * @return the train driver as a User object
     */

    public User getTrainDriver() {
        return trainDriver;
    }

    /**
     * Sets the user assigned as the train driver.
     *
     * @param trainDriver the train driver to set
     */

    public void setTrainDriver(User trainDriver) {
        this.trainDriver = trainDriver;
    }

    /**
     * Gets the current status of this service.
     *
     * @return the status as a String
     */

    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of this service.
     *
     * @param status the status to set
     */

    public void setStatus(String status) {
        this.status = status;
    }
}
