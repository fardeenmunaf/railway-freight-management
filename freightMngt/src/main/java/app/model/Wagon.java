package app.model;

/**
 * Represents a railway wagon in the freight management system.
 * Each wagon contains a reference to its model, operator, and service year.
 * The detailed specifications such as type, weight, and dimensions are stored in the WagonModel.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Wagon {
    private Integer wagonId;
    private WagonModel wagonModel;
    private User operator;
    private Integer serviceYear;

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
     * @param wagonModel the model of the wagon containing specifications
     * @param operator the user or company operating this wagon
     * @param serviceYear the year the wagon entered service
     */

    public Wagon(Integer wagonId, WagonModel wagonModel, User operator, Integer serviceYear) {
        this.wagonId = wagonId;
        this.wagonModel = wagonModel;
        this.operator = operator;
        this.serviceYear = serviceYear;
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
     * Gets the wagon model of this wagon.
     *
     * @return the wagon model as a WagonModel object
     */

    public WagonModel getWagonModel() {
        return wagonModel;
    }

    /**
     * Sets the wagon model of this wagon.
     *
     * @param wagonModel the wagon model to set
     */

    public void setWagonModel(WagonModel wagonModel) {
        this.wagonModel = wagonModel;
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
     * Gets the service year of this wagon.
     *
     * @return the service year as an Integer
     */

    public Integer getServiceYear() {
        return serviceYear;
    }

    /**
     * Sets the service year of this wagon.
     *
     * @param serviceYear the service year to set
     */

    public void setServiceYear(Integer serviceYear) {
        this.serviceYear = serviceYear;
    }

    @Override
    public String toString() {
        return "Wagon{" +
                "wagonId=" + wagonId +
                ", wagonModel=" + wagonModel +
                ", operator=" + operator +
                ", serviceYear=" + serviceYear +
                '}';
    }
}
