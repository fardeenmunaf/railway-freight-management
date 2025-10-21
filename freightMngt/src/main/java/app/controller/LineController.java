package app.controller;

import app.model.Line;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Line entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Line objects.
 * This controller manages line data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LineController {
    private List<Line> lines;

    /**
     * Default constructor that initializes the line list.
     */
    public LineController() {
        this.lines = new ArrayList<>();
    }

    /**
     * Creates and adds a new line to the system.
     *
     * @param line the Line object to be created
     * @return the created Line object
     */
    public Line createLine(Line line) {
        if (line != null) {
            lines.add(line);
        }
        return line;
    }

    /**
     * Retrieves all lines from the system.
     *
     * @return a list of all Line objects
     */
    public List<Line> getAllLines() {
        return new ArrayList<>(lines);
    }

    /**
     * Retrieves a line by its unique identifier.
     *
     * @param lineId the unique identifier of the line
     * @return an Optional containing the Line if found, or empty if not found
     */
    public Optional<Line> getLineById(Integer lineId) {
        return lines.stream()
                .filter(line -> line.getLineId().equals(lineId))
                .findFirst();
    }

    /**
     * Updates an existing line in the system.
     *
     * @param lineId the unique identifier of the line to update
     * @param updatedLine the Line object with updated information
     * @return true if the line was updated successfully, false otherwise
     */
    public boolean updateLine(Integer lineId, Line updatedLine) {
        Optional<Line> existingLine = getLineById(lineId);
        if (existingLine.isPresent()) {
            int index = lines.indexOf(existingLine.get());
            lines.set(index, updatedLine);
            return true;
        }
        return false;
    }

    /**
     * Deletes a line from the system.
     *
     * @param lineId the unique identifier of the line to delete
     * @return true if the line was deleted successfully, false otherwise
     */
    public boolean deleteLine(Integer lineId) {
        return lines.removeIf(line -> line.getLineId().equals(lineId));
    }
}
