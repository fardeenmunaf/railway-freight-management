package app.repositories;

import app.model.LocomotiveType;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing locomotive types in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveTypeRepository {

    private final List<LocomotiveType> locomotiveTypes = new ArrayList<>();

    public void addLocomotiveType(LocomotiveType locomotiveType) {
        if (locomotiveType != null && locomotiveType.getLocomotiveTypeId() != null) {
            if (!containsLocomotiveType(locomotiveType.getLocomotiveTypeId())) {
                locomotiveTypes.add(locomotiveType);
            }
        }
    }

    public List<LocomotiveType> getAllLocomotiveTypes() {
        return new ArrayList<>(locomotiveTypes);
    }

    public LocomotiveType getLocomotiveTypeById(Integer locomotiveTypeId) {
        for (LocomotiveType locomotiveType : locomotiveTypes) {
            if (locomotiveTypeId.equals(locomotiveType.getLocomotiveTypeId())) {
                return locomotiveType;
            }
        }
        return null;
    }

    public boolean updateLocomotiveType(Integer locomotiveTypeId, LocomotiveType updatedLocomotiveType) {
        LocomotiveType existingLocomotiveType = getLocomotiveTypeById(locomotiveTypeId);
        if (existingLocomotiveType != null) {
            int index = locomotiveTypes.indexOf(existingLocomotiveType);
            locomotiveTypes.set(index, updatedLocomotiveType);
            return true;
        }
        return false;
    }

    public boolean deleteLocomotiveType(Integer locomotiveTypeId) {
        return locomotiveTypes.removeIf(locomotiveType -> locomotiveTypeId.equals(locomotiveType.getLocomotiveTypeId()));
    }

    public Integer getNextTypeId() {
        int maxId = 0;
        for (LocomotiveType locomotiveType : locomotiveTypes) {
            if (locomotiveType.getLocomotiveTypeId() != null && locomotiveType.getLocomotiveTypeId() > maxId) {
                maxId = locomotiveType.getLocomotiveTypeId();
            }
        }
        return maxId + 1;
    }

    private boolean containsLocomotiveType(Integer locomotiveTypeId) {
        return getLocomotiveTypeById(locomotiveTypeId) != null;
    }

    public void clear() {
        locomotiveTypes.clear();
    }
}
