package app.repositories;

import app.model.WagonType;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing wagon types in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WagonTypeRepository {

    private final List<WagonType> wagonTypes = new ArrayList<>();

    public void addWagonType(WagonType wagonType) {
        if (wagonType != null && wagonType.getWagonTypeId() != null) {
            if (!containsWagonType(wagonType.getWagonTypeId())) {
                wagonTypes.add(wagonType);
            }
        }
    }

    public List<WagonType> getAllWagonTypes() {
        return new ArrayList<>(wagonTypes);
    }

    public WagonType getWagonTypeById(Integer wagonTypeId) {
        for (WagonType wagonType : wagonTypes) {
            if (wagonTypeId.equals(wagonType.getWagonTypeId())) {
                return wagonType;
            }
        }
        return null;
    }

    public boolean updateWagonType(Integer wagonTypeId, WagonType updatedWagonType) {
        WagonType existingWagonType = getWagonTypeById(wagonTypeId);
        if (existingWagonType != null) {
            int index = wagonTypes.indexOf(existingWagonType);
            wagonTypes.set(index, updatedWagonType);
            return true;
        }
        return false;
    }

    public boolean deleteWagonType(Integer wagonTypeId) {
        return wagonTypes.removeIf(wagonType -> wagonTypeId.equals(wagonType.getWagonTypeId()));
    }

    public Integer getNextWagonTypeId() {
        int maxId = 0;
        for (WagonType wagonType : wagonTypes) {
            if (wagonType.getWagonTypeId() != null && wagonType.getWagonTypeId() > maxId) {
                maxId = wagonType.getWagonTypeId();
            }
        }
        return maxId + 1;
    }

    private boolean containsWagonType(Integer wagonTypeId) {
        return getWagonTypeById(wagonTypeId) != null;
    }

    public void clear() {
        wagonTypes.clear();
    }
}

