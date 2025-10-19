package app.model;

/**
 * Represents the association between a locomotive and its compatible gauge in the freight management system.
 * This class establishes the relationship between locomotives and the track gauges they can operate on,
 * which is essential for ensuring compatibility between rolling stock and railway infrastructure.
 * A locomotive may be compatible with multiple gauges, and this class tracks those relationships.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class LocomotiveGauge {
    private Locomotive locomotive;
    private Gauge gauge;

    /**
     * Default constructor that creates a LocomotiveGauge with null values.
     * Initializes all fields to null.
     */

    public LocomotiveGauge() {
        this.locomotive = null;
        this.gauge = null;
    }

    /**
     * Parameterized constructor that creates a LocomotiveGauge with specified values.
     *
     * @param locomotive the locomotive in the relationship
     * @param gauge the gauge specification in the relationship
     */

    public LocomotiveGauge(Locomotive locomotive, Gauge gauge) {
        this.locomotive = locomotive;
        this.gauge = gauge;
    }

    /**
     * Gets the locomotive in this gauge association.
     *
     * @return the locomotive as a Locomotive object
     */

    public Locomotive getLocomotive() {
        return locomotive;
    }

    /**
     * Sets the locomotive in this gauge association.
     *
     * @param locomotive the locomotive to set
     */

    public void setLocomotive(Locomotive locomotive) {
        this.locomotive = locomotive;
    }

    /**
     * Gets the gauge in this locomotive association.
     *
     * @return the gauge as a Gauge object
     */

    public Gauge getGauge() {
        return gauge;
    }

    /**
     * Sets the gauge in this locomotive association.
     *
     * @param gauge the gauge to set
     */

    public void setGauge(Gauge gauge) {
        this.gauge = gauge;
    }

    @Override
    public String toString() {
        return "LocomotiveGauge{" +
                "locomotive=" + locomotive +
                ", gauge=" + gauge +
                '}';
    }
}
