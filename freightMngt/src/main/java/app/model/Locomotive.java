package app.model;

import java.awt.*;

/**
 * Represents a locomotive in the freight management system.
 * Each locomotive contains detailed specifications including operator information,
 * locomotive type, physical characteristics, and performance metrics.
 * This class stores essential data such as power, acceleration, dimensions, speed limits,
 * weight capacities, and bogie configurations for railway operations.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Locomotive {
    private Integer locomotiveId;
    private User operator; //operator equals company
    private LocomotiveType locomotiveType;
    private String brand;
    private String model;
    private Integer year;
    private Integer power; //in Kw
    private Double acceleration; //in m/s^2
    private Dimension dimensions;
    private Double maxSpeed; //in Km/h
    private Double maxWeight; //in Kg
    private Double totalWeight; //in Kg
    private Integer maxBogies;

    /**
     * Default constructor that creates a Locomotive with null values.
     * Initializes all fields to null.
     */

    public Locomotive() {
        this.locomotiveId = null;
        this.operator = null;
        this.locomotiveType = null;
        this.brand = null;
        this.model = null;
        this.year = null;
        this.power = null;
        this.acceleration = null;
        this.dimensions = null;
        this.maxSpeed = null;
        this.maxWeight = null;
        this.totalWeight = null;
        this.maxBogies = null;
    }

    /**
     * Parameterized constructor that creates a Locomotive with specified values.
     *
     * @param locomotiveId the unique identifier for this locomotive
     * @param operator the user or company operating this locomotive
     * @param locomotiveType the type of locomotive (diesel, electric, etc.)
     * @param brand the manufacturer brand of the locomotive
     * @param model the model designation of the locomotive
     * @param year the year of manufacture
     * @param power the power output in kilowatts
     * @param acceleration the acceleration capability in meters per second squared
     * @param dimensions the physical dimensions of the locomotive
     * @param maxSpeed the maximum speed in kilometers per hour
     * @param maxWeight the maximum weight capacity in kilograms
     * @param totalWeight the total weight of the locomotive in kilograms
     * @param maxBogies the maximum number of bogies
     */

    public Locomotive(Integer locomotiveId, User operator, LocomotiveType locomotiveType, String brand, String model, Integer year, Integer power, Double acceleration, Dimension dimensions, Double maxSpeed, Double maxWeight, Double totalWeight, Integer maxBogies) {
        this.locomotiveId = locomotiveId;
        this.operator = operator;
        this.locomotiveType = locomotiveType;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.power = power;
        this.acceleration = acceleration;
        this.dimensions = dimensions;
        this.maxSpeed = maxSpeed;
        this.maxWeight = maxWeight;
        this.totalWeight = totalWeight;
        this.maxBogies = maxBogies;
    }

    /**
     * Gets the unique identifier of this locomotive.
     *
     * @return the locomotive identifier as an Integer
     */

    public Integer getLocomotiveId() {
        return locomotiveId;
    }

    /**
     * Sets the unique identifier of this locomotive.
     *
     * @param locomotiveId the locomotive identifier to set
     */

    public void setLocomotiveId(Integer locomotiveId) {
        this.locomotiveId = locomotiveId;
    }

    /**
     * Gets the operator of this locomotive.
     *
     * @return the operator as a User object
     */

    public User getOperator() {
        return operator;
    }

    /**
     * Sets the operator of this locomotive.
     *
     * @param operator the operator to set
     */

    public void setOperator(User operator) {
        this.operator = operator;
    }

    /**
     * Gets the type of this locomotive.
     *
     * @return the locomotive type as a LocomotiveType object
     */

    public LocomotiveType getLocomotiveType() {
        return locomotiveType;
    }

    /**
     * Sets the type of this locomotive.
     *
     * @param locomotiveType the locomotive type to set
     */

    public void setLocomotiveType(LocomotiveType locomotiveType) {
        this.locomotiveType = locomotiveType;
    }

    /**
     * Gets the manufacturer brand of this locomotive.
     *
     * @return the brand as a String
     */

    public String getBrand() {
        return brand;
    }

    /**
     * Sets the manufacturer brand of this locomotive.
     *
     * @param brand the brand to set
     */

    public void setBrand(String brand) {
        this.brand = brand;
    }

    /**
     * Gets the model designation of this locomotive.
     *
     * @return the model as a String
     */

    public String getModel() {
        return model;
    }

    /**
     * Sets the model designation of this locomotive.
     *
     * @param model the model to set
     */

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Gets the year of manufacture of this locomotive.
     *
     * @return the year as an Integer
     */

    public Integer getYear() {
        return year;
    }

    /**
     * Sets the year of manufacture of this locomotive.
     *
     * @param year the year to set
     */

    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * Gets the power output of this locomotive.
     *
     * @return the power as an Integer in kilowatts
     */

    public Integer getPower() {
        return power;
    }

    /**
     * Sets the power output of this locomotive.
     *
     * @param power the power to set in kilowatts
     */

    public void setPower(Integer power) {
        this.power = power;
    }

    /**
     * Gets the acceleration capability of this locomotive.
     *
     * @return the acceleration as a Double in meters per second squared
     */

    public Double getAcceleration() {
        return acceleration;
    }

    /**
     * Sets the acceleration capability of this locomotive.
     *
     * @param acceleration the acceleration to set in meters per second squared
     */

    public void setAcceleration(Double acceleration) {
        this.acceleration = acceleration;
    }

    /**
     * Gets the physical dimensions of this locomotive.
     *
     * @return the dimensions as a Dimension object
     */

    public Dimension getDimensions() {
        return dimensions;
    }

    /**
     * Sets the physical dimensions of this locomotive.
     *
     * @param dimensions the dimensions to set
     */

    public void setDimensions(Dimension dimensions) {
        this.dimensions = dimensions;
    }

    /**
     * Gets the maximum speed of this locomotive.
     *
     * @return the maximum speed as a Double in kilometers per hour
     */

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    /**
     * Sets the maximum speed of this locomotive.
     *
     * @param maxSpeed the maximum speed to set in kilometers per hour
     */

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    /**
     * Gets the maximum weight capacity of this locomotive.
     *
     * @return the maximum weight as a Double in kilograms
     */

    public Double getMaxWeight() {
        return maxWeight;
    }

    /**
     * Sets the maximum weight capacity of this locomotive.
     *
     * @param maxWeight the maximum weight to set in kilograms
     */

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    /**
     * Gets the total weight of this locomotive.
     *
     * @return the total weight as a Double in kilograms
     */

    public Double getTotalWeight() {
        return totalWeight;
    }

    /**
     * Sets the total weight of this locomotive.
     *
     * @param totalWeight the total weight to set in kilograms
     */

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * Gets the maximum number of bogies for this locomotive.
     *
     * @return the maximum number of bogies as an Integer
     */

    public Integer getMaxBogies() {
        return maxBogies;
    }

    /**
     * Sets the maximum number of bogies for this locomotive.
     *
     * @param maxBogies the maximum number of bogies to set
     */

    public void setMaxBogies(Integer maxBogies) {
        this.maxBogies = maxBogies;
    }

    @Override
    public String toString() {
        return "Locomotive{" +
                "locomotiveId=" + locomotiveId +
                ", operator=" + operator +
                ", locomotiveType=" + locomotiveType +
                ", brand='" + brand + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", power=" + power +
                ", acceleration=" + acceleration +
                ", dimensions=" + dimensions +
                ", maxSpeed=" + maxSpeed +
                ", maxWeight=" + maxWeight +
                ", totalWeight=" + totalWeight +
                ", maxBogies=" + maxBogies +
                '}';
    }
}
