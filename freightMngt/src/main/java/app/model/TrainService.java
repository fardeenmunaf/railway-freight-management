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

    private Integer service_id;
    private Train train;
    private LocalDateTime departure_date;
    private LocalDateTime arrival_date;
    private User train_driver;
    private String status;

    /**
     * Default constructor that creates a TrainService with null/default values.
     * Initializes all fields to null.
     */

    public TrainService() {
        this.service_id = null;
        this.train = null;
        this.departure_date = null;
        this.arrival_date = null;
        this.train_driver = null;
        this.status = null;
    }

    /**
     * Parameterized constructor that creates a TrainService with specified values.
     *
     * @param service_id     the unique identifier for this train service
     * @param train          the train assigned to this service
     * @param departure_date the scheduled departure date and time
     * @param arrival_date   the scheduled arrival date and time
     * @param train_driver   the user assigned as the train driver
     * @param status         the current status of the service
     */

    public TrainService(Integer service_id, Train train, LocalDateTime departure_date, LocalDateTime arrival_date,
                        User train_driver, String status) {
        this.service_id = service_id;
        this.train = train;
        this.departure_date = departure_date;
        this.arrival_date = arrival_date;
        this.train_driver = train_driver;
        this.status = status;
    }

    /**
     * Gets the unique identifier of this train service.
     *
     * @return the service identifier as a Integer
     */

    public Integer getServiceId() {
        return service_id;
    }

    /**
     * Sets the unique identifier of this train service.
     *
     * @param service_id the service identifier to set
     */

    public void setServiceId(Integer service_id) {
        this.service_id = service_id;
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
        return departure_date;
    }

    /**
     * Sets the scheduled departure date and time.
     *
     * @param departure_date the departure date to set
     */

    public void setDepartureDate(LocalDateTime departure_date) {
        this.departure_date = departure_date;
    }

    /**
     * Gets the scheduled arrival date and time.
     *
     * @return the arrival date as a LocalDateTime
     */

    public LocalDateTime getArrivalDate() {
        return arrival_date;
    }

    /**
     * Sets the scheduled arrival date and time.
     *
     * @param arrival_date the arrival date to set
     */

    public void setArrivalDate(LocalDateTime arrival_date) {
        this.arrival_date = arrival_date;
    }

    /**
     * Gets the user assigned as the train driver.
     *
     * @return the train driver as a User object
     */

    public User getTrainDriver() {
        return train_driver;
    }

    /**
     * Sets the user assigned as the train driver.
     *
     * @param train_driver the train driver to set
     */

    public void setTrainDriver(User train_driver) {
        this.train_driver = train_driver;
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
