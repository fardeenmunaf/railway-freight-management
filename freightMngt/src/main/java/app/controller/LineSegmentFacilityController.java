package app.controller;

import app.model.LineSegmentFacility;
import app.repositories.LineSegmentFacilityRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing LineSegmentFacility entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for LineSegmentFacility objects.
 * This controller manages line segment facility relationships in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LineSegmentFacilityController {
    private final LineSegmentFacilityRepository repository;

    /**
     * Default constructor that initializes the controller with the line segment facility repository.
     */
    public LineSegmentFacilityController() {
        this.repository = Repositories.getInstance().getLineSegmentFacilityRepository();
    }

    /**
     * Creates and adds a new line segment facility relationship to the system.
     *
     * @param lineSegmentFacility the LineSegmentFacility object to be created
     * @return the created LineSegmentFacility object
     */
    public LineSegmentFacility createLineSegmentFacility(LineSegmentFacility lineSegmentFacility) {
        if (lineSegmentFacility != null) {
            repository.addLineSegmentFacility(lineSegmentFacility);
        }
        return lineSegmentFacility;
    }

    /**
     * Retrieves all line segment facilities from the system.
     *
     * @return a list of all LineSegmentFacility objects
     */
    public List<LineSegmentFacility> getAllLineSegmentFacilities() {
        return repository.getAllLineSegmentFacilities();
    }

    /**
     * Retrieves all line segment facilities for a specific line segment.
     *
     * @param lineSegmentId the line segment identifier
     * @return a list of all LineSegmentFacility objects for the specified line segment
     */
    public List<LineSegmentFacility> getLineSegmentFacilitiesByLineSegment(Integer lineSegmentId) {
        return repository.getLineSegmentFacilitiesBySegmentId(lineSegmentId);
    }

    /**
     * Deletes a line segment facility from the system.
     *
     * @param lineSegmentId the line segment identifier
     * @param facilityId the facility identifier
     * @return true if the line segment facility was deleted successfully, false otherwise
     */
    public boolean deleteLineSegmentFacility(Integer lineSegmentId, Integer facilityId) {
        return repository.deleteLineSegmentFacility(lineSegmentId, facilityId);
    }


    /**
     * Clears all line segment facilities from the system.
     */
    public void clearAllLineSegmentFacilities() {
        repository.clear();
    }
}
