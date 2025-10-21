package app.repositories;

import app.model.LineSegmentFacility;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing line segment facilities in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LineSegmentFacilityRepository {

    private final List<LineSegmentFacility> lineSegmentFacilities = new ArrayList<>();

    public void addLineSegmentFacility(LineSegmentFacility lsf) {
        if (lsf != null) {
            lineSegmentFacilities.add(lsf);
        }
    }

    public List<LineSegmentFacility> getAllLineSegmentFacilities() {
        return new ArrayList<>(lineSegmentFacilities);
    }

    public List<LineSegmentFacility> getLineSegmentFacilitiesBySegmentId(Integer segmentId) {
        List<LineSegmentFacility> result = new ArrayList<>();
        for (LineSegmentFacility lsf : lineSegmentFacilities) {
            if (lsf.getLineSegment() != null && segmentId != null &&
                segmentId.equals(lsf.getLineSegment().getSegmentId())) {
                result.add(lsf);
            }
        }
        return result;
    }

    public boolean deleteLineSegmentFacility(Integer segmentId, Integer facilityId) {
        return lineSegmentFacilities.removeIf(lsf ->
            lsf.getLineSegment() != null && lsf.getFacility() != null &&
            segmentId.equals(lsf.getLineSegment().getSegmentId()) &&
            facilityId.equals(lsf.getFacility().getFacilityId()));
    }

    public void clear() {
        lineSegmentFacilities.clear();
    }
}
