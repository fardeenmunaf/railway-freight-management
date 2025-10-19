package app.model;

import java.awt.*;

/**
 * Represents an electric locomotive in the freight management system.
 * This class extends the base Locomotive class with electric-specific attributes,
 * particularly the battery or energy storage capacity.
 * Electric locomotives draw power from overhead lines or third rails.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class LocomotiveEletric extends Locomotive{
    private Locomotive locomotive;
    private Double fuelCapacity; //in liters

    /**
     * Default constructor that creates a LocomotiveEletric with null values.
     * Calls the parent Locomotive constructor and initializes electric-specific fields to null.
     */

    public LocomotiveEletric() {
        super();
        this.locomotive = null;
        this.fuelCapacity = null;
    }

    /**
     * Parameterized constructor that creates a LocomotiveEletric with basic values.
     *
     * @param locomotive the base locomotive object
     * @param fuelCapacity the energy storage capacity
     */

    public LocomotiveEletric(Locomotive locomotive, Double fuelCapacity) {
        this.locomotive = locomotive;
        this.fuelCapacity = fuelCapacity;
    }

    /**
     * Parameterized constructor that creates a LocomotiveEletric with all locomotive and electric-specific values.
     *
     * @param locomotiveId the unique identifier for the locomotive
     * @param operator the user or company operating the locomotive
     * @param locomotiveType the type of locomotive
     * @param brand the manufacturer brand
     * @param model the model designation
     * @param year the year of manufacture
     * @param power the power output in kilowatts
     * @param acceleration the acceleration capability in meters per second squared
     * @param dimensions the physical dimensions
     * @param maxSpeed the maximum speed in kilometers per hour
     * @param max_weight the maximum weight capacity in kilograms
     * @param total_weight the total weight of the locomotive in kilograms
     * @param max_bogies the maximum number of bogies
     * @param locomotive the base locomotive object
     * @param fuelCapacity the energy storage capacity
     */

    public LocomotiveEletric(Integer locomotiveId, User operator, LocomotiveType locomotiveType, String brand, String model, Integer year, Integer power, Double acceleration, Dimension dimensions, Double maxSpeed, Double max_weight, Double total_weight, Integer max_bogies, Locomotive locomotive, Double fuelCapacity) {
        super(locomotiveId, operator, locomotiveType, brand, model, year, power, acceleration, dimensions, maxSpeed, max_weight, total_weight, max_bogies);
        this.locomotive = locomotive;
        this.fuelCapacity = fuelCapacity;
    }

    /**
     * Gets the base locomotive object.
     *
     * @return the locomotive as a Locomotive object
     */

    public Locomotive getLocomotive() {
        return locomotive;
    }

    /**
     * Sets the base locomotive object.
     *
     * @param locomotive the locomotive to set
     */

    public void setLocomotive(Locomotive locomotive) {
        this.locomotive = locomotive;
    }

    /**
     * Gets the energy storage capacity of this electric locomotive.
     *
     * @return the capacity as a Double
     */

    public Double getFuelCapacity() {
        return fuelCapacity;
    }

    /**
     * Sets the energy storage capacity of this electric locomotive.
     *
     * @param fuelCapacity the capacity to set
     */

    public void setFuelCapacity(Double fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    @Override
    public String toString() {
        return "LocomotiveDiesel{" +
                "locomotive=" + locomotive +
                ", fuelCapacity=" + fuelCapacity +
                '}';
    }
}
