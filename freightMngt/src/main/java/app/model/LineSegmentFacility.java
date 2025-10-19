package app.model;

public class LineSegmentFacility {

    private Integer segmentFacilityId;
    private LineSegment lineSegment;
    private Facility facility;

    public LineSegmentFacility() {
        this.segmentFacilityId = null;
        this.lineSegment = null;
        this.facility = null;
    }

    public LineSegmentFacility(Integer segmentFacilityId, LineSegment lineSegment, Facility facility) {
        this.segmentFacilityId = segmentFacilityId;
        this.lineSegment = lineSegment;
        this.facility = facility;
    }

    public Integer getSegmentFacilityId() {
        return segmentFacilityId;
    }

    public void setSegmentFacilityId(Integer segmentFacilityId) {
        this.segmentFacilityId = segmentFacilityId;
    }

    public LineSegment getLineSegment() {
        return lineSegment;
    }

    public void setLineSegment(LineSegment lineSegment) {
        this.lineSegment = lineSegment;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }
}
