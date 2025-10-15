package app.model;

import java.awt.*;

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
    private Double max_weight; //in Kg
    private Double total_weight; //in Kg
    private Integer max_bogies;

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
        this.max_weight = null;
        this.total_weight = null;
        this.max_bogies = null;
    }

    public Locomotive(Integer locomotiveId, User operator, LocomotiveType locomotiveType, String brand, String model, Integer year, Integer power, Double acceleration, Dimension dimensions, Double maxSpeed, Double max_weight, Double total_weight, Integer max_bogies) {
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
        this.max_weight = max_weight;
        this.total_weight = total_weight;
        this.max_bogies = max_bogies;
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

    public Double getMax_weight() {
        return max_weight;
    }

    public void setMax_weight(Double max_weight) {
        this.max_weight = max_weight;
    }

    public Double getTotal_weight() {
        return total_weight;
    }

    public void setTotal_weight(Double total_weight) {
        this.total_weight = total_weight;
    }

    public Integer getMax_bogies() {
        return max_bogies;
    }

    public void setMax_bogies(Integer max_bogies) {
        this.max_bogies = max_bogies;
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
                ", max_weight=" + max_weight +
                ", total_weight=" + total_weight +
                ", max_bogies=" + max_bogies +
                '}';
    }
}

