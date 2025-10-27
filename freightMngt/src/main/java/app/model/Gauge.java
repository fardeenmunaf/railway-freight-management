package app.model;

/**
 * Represents a rail gauge specification in the freight management system.
 * Each gauge defines the track width standard used by railway rolling stock,
 * including an identifier, descriptive name, and the physical gauge size in millimeters.
 * This class is essential for ensuring compatibility between rolling stock and track infrastructure.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Gauge {
    private Integer gaugeId;
    private String gaugeName;
    private Double gaugeSize; // in millimeters

    /**
     * Default constructor that creates a Gauge with null values.
     * Initializes all fields to null.
     */

    public Gauge() {
        gaugeId = null;
        gaugeName = null;
        gaugeSize = null;
    }

    /**
     * Parameterized constructor that creates a Gauge with specified values.
     *
     * @param gaugeId the unique identifier for this gauge
     * @param gaugeName the descriptive name of the gauge
     * @param gaugeSize the physical gauge size in millimeters
     */

    public Gauge(Integer gaugeId, String gaugeName, Double gaugeSize) {
        this.gaugeId = gaugeId;
        this.gaugeName = gaugeName;
        this.gaugeSize = gaugeSize;
    }

    /**
     * Gets the unique identifier of this gauge.
     *
     * @return the gauge identifier as an Integer
     */

    public Integer getGaugeId() {
        return gaugeId;
    }

    /**
     * Sets the unique identifier of this gauge.
     *
     * @param gaugeId the gauge identifier to set
     */

    public void setGaugeId(Integer gaugeId) {
        this.gaugeId = gaugeId;
    }

    /**
     * Gets the descriptive name of this gauge.
     *
     * @return the gauge name as a String
     */

    public String getGaugeName() {
        return gaugeName;
    }

    /**
     * Sets the descriptive name of this gauge.
     *
     * @param gaugeName the gauge name to set
     */

    public void setGaugeName(String gaugeName) {
        this.gaugeName = gaugeName;
    }

    /**
     * Gets the physical gauge size in millimeters.
     *
     * @return the gauge size as a Double in millimeters
     */

    public Double getGaugeSize() {
        return gaugeSize;
    }

    /**
     * Sets the physical gauge size in millimeters.
     *
     * @param gaugeSize the gauge size to set in millimeters
     */

    public void setGaugeSize(Double gaugeSize) {
        this.gaugeSize = gaugeSize;
    }

    @Override
    public String toString() {
        return "Gauge{" +
                "gaugeId=" + gaugeId +
                ", gaugeName='" + gaugeName + '\'' +
                ", gaugeSize=" + gaugeSize +
                '}';
    }
}
