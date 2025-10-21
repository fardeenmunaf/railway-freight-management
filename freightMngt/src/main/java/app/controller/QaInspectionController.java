package app.controller;

import app.model.QaInspection;
import java.util.ArrayList;
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
    private List<QaInspection> qaInspections;

    /**
     * Default constructor that initializes the QA inspection list.
     */
    public QaInspectionController() {
        this.qaInspections = new ArrayList<>();
    }

    /**
     * Creates and adds a new QA inspection to the system.
     *
     * @param qaInspection the QaInspection object to be created
     * @return the created QaInspection object
     */
    public QaInspection createQaInspection(QaInspection qaInspection) {
        if (qaInspection != null) {
            qaInspections.add(qaInspection);
        }
        return qaInspection;
    }

    /**
     * Retrieves all QA inspections from the system.
     *
     * @return a list of all QaInspection objects
     */
    public List<QaInspection> getAllQaInspections() {
        return new ArrayList<>(qaInspections);
    }

    /**
     * Retrieves a QA inspection by its unique identifier.
     *
     * @param qaInspectionId the unique identifier of the QA inspection
     * @return an Optional containing the QaInspection if found, or empty if not found
     */
    public Optional<QaInspection> getQaInspectionById(Integer qaInspectionId) {
        return qaInspections.stream()
                .filter(qaInspection -> qaInspection.getQaInspectionId().equals(qaInspectionId))
                .findFirst();
    }

    /**
     * Updates an existing QA inspection in the system.
     *
     * @param qaInspectionId the unique identifier of the QA inspection to update
     * @param updatedQaInspection the QaInspection object with updated information
     * @return true if the QA inspection was updated successfully, false otherwise
     */
    public boolean updateQaInspection(Integer qaInspectionId, QaInspection updatedQaInspection) {
        Optional<QaInspection> existingQaInspection = getQaInspectionById(qaInspectionId);
        if (existingQaInspection.isPresent()) {
            int index = qaInspections.indexOf(existingQaInspection.get());
            qaInspections.set(index, updatedQaInspection);
            return true;
        }
        return false;
    }

    /**
     * Deletes a QA inspection from the system.
     *
     * @param qaInspectionId the unique identifier of the QA inspection to delete
     * @return true if the QA inspection was deleted successfully, false otherwise
     */
    public boolean deleteQaInspection(Integer qaInspectionId) {
        return qaInspections.removeIf(qaInspection -> qaInspection.getQaInspectionId().equals(qaInspectionId));
    }
}

