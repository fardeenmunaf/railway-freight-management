package app.model;

import java.awt.*;

public class LocomotiveDiesel extends Locomotive{
    private Locomotive locomotive;
    private Double fuelCapacity; //in liters

    public LocomotiveDiesel() {
        super();
        this.locomotive = null;
        this.fuelCapacity = null;
    }

    public LocomotiveDiesel(Locomotive locomotive, Double fuelCapacity) {
        this.locomotive = locomotive;
        this.fuelCapacity = fuelCapacity;
    }

    public LocomotiveDiesel(Integer locomotiveId, User operator, LocomotiveType locomotiveType, String brand, String model, Integer year, Integer power, Double acceleration, Dimension dimensions, Double maxSpeed, Double max_weight, Double total_weight, Integer max_bogies, Locomotive locomotive, Double fuelCapacity) {
        super(locomotiveId, operator, locomotiveType, brand, model, year, power, acceleration, dimensions, maxSpeed, max_weight, total_weight, max_bogies);
        this.locomotive = locomotive;
        this.fuelCapacity = fuelCapacity;
    }

    public Locomotive getLocomotive() {
        return locomotive;
    }

    public void setLocomotive(Locomotive locomotive) {
        this.locomotive = locomotive;
    }

    public Double getFuelCapacity() {
        return fuelCapacity;
    }

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
