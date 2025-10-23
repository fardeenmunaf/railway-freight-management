package app.repositories;

import app.model.FacilityFeatureType;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing facility feature types in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class FacilityFeatureTypeRepository {

    private final List<FacilityFeatureType> facilityFeatureTypes = new ArrayList<>();

    public void addFacilityFeatureType(FacilityFeatureType facilityFeatureType) {
        if (facilityFeatureType != null && facilityFeatureType.getFeatureId() != null) {
            if (!containsFacilityFeatureType(facilityFeatureType.getFeatureId())) {
                facilityFeatureTypes.add(facilityFeatureType);
            }
        }
    }

    public List<FacilityFeatureType> getAllFacilityFeatureTypes() {
        return new ArrayList<>(facilityFeatureTypes);
    }

    public FacilityFeatureType getFacilityFeatureTypeById(Integer featureId) {
        for (FacilityFeatureType type : facilityFeatureTypes) {
            if (featureId.equals(type.getFeatureId())) {
                return type;
            }
        }
        return null;
    }

    public boolean updateFacilityFeatureType(Integer featureId, FacilityFeatureType updatedType) {
        FacilityFeatureType existingType = getFacilityFeatureTypeById(featureId);
        if (existingType != null) {
            int index = facilityFeatureTypes.indexOf(existingType);
            facilityFeatureTypes.set(index, updatedType);
            return true;
        }
        return false;
    }

    public boolean deleteFacilityFeatureType(Integer featureId) {
        return facilityFeatureTypes.removeIf(type -> featureId.equals(type.getFeatureId()));
    }

    public Integer getNextFeatureTypeId() {
        int maxId = 0;
        for (FacilityFeatureType type : facilityFeatureTypes) {
            if (type.getFeatureId() != null && type.getFeatureId() > maxId) {
                maxId = type.getFeatureId();
            }
        }
        return maxId + 1;
    }

    private boolean containsFacilityFeatureType(Integer featureId) {
        return getFacilityFeatureTypeById(featureId) != null;
    }

    public void clear() {
        facilityFeatureTypes.clear();
    }
}
