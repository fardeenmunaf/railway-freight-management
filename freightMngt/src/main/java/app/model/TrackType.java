package app.model;

/**
 * Represents a track type in the freight management system.
 * Each track type defines the category or classification of railway track infrastructure,
 * such as mainline, siding, yard track, or industrial spur.
 * This class is used to categorize different types of railway tracks based on their purpose and characteristics.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class TrackType {

    private Integer trackTypeId;
    private String name;

    /**
     * Default constructor that creates a TrackType with null values.
     * Initializes all fields to null.
     */

    public TrackType() {
        this.trackTypeId = null;
        this.name = null;
    }

    /**
     * Parameterized constructor that creates a TrackType with specified values.
     *
     * @param trackTypeId the unique identifier for this track type
     * @param name the name of the track type
     */

    public TrackType(Integer trackTypeId, String name) {
        this.trackTypeId = trackTypeId;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this track type.
     *
     * @return the track type identifier as an Integer
     */

    public Integer getTrackTypeId() {
        return trackTypeId;
    }

    /**
     * Sets the unique identifier of this track type.
     *
     * @param trackTypeId the track type identifier to set
     */

    public void setTrackTypeId(Integer trackTypeId) {
        this.trackTypeId = trackTypeId;
    }

    /**
     * Gets the name of this track type.
     *
     * @return the track type name as a String
     */

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this track type.
     *
     * @param name the track type name to set
     */

    public void setName(String name) {
        this.name = name;
    }
}
