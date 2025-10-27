package app.repositories;

import app.model.Locomotive;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing locomotives in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveRepository {

    private final List<Locomotive> locomotives = new ArrayList<>();

    public void addLocomotive(Locomotive locomotive) {
        if (locomotive != null && locomotive.getLocomotiveId() != null) {
            if (!containsLocomotive(locomotive.getLocomotiveId())) {
                locomotives.add(locomotive);
            }
        }
    }

    public List<Locomotive> getAllLocomotives() {
        return new ArrayList<>(locomotives);
    }

    public Locomotive getLocomotiveById(Integer locomotiveId) {
        for (Locomotive locomotive : locomotives) {
            if (locomotiveId.equals(locomotive.getLocomotiveId())) {
                return locomotive;
            }
        }
        return null;
    }

    public boolean updateLocomotive(Integer locomotiveId, Locomotive updatedLocomotive) {
        Locomotive existingLocomotive = getLocomotiveById(locomotiveId);
        if (existingLocomotive != null) {
            int index = locomotives.indexOf(existingLocomotive);
            locomotives.set(index, updatedLocomotive);
            return true;
        }
        return false;
    }

    public boolean deleteLocomotive(Integer locomotiveId) {
        return locomotives.removeIf(locomotive -> locomotiveId.equals(locomotive.getLocomotiveId()));
    }

    public Integer getNextLocomotiveId() {
        int maxId = 0;
        for (Locomotive locomotive : locomotives) {
            if (locomotive.getLocomotiveId() != null && locomotive.getLocomotiveId() > maxId) {
                maxId = locomotive.getLocomotiveId();
            }
        }
        return maxId + 1;
    }

    private boolean containsLocomotive(Integer locomotiveId) {
        return getLocomotiveById(locomotiveId) != null;
    }

    public void clear() {
        locomotives.clear();
    }
}

