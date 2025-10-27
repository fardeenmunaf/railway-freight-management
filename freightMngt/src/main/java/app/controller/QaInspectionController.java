package app.controller;

import app.model.QaInspection;
import app.repositories.QaInspectionRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing QaInspection entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for QaInspection objects.
 * This controller manages QA inspection data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class QaInspectionController {
    private final QaInspectionRepository repository;

    /**
     * Default constructor that initializes the controller with the QA inspection repository.
     */
    public QaInspectionController() {
        this.repository = Repositories.getInstance().getQaInspectionRepository();
    }

    /**
     * Creates and adds a new QA inspection to the system.
     *
     * @param qaInspection the QaInspection object to be created
     * @return the created QaInspection object
     */
    public QaInspection createQaInspection(QaInspection qaInspection) {
        if (qaInspection != null) {
            repository.addQaInspection(qaInspection);
        }
        return qaInspection;
    }

    /**
     * Retrieves all QA inspections from the system.
     *
     * @return a list of all QaInspection objects
     */
    public List<QaInspection> getAllQaInspections() {
        return repository.getAllQaInspections();
    }

    /**
     * Retrieves a QA inspection by its unique identifier.
     *
     * @param qaInspectionId the unique identifier of the QA inspection
     * @return an Optional containing the QaInspection if found, or empty if not found
     */
    public Optional<QaInspection> getQaInspectionById(Integer qaInspectionId) {
        QaInspection qaInspection = repository.getQaInspectionById(qaInspectionId);
        return Optional.ofNullable(qaInspection);
    }

    /**
     * Updates an existing QA inspection in the system.
     *
     * @param qaInspectionId the unique identifier of the QA inspection to update
     * @param updatedQaInspection the QaInspection object with updated information
     * @return true if the QA inspection was updated successfully, false otherwise
     */
    public boolean updateQaInspection(Integer qaInspectionId, QaInspection updatedQaInspection) {
        return repository.updateQaInspection(qaInspectionId, updatedQaInspection);
    }

    /**
     * Deletes a QA inspection from the system.
     *
     * @param qaInspectionId the unique identifier of the QA inspection to delete
     * @return true if the QA inspection was deleted successfully, false otherwise
     */
    public boolean deleteQaInspection(Integer qaInspectionId) {
        return repository.deleteQaInspection(qaInspectionId);
    }

    /**
     * Gets the next available QA inspection ID.
     *
     * @return the next QA inspection ID to be used
     */
    public Integer getNextQaInspectionId() {
        return repository.getNextInspectionId();
    }

    /**
     * Clears all QA inspections from the system.
     */
    public void clearAllQaInspections() {
        repository.clear();
    }
}
