package app.model;

/**
 * Represents a parking track in the freight management system.
 * Each parking track is a designated railway track at a facility where trains can park,
 * load, or unload. The track has a specific capacity measured in number of wagons.
 * This class is essential for managing track utilization and train scheduling at facilities.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class ParkingTrack {

    private Integer parkingTrackId;
    private Line line;
    private Facility facility;
    private String name;
    private Integer capacityWagons;

    /**
     * Default constructor that creates a ParkingTrack with null values.
     * Initializes all fields to null.
     */

    public ParkingTrack() {
        this.parkingTrackId = null;
        this.line = null;
        this.facility = null;
        this.name = null;
        this.capacityWagons = null;
    }

    /**
     * Parameterized constructor that creates a ParkingTrack with specified values.
     *
     * @param parkingTrackId the unique identifier for this parking track
     * @param line the railway line this track belongs to
     * @param facility the facility where this track is located
     * @param name the name of the parking track
     * @param capacityWagons the capacity in number of wagons
     */

    public ParkingTrack(Integer parkingTrackId, Line line, Facility facility, String name, Integer capacityWagons) {
        this.parkingTrackId = parkingTrackId;
        this.line = line;
        this.facility = facility;
        this.name = name;
        this.capacityWagons = capacityWagons;
    }

    /**
     * Gets the unique identifier of this parking track.
     *
     * @return the parking track identifier as an Integer
     */

    public Integer getParkingTrackId() {
        return parkingTrackId;
    }

    /**
     * Sets the unique identifier of this parking track.
     *
     * @param parkingTrackId the parking track identifier to set
     */

    public void setParkingTrackId(Integer parkingTrackId) {
        this.parkingTrackId = parkingTrackId;
    }

    /**
     * Gets the railway line this track belongs to.
     *
     * @return the line as a Line object
     */

    public Line getLine() {
        return line;
    }

    /**
     * Sets the railway line this track belongs to.
     *
     * @param line the line to set
     */

    public void setLine(Line line) {
        this.line = line;
    }

    /**
     * Gets the facility where this track is located.
     *
     * @return the facility as a Facility object
     */

    public Facility getFacility() {
        return facility;
    }

    /**
     * Sets the facility where this track is located.
     *
     * @param facility the facility to set
     */

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    /**
     * Gets the name of this parking track.
     *
     * @return the track name as a String
     */

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this parking track.
     *
     * @param name the track name to set
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the capacity in number of wagons.
     *
     * @return the capacity as an Integer
     */

    public Integer getCapacityWagons() {
        return capacityWagons;
    }

    /**
     * Sets the capacity in number of wagons.
     *
     * @param capacityWagons the capacity to set
     */

    public void setCapacityWagons(Integer capacityWagons) {
        this.capacityWagons = capacityWagons;
    }
}
