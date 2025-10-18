package app.model;

import javax.swing.text.StyledEditorKit;

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

    private Integer facility_id;
    private String name;
    private FacilityType facility_type;
    private Double lat;
    private Double lon;
    private Boolean is_intermodal;

    /**
     * Default constructor that creates a Facility with null/default values.
     * Initializes all fields to null.
     */

    public Facility() {
        this.facility_id = null;
        this.name = null;
        this.facility_type = null;
        this.lat = null;
        this.lon = null;
        this.is_intermodal = null;
    }

    /**
     * Parameterized constructor that creates a Facility with specified values.
     *
     * @param facility_id   the unique identifier for this facility
     * @param name          the name of the facility
     * @param facility_type the type/category of this facility
     * @param lat           the latitude coordinate in degrees
     * @param lon           the longitude coordinate in degrees
     * @param is_intermodal true if the facility supports intermodal operations, false otherwise
     */

    public Facility(Integer facility_id, String name, FacilityType facility_type, Double lat, Double lon, Boolean is_intermodal) {
        this.facility_id = facility_id;
        this.name = name;
        this.facility_type = facility_type;
        this.lat = lat;
        this.lon = lon;
        this.is_intermodal = is_intermodal;
    }

    /**
     * Gets the unique identifier of this facility.
     *
     * @return the facility identifier as an Integer
     */

    public Integer getFacilityId() {
        return facility_id;
    }

    /**
     * Sets the unique identifier of this facility.
     *
     * @param facility_id the facility identifier to set
     */

    public void setFacilityId(Integer facility_id) {
        this.facility_id = facility_id;
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
        return facility_type;
    }

    /**
     * Sets the type of this facility.
     *
     * @param facility_type the facility type to set
     */

    public void setFacilityType(FacilityType facility_type) {
        this.facility_type = facility_type;
    }

    /**
     * Gets the latitude coordinate of this facility.
     *
     * @return the latitude in degrees as a double
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
     * @return the longitude in degrees as a double
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
     * @return true if the facility is intermodal, false otherwise
     */

    public Boolean getIsIntermodal() {
        return is_intermodal;
    }

    /**
     * Sets whether this facility supports intermodal operations.
     *
     * @param is_intermodal true to mark as intermodal, false otherwise
     */

    public void setIsIntermodal(Boolean is_intermodal) {
        this.is_intermodal = is_intermodal;
    }
}
