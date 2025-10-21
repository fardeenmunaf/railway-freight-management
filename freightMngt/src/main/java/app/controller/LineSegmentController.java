package app.controller;

import app.model.LineSegment;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing LineSegment entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for LineSegment objects.
 * This controller manages line segment data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LineSegmentController {
    private List<LineSegment> lineSegments;

    /**
     * Default constructor that initializes the line segment list.
     */
    public LineSegmentController() {
        this.lineSegments = new ArrayList<>();
    }

    /**
     * Creates and adds a new line segment to the system.
     *
     * @param lineSegment the LineSegment object to be created
     * @return the created LineSegment object
     */
    public LineSegment createLineSegment(LineSegment lineSegment) {
        if (lineSegment != null) {
            lineSegments.add(lineSegment);
        }
        return lineSegment;
    }

    /**
     * Retrieves all line segments from the system.
     *
     * @return a list of all LineSegment objects
     */
    public List<LineSegment> getAllLineSegments() {
        return new ArrayList<>(lineSegments);
    }

    /**
     * Retrieves a line segment by its unique identifier.
     *
     * @param segmentId the unique identifier of the line segment
     * @return an Optional containing the LineSegment if found, or empty if not found
     */
    public Optional<LineSegment> getLineSegmentById(Integer segmentId) {
        return lineSegments.stream()
                .filter(lineSegment -> lineSegment.getSegmentId().equals(segmentId))
                .findFirst();
    }

    /**
     * Updates an existing line segment in the system.
     *
     * @param segmentId the unique identifier of the line segment to update
     * @param updatedLineSegment the LineSegment object with updated information
     * @return true if the line segment was updated successfully, false otherwise
     */
    public boolean updateLineSegment(Integer segmentId, LineSegment updatedLineSegment) {
        Optional<LineSegment> existingLineSegment = getLineSegmentById(segmentId);
        if (existingLineSegment.isPresent()) {
            int index = lineSegments.indexOf(existingLineSegment.get());
            lineSegments.set(index, updatedLineSegment);
            return true;
        }
        return false;
    }

    /**
     * Deletes a line segment from the system.
     *
     * @param segmentId the unique identifier of the line segment to delete
     * @return true if the line segment was deleted successfully, false otherwise
     */
    public boolean deleteLineSegment(Integer segmentId) {
        return lineSegments.removeIf(lineSegment -> lineSegment.getSegmentId().equals(segmentId));
    }
}
