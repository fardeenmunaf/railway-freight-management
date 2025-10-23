package app.controller;

import app.model.LineSegment;
import app.repositories.LineSegmentRepository;
import app.repositories.Repositories;
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
    private final LineSegmentRepository repository;

    /**
     * Default constructor that initializes the controller with the line segment repository.
     */
    public LineSegmentController() {
        this.repository = Repositories.getInstance().getLineSegmentRepository();
    }

    /**
     * Creates and adds a new line segment to the system.
     *
     * @param lineSegment the LineSegment object to be created
     * @return the created LineSegment object
     */
    public LineSegment createLineSegment(LineSegment lineSegment) {
        if (lineSegment != null) {
            repository.addLineSegment(lineSegment);
        }
        return lineSegment;
    }

    /**
     * Retrieves all line segments from the system.
     *
     * @return a list of all LineSegment objects
     */
    public List<LineSegment> getAllLineSegments() {
        return repository.getAllLineSegments();
    }

    /**
     * Retrieves a line segment by its unique identifier.
     *
     * @param lineSegmentId the unique identifier of the line segment
     * @return an Optional containing the LineSegment if found, or empty if not found
     */
    public Optional<LineSegment> getLineSegmentById(Integer lineSegmentId) {
        LineSegment lineSegment = repository.getLineSegmentById(lineSegmentId);
        return Optional.ofNullable(lineSegment);
    }

    /**
     * Updates an existing line segment in the system.
     *
     * @param lineSegmentId the unique identifier of the line segment to update
     * @param updatedLineSegment the LineSegment object with updated information
     * @return true if the line segment was updated successfully, false otherwise
     */
    public boolean updateLineSegment(Integer lineSegmentId, LineSegment updatedLineSegment) {
        return repository.updateLineSegment(lineSegmentId, updatedLineSegment);
    }

    /**
     * Deletes a line segment from the system.
     *
     * @param lineSegmentId the unique identifier of the line segment to delete
     * @return true if the line segment was deleted successfully, false otherwise
     */
    public boolean deleteLineSegment(Integer lineSegmentId) {
        return repository.deleteLineSegment(lineSegmentId);
    }

    /**
     * Gets the next available line segment ID.
     *
     * @return the next line segment ID to be used
     */
    public Integer getNextLineSegmentId() {
        return repository.getNextSegmentId();
    }

    /**
     * Clears all line segments from the system.
     */
    public void clearAllLineSegments() {
        repository.clear();
    }
}
