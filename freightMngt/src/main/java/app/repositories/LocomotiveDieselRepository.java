package app.repositories;

import app.model.LocomotiveDiesel;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing diesel locomotives in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveDieselRepository {

    private final List<LocomotiveDiesel> locomotiveDiesels = new ArrayList<>();

    public void addLocomotiveDiesel(LocomotiveDiesel locomotiveDiesel) {
        if (locomotiveDiesel != null && locomotiveDiesel.getLocomotiveId() != null) {
            if (!containsLocomotiveDiesel(locomotiveDiesel.getLocomotiveId())) {
                locomotiveDiesels.add(locomotiveDiesel);
            }
        }
    }

    public List<LocomotiveDiesel> getAllLocomotiveDiesels() {
        return new ArrayList<>(locomotiveDiesels);
    }

    public LocomotiveDiesel getLocomotiveDieselById(Integer locomotiveId) {
        for (LocomotiveDiesel ld : locomotiveDiesels) {
            if (locomotiveId.equals(ld.getLocomotiveId())) {
                return ld;
            }
        }
        return null;
    }

    public boolean updateLocomotiveDiesel(Integer locomotiveId, LocomotiveDiesel updatedLD) {
        LocomotiveDiesel existingLD = getLocomotiveDieselById(locomotiveId);
        if (existingLD != null) {
            int index = locomotiveDiesels.indexOf(existingLD);
            locomotiveDiesels.set(index, updatedLD);
            return true;
        }
        return false;
    }

    public boolean deleteLocomotiveDiesel(Integer locomotiveId) {
        return locomotiveDiesels.removeIf(ld -> locomotiveId.equals(ld.getLocomotiveId()));
    }

    private boolean containsLocomotiveDiesel(Integer locomotiveId) {
        return getLocomotiveDieselById(locomotiveId) != null;
    }

    public void clear() {
        locomotiveDiesels.clear();
    }
}

