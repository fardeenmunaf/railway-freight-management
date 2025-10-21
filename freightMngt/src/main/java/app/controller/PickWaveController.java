package app.controller;

import app.model.PickWave;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing PickWave entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for PickWave objects.
 * This controller manages pick wave data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class PickWaveController {
    private List<PickWave> pickWaves;

    /**
     * Default constructor that initializes the pick wave list.
     */
    public PickWaveController() {
        this.pickWaves = new ArrayList<>();
    }

    /**
     * Creates and adds a new pick wave to the system.
     *
     * @param pickWave the PickWave object to be created
     * @return the created PickWave object
     */
    public PickWave createPickWave(PickWave pickWave) {
        if (pickWave != null) {
            pickWaves.add(pickWave);
        }
        return pickWave;
    }

    /**
     * Retrieves all pick waves from the system.
     *
     * @return a list of all PickWave objects
     */
    public List<PickWave> getAllPickWaves() {
        return new ArrayList<>(pickWaves);
    }

    /**
     * Retrieves a pick wave by its unique identifier.
     *
     * @param pickWaveId the unique identifier of the pick wave
     * @return an Optional containing the PickWave if found, or empty if not found
     */
    public Optional<PickWave> getPickWaveById(Integer pickWaveId) {
        return pickWaves.stream()
                .filter(pickWave -> pickWave.getPickWaveId().equals(pickWaveId))
                .findFirst();
    }

    /**
     * Updates an existing pick wave in the system.
     *
     * @param pickWaveId the unique identifier of the pick wave to update
     * @param updatedPickWave the PickWave object with updated information
     * @return true if the pick wave was updated successfully, false otherwise
     */
    public boolean updatePickWave(Integer pickWaveId, PickWave updatedPickWave) {
        Optional<PickWave> existingPickWave = getPickWaveById(pickWaveId);
        if (existingPickWave.isPresent()) {
            int index = pickWaves.indexOf(existingPickWave.get());
            pickWaves.set(index, updatedPickWave);
            return true;
        }
        return false;
    }

    /**
     * Deletes a pick wave from the system.
     *
     * @param pickWaveId the unique identifier of the pick wave to delete
     * @return true if the pick wave was deleted successfully, false otherwise
     */
    public boolean deletePickWave(Integer pickWaveId) {
        return pickWaves.removeIf(pickWave -> pickWave.getPickWaveId().equals(pickWaveId));
    }
}

