package app.repositories;

import app.model.PickWave;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing pick waves in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class PickWaveRepository {

    private final List<PickWave> pickWaves = new ArrayList<>();

    public void addPickWave(PickWave pickWave) {
        if (pickWave != null && pickWave.getPickWaveId() != null) {
            if (!containsPickWave(pickWave.getPickWaveId())) {
                pickWaves.add(pickWave);
            }
        }
    }

    public List<PickWave> getAllPickWaves() {
        return new ArrayList<>(pickWaves);
    }

    public PickWave getPickWaveById(Integer pickWaveId) {
        for (PickWave pickWave : pickWaves) {
            if (pickWaveId.equals(pickWave.getPickWaveId())) {
                return pickWave;
            }
        }
        return null;
    }

    public boolean updatePickWave(Integer pickWaveId, PickWave updatedPickWave) {
        PickWave existingPickWave = getPickWaveById(pickWaveId);
        if (existingPickWave != null) {
            int index = pickWaves.indexOf(existingPickWave);
            pickWaves.set(index, updatedPickWave);
            return true;
        }
        return false;
    }

    public boolean deletePickWave(Integer pickWaveId) {
        return pickWaves.removeIf(pickWave -> pickWaveId.equals(pickWave.getPickWaveId()));
    }

    public Integer getNextWaveId() {
        int maxId = 0;
        for (PickWave pickWave : pickWaves) {
            if (pickWave.getPickWaveId() != null && pickWave.getPickWaveId() > maxId) {
                maxId = pickWave.getPickWaveId();
            }
        }
        return maxId + 1;
    }

    private boolean containsPickWave(Integer pickWaveId) {
        return getPickWaveById(pickWaveId) != null;
    }

    public void clear() {
        pickWaves.clear();
    }
}
