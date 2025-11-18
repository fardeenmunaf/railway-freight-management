package app.repositories;

import app.model.FacilityType;

import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing facility types in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FacilityTypeRepository {

    private final List<FacilityType> facilityTypes = new ArrayList<>();

    public void addFacilityType(FacilityType facilityType) {
        if (facilityType != null && facilityType.getFacilityTypeId() != null) {
            if (!containsFacilityType(facilityType.getFacilityTypeId())) {
                facilityTypes.add(facilityType);
            }
        }
    }

    public List<FacilityType> getAllFacilityTypes() {
        return new ArrayList<>(facilityTypes);
    }

    public FacilityType getFacilityTypeById(Integer facilityTypeId) {
        for (FacilityType type : facilityTypes) {
            if (facilityTypeId.equals(type.getFacilityTypeId())) {
                return type;
            }
        }
        return null;
    }

    public FacilityType getFacilityTypeByName(String name) {
        if (name == null) return null;
        for (FacilityType ft : facilityTypes) {
            if (ft.getName() != null && ft.getName().equalsIgnoreCase(name.trim())) {
                return ft;
            }
        }
        return null;
    }

    public boolean updateFacilityType(Integer facilityTypeId, FacilityType updatedType) {
        FacilityType existingType = getFacilityTypeById(facilityTypeId);
        if (existingType != null) {
            int index = facilityTypes.indexOf(existingType);
            facilityTypes.set(index, updatedType);
            return true;
        }
        return false;
    }

    public boolean deleteFacilityType(Integer facilityTypeId) {
        return facilityTypes.removeIf(type -> facilityTypeId.equals(type.getFacilityTypeId()));
    }

    public Integer getNextFacilityTypeId() {
        int maxId = 0;
        for (FacilityType type : facilityTypes) {
            if (type.getFacilityTypeId() != null && type.getFacilityTypeId() > maxId) {
                maxId = type.getFacilityTypeId();
            }
        }
        return maxId + 1;
    }

    private boolean containsFacilityType(Integer facilityTypeId) {
        return getFacilityTypeById(facilityTypeId) != null;
    }

    public void clear() {
        facilityTypes.clear();
    }
}

