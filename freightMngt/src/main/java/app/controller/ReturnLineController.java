package app.controller;

import app.model.ReturnLine;
import app.repositories.ReturnLineRepository;
import app.repositories.Repositories;
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
    private final ReturnLineRepository repository;

    /**
     * Default constructor that initializes the controller with the return line repository.
     */
    public ReturnLineController() {
        this.repository = Repositories.getInstance().getReturnLineRepository();
    }

    /**
     * Creates and adds a new return line to the system.
     *
     * @param returnLine the ReturnLine object to be created
     * @return the created ReturnLine object
     */
    public ReturnLine createReturnLine(ReturnLine returnLine) {
        if (returnLine != null) {
            repository.addReturnLine(returnLine);
        }
        return returnLine;
    }

    /**
     * Retrieves all return lines from the system.
     *
     * @return a list of all ReturnLine objects
     */
    public List<ReturnLine> getAllReturnLines() {
        return repository.getAllReturnLines();
    }

    /**
     * Retrieves a return line by its unique identifier.
     *
     * @param returnLineId the unique identifier of the return line
     * @return an Optional containing the ReturnLine if found, or empty if not found
     */
    public Optional<ReturnLine> getReturnLineById(Integer returnLineId) {
        ReturnLine returnLine = repository.getReturnLineById(returnLineId);
        return Optional.ofNullable(returnLine);
    }

    /**
     * Updates an existing return line in the system.
     *
     * @param returnLineId the unique identifier of the return line to update
     * @param updatedReturnLine the ReturnLine object with updated information
     * @return true if the return line was updated successfully, false otherwise
     */
    public boolean updateReturnLine(Integer returnLineId, ReturnLine updatedReturnLine) {
        return repository.updateReturnLine(returnLineId, updatedReturnLine);
    }

    /**
     * Deletes a return line from the system.
     *
     * @param returnLineId the unique identifier of the return line to delete
     * @return true if the return line was deleted successfully, false otherwise
     */
    public boolean deleteReturnLine(Integer returnLineId) {
        return repository.deleteReturnLine(returnLineId);
    }

    /**
     * Gets the next available return line ID.
     *
     * @return the next return line ID to be used
     */
    public Integer getNextReturnLineId() {
        return repository.getNextReturnLineId();
    }

    /**
     * Clears all return lines from the system.
     */
    public void clearAllReturnLines() {
        repository.clear();
    }
}
