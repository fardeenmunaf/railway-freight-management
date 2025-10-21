package app.controller;

import app.model.PickWave;
import app.repositories.PickWaveRepository;
import app.repositories.Repositories;
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
    private final PickWaveRepository repository;

    /**
     * Default constructor that initializes the controller with the pick wave repository.
     */
    public PickWaveController() {
        this.repository = Repositories.getInstance().getPickWaveRepository();
    }

    /**
     * Creates and adds a new pick wave to the system.
     *
     * @param pickWave the PickWave object to be created
     * @return the created PickWave object
     */
    public PickWave createPickWave(PickWave pickWave) {
        if (pickWave != null) {
            repository.addPickWave(pickWave);
        }
        return pickWave;
    }

    /**
     * Retrieves all pick waves from the system.
     *
     * @return a list of all PickWave objects
     */
    public List<PickWave> getAllPickWaves() {
        return repository.getAllPickWaves();
    }

    /**
     * Retrieves a pick wave by its unique identifier.
     *
     * @param pickWaveId the unique identifier of the pick wave
     * @return an Optional containing the PickWave if found, or empty if not found
     */
    public Optional<PickWave> getPickWaveById(Integer pickWaveId) {
        PickWave pickWave = repository.getPickWaveById(pickWaveId);
        return Optional.ofNullable(pickWave);
    }

    /**
     * Updates an existing pick wave in the system.
     *
     * @param pickWaveId the unique identifier of the pick wave to update
     * @param updatedPickWave the PickWave object with updated information
     * @return true if the pick wave was updated successfully, false otherwise
     */
    public boolean updatePickWave(Integer pickWaveId, PickWave updatedPickWave) {
        return repository.updatePickWave(pickWaveId, updatedPickWave);
    }

    /**
     * Deletes a pick wave from the system.
     *
     * @param pickWaveId the unique identifier of the pick wave to delete
     * @return true if the pick wave was deleted successfully, false otherwise
     */
    public boolean deletePickWave(Integer pickWaveId) {
        return repository.deletePickWave(pickWaveId);
    }

    /**
     * Gets the next available pick wave ID.
     *
     * @return the next pick wave ID to be used
     */
    public Integer getNextPickWaveId() {
        return repository.getNextWaveId();
    }

    /**
     * Clears all pick waves from the system.
     */
    public void clearAllPickWaves() {
        repository.clear();
    }
}
