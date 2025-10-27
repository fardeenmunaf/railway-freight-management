package app.model;

/**
 * Represents the association between a line segment and a facility in the freight management system.
 * This class creates a relationship between railway line segments and facilities
 * (such as stations, depots, or terminals) located along those segments.
 * Used for tracking which facilities are accessible from specific line segments.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class LineSegmentFacility {

    private Integer segmentFacilityId;
    private LineSegment lineSegment;
    private Facility facility;

    /**
     * Default constructor that creates a LineSegmentFacility with null values.
     * Initializes all fields to null.
     */

    public LineSegmentFacility() {
        this.segmentFacilityId = null;
        this.lineSegment = null;
        this.facility = null;
    }

    /**
     * Parameterized constructor that creates a LineSegmentFacility with specified values.
     *
     * @param segmentFacilityId the unique identifier for this segment-facility association
     * @param lineSegment the line segment in the relationship
     * @param facility the facility in the relationship
     */

    public LineSegmentFacility(Integer segmentFacilityId, LineSegment lineSegment, Facility facility) {
        this.segmentFacilityId = segmentFacilityId;
        this.lineSegment = lineSegment;
        this.facility = facility;
    }

    /**
     * Gets the unique identifier of this segment-facility association.
     *
     * @return the segment-facility identifier as an Integer
     */

    public Integer getSegmentFacilityId() {
        return segmentFacilityId;
    }

    /**
     * Sets the unique identifier of this segment-facility association.
     *
     * @param segmentFacilityId the segment-facility identifier to set
     */

    public void setSegmentFacilityId(Integer segmentFacilityId) {
        this.segmentFacilityId = segmentFacilityId;
    }

    /**
     * Gets the line segment in this association.
     *
     * @return the line segment as a LineSegment object
     */

    public LineSegment getLineSegment() {
        return lineSegment;
    }

    /**
     * Sets the line segment in this association.
     *
     * @param lineSegment the line segment to set
     */

    public void setLineSegment(LineSegment lineSegment) {
        this.lineSegment = lineSegment;
    }

    /**
     * Gets the facility in this association.
     *
     * @return the facility as a Facility object
     */

    public Facility getFacility() {
        return facility;
    }

    /**
     * Sets the facility in this association.
     *
     * @param facility the facility to set
     */

    public void setFacility(Facility facility) {
        this.facility = facility;
    }
}
