package app.model;

/**
 * Represents a railway wagon in the freight management system.
 * Each wagon contains detailed specifications including operator information,
 * wagon type, weight characteristics (tare and payload), and physical dimensions.
 * This class stores essential data for managing railway freight cars and their cargo capacity.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Wagon {
    private Integer wagonId;
    private User operator;
    private WagonType wagonType;
    private Double tareWeight;
    private Double payloadWeight;
    private Dimensions dimensions;

    /**
     * Default constructor that creates a Wagon with null values.
     * Initializes all fields to null.
     */

    public Wagon() {
    }

    /**
     * Parameterized constructor that creates a Wagon with specified values.
     *
     * @param wagonId the unique identifier for this wagon
     * @param operator the user or company operating this wagon
     * @param wagonType the type of wagon
     * @param tareWeight the tare weight (empty weight) in kilograms
     * @param payloadWeight the maximum payload weight in kilograms
     * @param dimensions the physical dimensions of the wagon
     */

    public Wagon(Integer wagonId, User operator, WagonType wagonType, Double tareWeight, Double payloadWeight, Dimensions dimensions) {
        this.wagonId = wagonId;
        this.operator = operator;
        this.wagonType = wagonType;
        this.tareWeight = tareWeight;
        this.payloadWeight = payloadWeight;
        this.dimensions = dimensions;
    }

    /**
     * Gets the unique identifier of this wagon.
     *
     * @return the wagon identifier as an Integer
     */

    public Integer getWagonId() {
        return wagonId;
    }

    /**
     * Sets the unique identifier of this wagon.
     *
     * @param wagonId the wagon identifier to set
     */

    public void setWagonId(Integer wagonId) {
        this.wagonId = wagonId;
    }

    /**
     * Gets the operator of this wagon.
     *
     * @return the operator as a User object
     */

    public User getOperator() {
        return operator;
    }

    /**
     * Sets the operator of this wagon.
     *
     * @param operator the operator to set
     */

    public void setOperator(User operator) {
        this.operator = operator;
    }

    /**
     * Gets the type of this wagon.
     *
     * @return the wagon type as a WagonType object
     */

    public WagonType getWagonType() {
        return wagonType;
    }

    /**
     * Sets the type of this wagon.
     *
     * @param wagonType the wagon type to set
     */

    public void setWagonType(WagonType wagonType) {
        this.wagonType = wagonType;
    }

    /**
     * Gets the tare weight (empty weight) of this wagon.
     *
     * @return the tare weight as a Double in kilograms
     */

    public Double getTareWeight() {
        return tareWeight;
    }

    /**
     * Sets the tare weight (empty weight) of this wagon.
     *
     * @param tareWeight the tare weight to set in kilograms
     */

    public void setTareWeight(Double tareWeight) {
        this.tareWeight = tareWeight;
    }

    /**
     * Gets the maximum payload weight of this wagon.
     *
     * @return the payload weight as a Double in kilograms
     */

    public Double getPayloadWeight() {
        return payloadWeight;
    }

    /**
     * Sets the maximum payload weight of this wagon.
     *
     * @param payloadWeight the payload weight to set in kilograms
     */

    public void setPayloadWeight(Double payloadWeight) {
        this.payloadWeight = payloadWeight;
    }

    /**
     * Gets the physical dimensions of this wagon.
     *
     * @return the dimensions as a Dimensions object
     */

    public Dimensions getDimensions() {
        return dimensions;
    }

    /**
     * Sets the physical dimensions of this wagon.
     *
     * @param dimensions the dimensions to set
     */

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public String toString() {
        return "Wagon{" +
                "wagonId=" + wagonId +
                ", operator=" + operator +
                ", wagonType=" + wagonType +
                ", tareWeight=" + tareWeight +
                ", payloadWeight=" + payloadWeight +
                ", dimensions=" + dimensions +
                '}';
    }
}
