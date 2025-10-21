package app.model;


/**
 * Represents a facility in the freight management system.
 * A facility is a physical location such as a warehouse, station, or terminal
 * that can be part of the transportation and logistics network.
 * Each facility has geographic coordinates and can be designated as intermodal.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Facility {

    private Integer facilityId;
    private String name;
    private FacilityType facilityType;
    private Double lat;
    private Double lon;
    private Boolean isIntermodal;

    /**
     * Default constructor that creates a Facility with null/default values.
     * Initializes all fields to null.
     */

    public Facility() {
        this.facilityId = null;
        this.name = null;
        this.facilityType = null;
        this.lat = null;
        this.lon = null;
        this.isIntermodal = null;
    }

    /**
     * Parameterized constructor that creates a Facility with specified values.
     *
     * @param facilityId    the unique identifier for this facility
     * @param name          the name of the facility
     * @param facilityType  the type/category of this facility
     * @param lat           the latitude coordinate in degrees
     * @param lon           the longitude coordinate in degrees
     * @param isIntermodal  true if the facility supports intermodal operations, false otherwise
     */

    public Facility(Integer facilityId, String name, FacilityType facilityType, Double lat, Double lon, Boolean isIntermodal) {
        this.facilityId = facilityId;
        this.name = name;
        this.facilityType = facilityType;
        this.lat = lat;
        this.lon = lon;
        this.isIntermodal = isIntermodal;
    }

    /**
     * Gets the unique identifier of this facility.
     *
     * @return the facility identifier as an Integer
     */

    public Integer getFacilityId() {
        return facilityId;
    }

    /**
     * Sets the unique identifier of this facility.
     *
     * @param facilityId the facility identifier to set
     */

    public void setFacilityId(Integer facilityId) {
        this.facilityId = facilityId;
    }

    /**
     * Gets the name of this facility.
     *
     * @return the facility name as a String
     */

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this facility.
     *
     * @param name the facility name to set
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the type of this facility.
     *
     * @return the facility type as a FacilityType object
     */

    public FacilityType getFacilityType() {
        return facilityType;
    }

    /**
     * Sets the type of this facility.
     *
     * @param facilityType the facility type to set
     */

    public void setFacilityType(FacilityType facilityType) {
        this.facilityType = facilityType;
    }

    /**
     * Gets the latitude coordinate of this facility.
     *
     * @return the latitude in degrees as a Double
     */

    public Double getLat() {
        return lat;
    }

    /**
     * Sets the latitude coordinate of this facility.
     *
     * @param lat the latitude in degrees to set
     */

    public void setLat(Double lat) {
        this.lat = lat;
    }

    /**
     * Gets the longitude coordinate of this facility.
     *
     * @return the longitude in degrees as a Double
     */

    public Double getLon() {
        return lon;
    }

    /**
     * Sets the longitude coordinate of this facility.
     *
     * @param lon the longitude in degrees to set
     */

    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     * Gets whether this facility supports intermodal operations.
     *
     * @return true if the facility is intermodal, false otherwise, as a Boolean
     */

    public Boolean getIsIntermodal() {
        return isIntermodal;
    }

    /**
     * Sets whether this facility supports intermodal operations.
     *
     * @param isIntermodal true to mark as intermodal, false otherwise
     */

    public void setIsIntermodal(Boolean isIntermodal) {
        this.isIntermodal = isIntermodal;
    }
}
