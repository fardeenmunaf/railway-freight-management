package app.controller;

import app.model.ReturnLine;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing ReturnLine entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for ReturnLine objects.
 * This controller manages return line data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ReturnLineController {
    private List<ReturnLine> returnLines;

    /**
     * Default constructor that initializes the return line list.
     */
    public ReturnLineController() {
        this.returnLines = new ArrayList<>();
    }

    /**
     * Creates and adds a new return line to the system.
     *
     * @param returnLine the ReturnLine object to be created
     * @return the created ReturnLine object
     */
    public ReturnLine createReturnLine(ReturnLine returnLine) {
        if (returnLine != null) {
            returnLines.add(returnLine);
        }
        return returnLine;
    }

    /**
     * Retrieves all return lines from the system.
     *
     * @return a list of all ReturnLine objects
     */
    public List<ReturnLine> getAllReturnLines() {
        return new ArrayList<>(returnLines);
    }

    /**
     * Retrieves a return line by its unique identifier.
     *
     * @param returnLineId the unique identifier of the return line
     * @return an Optional containing the ReturnLine if found, or empty if not found
     */
    public Optional<ReturnLine> getReturnLineById(Integer returnLineId) {
        return returnLines.stream()
                .filter(returnLine -> returnLine.getReturnLineId().equals(returnLineId))
                .findFirst();
    }

    /**
     * Updates an existing return line in the system.
     *
     * @param returnLineId the unique identifier of the return line to update
     * @param updatedReturnLine the ReturnLine object with updated information
     * @return true if the return line was updated successfully, false otherwise
     */
    public boolean updateReturnLine(Integer returnLineId, ReturnLine updatedReturnLine) {
        Optional<ReturnLine> existingReturnLine = getReturnLineById(returnLineId);
        if (existingReturnLine.isPresent()) {
            int index = returnLines.indexOf(existingReturnLine.get());
            returnLines.set(index, updatedReturnLine);
            return true;
        }
        return false;
    }

    /**
     * Deletes a return line from the system.
     *
     * @param returnLineId the unique identifier of the return line to delete
     * @return true if the return line was deleted successfully, false otherwise
     */
    public boolean deleteReturnLine(Integer returnLineId) {
        return returnLines.removeIf(returnLine -> returnLine.getReturnLineId().equals(returnLineId));
    }
}

