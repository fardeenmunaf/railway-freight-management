package app.repositories;

import app.model.WagonModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing wagon models in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WagonModelRepository {

    private final List<WagonModel> wagonModels = new ArrayList<>();

    public void addWagonModel(WagonModel wagonModel) {
        if (wagonModel != null && wagonModel.getWagonModelId() != null) {
            if (!containsWagonModel(wagonModel.getWagonModelId())) {
                wagonModels.add(wagonModel);
            }
        }
    }

    public List<WagonModel> getAllWagonModels() {
        return new ArrayList<>(wagonModels);
    }

    public WagonModel getWagonModelById(Integer wagonModelId) {
        for (WagonModel wagonModel : wagonModels) {
            if (wagonModelId.equals(wagonModel.getWagonModelId())) {
                return wagonModel;
            }
        }
        return null;
    }

    public WagonModel getWagonModelByName(String name) {
        for (WagonModel wagonModel : wagonModels) {
            if (wagonModel.getName() != null && wagonModel.getName().equals(name)) {
                return wagonModel;
            }
        }
        return null;
    }

    public boolean updateWagonModel(Integer wagonModelId, WagonModel updatedWagonModel) {
        WagonModel existingWagonModel = getWagonModelById(wagonModelId);
        if (existingWagonModel != null) {
            int index = wagonModels.indexOf(existingWagonModel);
            wagonModels.set(index, updatedWagonModel);
            return true;
        }
        return false;
    }

    public boolean deleteWagonModel(Integer wagonModelId) {
        return wagonModels.removeIf(wagonModel -> wagonModelId.equals(wagonModel.getWagonModelId()));
    }

    public Integer getNextWagonModelId() {
        int maxId = 0;
        for (WagonModel wagonModel : wagonModels) {
            if (wagonModel.getWagonModelId() != null && wagonModel.getWagonModelId() > maxId) {
                maxId = wagonModel.getWagonModelId();
            }
        }
        return maxId + 1;
    }

    private boolean containsWagonModel(Integer wagonModelId) {
        return getWagonModelById(wagonModelId) != null;
    }

    public void clear() {
        wagonModels.clear();
    }
}
