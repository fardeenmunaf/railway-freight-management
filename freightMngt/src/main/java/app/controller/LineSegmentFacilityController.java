package app.controller;

import app.model.LineSegmentFacility;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing LineSegmentFacility entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for LineSegmentFacility objects.
 * This controller manages line segment facility data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LineSegmentFacilityController {
    private List<LineSegmentFacility> lineSegmentFacilities;

    /**
     * Default constructor that initializes the line segment facility list.
     */
    public LineSegmentFacilityController() {
        this.lineSegmentFacilities = new ArrayList<>();
    }

    /**
     * Creates and adds a new line segment facility to the system.
     *
     * @param lineSegmentFacility the LineSegmentFacility object to be created
     * @return the created LineSegmentFacility object
     */
    public LineSegmentFacility createLineSegmentFacility(LineSegmentFacility lineSegmentFacility) {
        if (lineSegmentFacility != null) {
            lineSegmentFacilities.add(lineSegmentFacility);
        }
        return lineSegmentFacility;
    }

    /**
     * Retrieves all line segment facilities from the system.
     *
     * @return a list of all LineSegmentFacility objects
     */
    public List<LineSegmentFacility> getAllLineSegmentFacilities() {
        return new ArrayList<>(lineSegmentFacilities);
    }

    /**
     * Retrieves a line segment facility by its unique identifier.
     *
     * @param lineSegmentFacilityId the unique identifier of the line segment facility
     * @return an Optional containing the LineSegmentFacility if found, or empty if not found
     */
    public Optional<LineSegmentFacility> getLineSegmentFacilityById(Integer lineSegmentFacilityId) {
        return lineSegmentFacilities.stream()
                .filter(lineSegmentFacility -> lineSegmentFacility.getLineSegmentFacilityId().equals(lineSegmentFacilityId))
                .findFirst();
    }

    /**
     * Updates an existing line segment facility in the system.
     *
     * @param lineSegmentFacilityId the unique identifier of the line segment facility to update
     * @param updatedLineSegmentFacility the LineSegmentFacility object with updated information
     * @return true if the line segment facility was updated successfully, false otherwise
     */
    public boolean updateLineSegmentFacility(Integer lineSegmentFacilityId, LineSegmentFacility updatedLineSegmentFacility) {
        Optional<LineSegmentFacility> existingLineSegmentFacility = getLineSegmentFacilityById(lineSegmentFacilityId);
        if (existingLineSegmentFacility.isPresent()) {
            int index = lineSegmentFacilities.indexOf(existingLineSegmentFacility.get());
            lineSegmentFacilities.set(index, updatedLineSegmentFacility);
            return true;
        }
        return false;
    }

    /**
     * Deletes a line segment facility from the system.
     *
     * @param lineSegmentFacilityId the unique identifier of the line segment facility to delete
     * @return true if the line segment facility was deleted successfully, false otherwise
     */
    public boolean deleteLineSegmentFacility(Integer lineSegmentFacilityId) {
        return lineSegmentFacilities.removeIf(lineSegmentFacility -> lineSegmentFacility.getLineSegmentFacilityId().equals(lineSegmentFacilityId));
    }
}

