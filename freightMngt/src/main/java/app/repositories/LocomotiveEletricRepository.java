package app.repositories;

import app.model.LocomotiveEletric;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing electric locomotives in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveEletricRepository {

    private final List<LocomotiveEletric> locomotiveEletrics = new ArrayList<>();

    public void addLocomotiveEletric(LocomotiveEletric locomotiveEletric) {
        if (locomotiveEletric != null && locomotiveEletric.getLocomotiveId() != null) {
            if (!containsLocomotiveEletric(locomotiveEletric.getLocomotiveId())) {
                locomotiveEletrics.add(locomotiveEletric);
            }
        }
    }

    public List<LocomotiveEletric> getAllLocomotiveEletrics() {
        return new ArrayList<>(locomotiveEletrics);
    }

    public LocomotiveEletric getLocomotiveEletricById(Integer locomotiveId) {
        for (LocomotiveEletric le : locomotiveEletrics) {
            if (locomotiveId.equals(le.getLocomotiveId())) {
                return le;
            }
        }
        return null;
    }

    public boolean updateLocomotiveEletric(Integer locomotiveId, LocomotiveEletric updatedLE) {
        LocomotiveEletric existingLE = getLocomotiveEletricById(locomotiveId);
        if (existingLE != null) {
            int index = locomotiveEletrics.indexOf(existingLE);
            locomotiveEletrics.set(index, updatedLE);
            return true;
        }
        return false;
    }

    public boolean deleteLocomotiveEletric(Integer locomotiveId) {
        return locomotiveEletrics.removeIf(le -> locomotiveId.equals(le.getLocomotiveId()));
    }

    public Integer getNextLocomotiveEletricId() {
        int maxId = 0;
        for (LocomotiveEletric le : locomotiveEletrics) {
            if (le.getLocomotiveId() != null && le.getLocomotiveId() > maxId) {
                maxId = le.getLocomotiveId();
            }
        }
        return maxId + 1;
    }

    private boolean containsLocomotiveEletric(Integer locomotiveId) {
        return getLocomotiveEletricById(locomotiveId) != null;
    }

    public void clear() {
        locomotiveEletrics.clear();
    }
}

