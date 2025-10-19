package app.model;

import java.awt.*;

/**
 * Represents a locomotive with its main attributes.
 * <p>
 * Includes references to the operator, locomotive type, physical
 * characteristics and performance metrics (power, speed, weights, etc.).
 * </p>
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

    public Integer getLocomotiveId() {
        return locomotiveId;
    }

    public void setLocomotiveId(Integer locomotiveId) {
        this.locomotiveId = locomotiveId;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public LocomotiveType getLocomotiveType() {
        return locomotiveType;
    }

    public void setLocomotiveType(LocomotiveType locomotiveType) {
        this.locomotiveType = locomotiveType;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public Double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Double acceleration) {
        this.acceleration = acceleration;
    }

    public Dimension getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimension dimensions) {
        this.dimensions = dimensions;
    }

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Integer getMaxBogies() {
        return maxBogies;
    }

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
