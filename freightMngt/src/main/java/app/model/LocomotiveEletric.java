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

public class LocomotiveEletric extends Locomotive {
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
     * @param locomotive   the base locomotive object
     * @param fuelCapacity the energy storage capacity
     */

    public LocomotiveEletric(Locomotive locomotive, Double fuelCapacity) {
        this.locomotive = locomotive;
        this.fuelCapacity = fuelCapacity;
    }

    /**
     * Parameterized constructor that creates a LocomotiveEletric with all values.
     *
     * @param locomotiveId     the unique identifier for this locomotive
     * @param operator         the user or company operating this locomotive
     * @param locomotiveType   the type of the locomotive
     * @param brand            the brand of the locomotive
     * @param model            the model of the locomotive
     * @param year             the manufacturing year of the locomotive
     * @param power            the power output of the locomotive in kW
     * @param acceleration     the acceleration of the locomotive in m/s²
     * @param dimensions       the dimensions of the locomotive
     * @param maxSpeed         the maximum speed of the locomotive in km/h
     * @param operationalSpeed the operational speed of the locomotive in km/h
     * @param maxWeight        the maximum weight capacity of the locomotive in tons
     * @param totalWeight      the total weight of the locomotive in tons
     * @param maxBogies        the maximum number of bogies for the locomotive
     * @param bogieType        the type of bogies used by the locomotive
     * @param traction         the traction type of the locomotive
     * @param locomotive       the base locomotive object
     * @param fuelCapacity     the energy storage capacity
     */
    public LocomotiveEletric(Integer locomotiveId, User operator, LocomotiveType locomotiveType, String brand, String model, Integer year, Integer power, Double acceleration, Dimension dimensions, Double maxSpeed, Double operationalSpeed, Double maxWeight, Double totalWeight, Integer maxBogies, String bogieType, String traction, Locomotive locomotive, Double fuelCapacity) {
        super(locomotiveId, operator, locomotiveType, brand, model, year, power, acceleration, dimensions, maxSpeed, operationalSpeed, maxWeight, totalWeight, maxBogies, bogieType, traction);
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
