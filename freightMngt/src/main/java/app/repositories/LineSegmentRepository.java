package app.repositories;

import app.model.LineSegment;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing line segments in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LineSegmentRepository {

    private final List<LineSegment> lineSegments = new ArrayList<>();

    public void addLineSegment(LineSegment lineSegment) {
        if (lineSegment != null && lineSegment.getSegmentId() != null) {
            if (!containsLineSegment(lineSegment.getSegmentId())) {
                lineSegments.add(lineSegment);
            }
        }
    }

    public List<LineSegment> getAllLineSegments() {
        return new ArrayList<>(lineSegments);
    }

    public LineSegment getLineSegmentById(Integer segmentId) {
        for (LineSegment segment : lineSegments) {
            if (segmentId.equals(segment.getSegmentId())) {
                return segment;
            }
        }
        return null;
    }

    public boolean updateLineSegment(Integer segmentId, LineSegment updatedSegment) {
        LineSegment existingSegment = getLineSegmentById(segmentId);
        if (existingSegment != null) {
            int index = lineSegments.indexOf(existingSegment);
            lineSegments.set(index, updatedSegment);
            return true;
        }
        return false;
    }

    public boolean deleteLineSegment(Integer segmentId) {
        return lineSegments.removeIf(segment -> segmentId.equals(segment.getSegmentId()));
    }

    public Integer getNextSegmentId() {
        int maxId = 0;
        for (LineSegment segment : lineSegments) {
            if (segment.getSegmentId() != null && segment.getSegmentId() > maxId) {
                maxId = segment.getSegmentId();
            }
        }
        return maxId + 1;
    }

    private boolean containsLineSegment(Integer segmentId) {
        return getLineSegmentById(segmentId) != null;
    }

    public void clear() {
        lineSegments.clear();
    }
}

