package app.model;

/**
 * Represents a train in the freight management system.
 * This class defines a train entity with its operational details including
 * identification, route information, and operating company. Trains are used
 * for transporting freight between different facilities in the network.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Train {

    private Integer trainId;
    private String code;
    private String departure;
    private String destination;
    private Company operator;

    /**
     * Default constructor that creates a Train with null/default values.
     * Initializes all fields to null.
     */

    public Train() {
        this.trainId = null;
        this.code = null;
        this.departure = null;
        this.destination = null;
        this.operator = null;
    }

    /**
     * Parameterized constructor that creates a Train with specified values.
     *
     * @param trainId    the unique identifier for this train
     * @param code        the operational code of the train
     * @param departure   the departure location or station
     * @param destination the destination location or station
     * @param operator    the company operating this train
     */

    public Train(Integer trainId, String code, String departure, String destination, Company operator) {
        this.trainId = trainId;
        this.code = code;
        this.departure = departure;
        this.destination = destination;
        this.operator = operator;
    }

    /**
     * Gets the unique identifier of this train.
     *
     * @return the train identifier as an Integer
     */

    public Integer getTrainId() {
        return trainId;
    }

    /**
     * Sets the unique identifier of this train.
     *
     * @param trainId the train identifier to set
     */

    public void setTrainId(Integer trainId) {
        this.trainId = trainId;
    }

    /**
     * Gets the operational code of this train.
     *
     * @return the train code as a String
     */

    public String getCode() {
        return code;
    }

    /**
     * Sets the operational code of this train.
     *
     * @param code the train code to set
     */

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Gets the departure location of this train.
     *
     * @return the departure location as a String
     */

    public String getDeparture() {
        return departure;
    }

    /**
     * Sets the departure location of this train.
     *
     * @param departure the departure location to set
     */

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    /**
     * Gets the destination location of this train.
     *
     * @return the destination location as a String
     */

    public String getDestination() {
        return destination;
    }

    /**
     * Sets the destination location of this train.
     *
     * @param destination the destination location to set
     */

    public void setDestination(String destination) {
        this.destination = destination;
    }

    /**
     * Gets the company operating this train.
     *
     * @return the operating company as a Company object
     */

    public Company getOperator() {
        return operator;
    }

    /**
     * Sets the company operating this train.
     *
     * @param operator the operating company to set
     */

    public void setOperatorId(Company operator) {
        this.operator = operator;
    }
}
