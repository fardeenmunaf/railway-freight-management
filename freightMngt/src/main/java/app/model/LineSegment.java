package app.model;

/**
 * Represents a segment of a railway line in the freight management system.
 * Each segment is a section of track with specific characteristics such as length,
 * track type, electrification, gauge, weight limits, speed limits, and track configuration.
 * Segments are ordered within a line by position and may connect to facilities.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class LineSegment {

    private Integer segmentId;
    private Line line;
    private Double lengthKm;
    private TrackType trackType;
    private Boolean electrifType;
    private Gauge gauge;
    private Double maxWeightKgM;
    private Double speedLimitKmh;
    private Boolean singleTrack;
    private Integer position;

    /**
     * Default constructor that creates a LineSegment with null values.
     * Initializes all fields to null.
     */

    public LineSegment() {
        this.segmentId = null;
        this.line = null;
        this.lengthKm = null;
        this.trackType = null;
        this.electrifType = null;
        this.gauge = null;
        this.maxWeightKgM = null;
        this.speedLimitKmh = null;
        this.singleTrack = null;
        this.position = null;
    }

    /**
     * Parameterized constructor that creates a LineSegment with specified values.
     *
     * @param segmentId the unique identifier for this line segment
     * @param line the railway line to which this segment belongs
     * @param lengthKm the length of the segment in kilometers
     * @param trackType the type of track used in this segment
     * @param electrifType whether the segment is electrified
     * @param gauge the gauge specification for this segment
     * @param maxWeightKgM the maximum weight per meter in kilograms per meter
     * @param speedLimitKmh the speed limit in kilometers per hour
     * @param singleTrack whether this is a single track segment
     * @param position the position of this segment within the line
     */

    public LineSegment(Integer segmentId, Line line, Double lengthKm, TrackType trackType, Boolean electrifType, Gauge gauge, Double maxWeightKgM, Double speedLimitKmh, Boolean singleTrack, Integer position) {
        this.segmentId = segmentId;
        this.line = line;
        this.lengthKm = lengthKm;
        this.trackType = trackType;
        this.electrifType = electrifType;
        this.gauge = gauge;
        this.maxWeightKgM = maxWeightKgM;
        this.speedLimitKmh = speedLimitKmh;
        this.singleTrack = singleTrack;
        this.position = position;
    }

    /**
     * Gets the unique identifier of this line segment.
     *
     * @return the segment identifier as an Integer
     */

    public Integer getSegmentId() {
        return segmentId;
    }

    /**
     * Sets the unique identifier of this line segment.
     *
     * @param segmentId the segment identifier to set
     */

    public void setSegmentId(Integer segmentId) {
        this.segmentId = segmentId;
    }

    /**
     * Gets the railway line to which this segment belongs.
     *
     * @return the line as a Line object
     */

    public Line getLine() {
        return line;
    }

    /**
     * Sets the railway line to which this segment belongs.
     *
     * @param line the line to set
     */

    public void setLine(Line line) {
        this.line = line;
    }

    /**
     * Gets the length of this segment in kilometers.
     *
     * @return the length as a Double in kilometers
     */

    public Double getLengthKm() {
        return lengthKm;
    }

    /**
     * Sets the length of this segment in kilometers.
     *
     * @param lengthKm the length to set in kilometers
     */

    public void setLengthKm(Double lengthKm) {
        this.lengthKm = lengthKm;
    }

    /**
     * Gets the track type of this segment.
     *
     * @return the track type as a TrackType object
     */

    public TrackType getTrackType() {
        return trackType;
    }

    /**
     * Sets the track type of this segment.
     *
     * @param trackType the track type to set
     */

    public void setTrackType(TrackType trackType) {
        this.trackType = trackType;
    }

    /**
     * Gets whether this segment is electrified.
     *
     * @return true if electrified, false otherwise, as a Boolean
     */

    public Boolean getElectrifType() {
        return electrifType;
    }

    /**
     * Sets whether this segment is electrified.
     *
     * @param electrifType the electrification status to set
     */

    public void setElectrifType(Boolean electrifType) {
        this.electrifType = electrifType;
    }

    /**
     * Gets the gauge specification for this segment.
     *
     * @return the gauge as a Gauge object
     */

    public Gauge getGauge() {
        return gauge;
    }

    /**
     * Sets the gauge specification for this segment.
     *
     * @param gauge the gauge to set
     */

    public void setGauge(Gauge gauge) {
        this.gauge = gauge;
    }

    /**
     * Gets the maximum weight per meter for this segment.
     *
     * @return the maximum weight as a Double in kilograms per meter
     */

    public Double getMaxWeightKgM() {
        return maxWeightKgM;
    }

    /**
     * Sets the maximum weight per meter for this segment.
     *
     * @param maxWeightKgM the maximum weight to set in kilograms per meter
     */

    public void setMaxWeightKgM(Double maxWeightKgM) {
        this.maxWeightKgM = maxWeightKgM;
    }

    /**
     * Gets the speed limit for this segment.
     *
     * @return the speed limit as a Double in kilometers per hour
     */

    public Double getSpeedLimitKmh() {
        return speedLimitKmh;
    }

    /**
     * Sets the speed limit for this segment.
     *
     * @param speedLimitKmh the speed limit to set in kilometers per hour
     */

    public void setSpeedLimitKmh(Double speedLimitKmh) {
        this.speedLimitKmh = speedLimitKmh;
    }

    /**
     * Gets whether this is a single track segment.
     *
     * @return true if single track, false if multiple tracks, as a Boolean
     */

    public Boolean getSingleTrack() {
        return singleTrack;
    }

    /**
     * Sets whether this is a single track segment.
     *
     * @param singleTrack the single track status to set
     */

    public void setSingleTrack(Boolean singleTrack) {
        this.singleTrack = singleTrack;
    }

    /**
     * Gets the position of this segment within the line.
     *
     * @return the position as an Integer
     */

    public Integer getPosition() {
        return position;
    }

    /**
     * Sets the position of this segment within the line.
     *
     * @param position the position to set
     */

    public void setPosition(Integer position) {
        this.position = position;
    }
}
