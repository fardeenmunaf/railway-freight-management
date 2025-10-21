package app.repositories;

import app.model.QaInspection;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing QA inspections in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class QaInspectionRepository {

    private final List<QaInspection> qaInspections = new ArrayList<>();

    public void addQaInspection(QaInspection qaInspection) {
        if (qaInspection != null && qaInspection.getQaId() != null) {
            if (!containsQaInspection(qaInspection.getQaId())) {
                qaInspections.add(qaInspection);
            }
        }
    }

    public List<QaInspection> getAllQaInspections() {
        return new ArrayList<>(qaInspections);
    }

    public QaInspection getQaInspectionById(Integer inspectionId) {
        for (QaInspection inspection : qaInspections) {
            if (inspectionId.equals(inspection.getQaId())) {
                return inspection;
            }
        }
        return null;
    }

    public boolean updateQaInspection(Integer inspectionId, QaInspection updatedInspection) {
        QaInspection existingInspection = getQaInspectionById(inspectionId);
        if (existingInspection != null) {
            int index = qaInspections.indexOf(existingInspection);
            qaInspections.set(index, updatedInspection);
            return true;
        }
        return false;
    }

    public boolean deleteQaInspection(Integer inspectionId) {
        return qaInspections.removeIf(inspection -> inspectionId.equals(inspection.getQaId()));
    }

    public Integer getNextInspectionId() {
        int maxId = 0;
        for (QaInspection inspection : qaInspections) {
            if (inspection.getQaId() != null && inspection.getQaId() > maxId) {
                maxId = inspection.getQaId();
            }
        }
        return maxId + 1;
    }

    private boolean containsQaInspection(Integer inspectionId) {
        return getQaInspectionById(inspectionId) != null;
    }

    public void clear() {
        qaInspections.clear();
    }
}
