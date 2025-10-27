package app.controller;

import app.model.TrackType;
import app.repositories.TrackTypeRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing TrackType entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for TrackType objects.
 * This controller manages track type data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrackTypeController {
    private final TrackTypeRepository repository;

    /**
     * Default constructor that initializes the controller with the track type repository.
     */
    public TrackTypeController() {
        this.repository = Repositories.getInstance().getTrackTypeRepository();
    }

    /**
     * Creates and adds a new track type to the system.
     *
     * @param trackType the TrackType object to be created
     * @return the created TrackType object
     */
    public TrackType createTrackType(TrackType trackType) {
        if (trackType != null) {
            repository.addTrackType(trackType);
        }
        return trackType;
    }

    /**
     * Retrieves all track types from the system.
     *
     * @return a list of all TrackType objects
     */
    public List<TrackType> getAllTrackTypes() {
        return repository.getAllTrackTypes();
    }

    /**
     * Retrieves a track type by its unique identifier.
     *
     * @param trackTypeId the unique identifier of the track type
     * @return an Optional containing the TrackType if found, or empty if not found
     */
    public Optional<TrackType> getTrackTypeById(Integer trackTypeId) {
        TrackType trackType = repository.getTrackTypeById(trackTypeId);
        return Optional.ofNullable(trackType);
    }

    /**
     * Updates an existing track type in the system.
     *
     * @param trackTypeId the unique identifier of the track type to update
     * @param updatedTrackType the TrackType object with updated information
     * @return true if the track type was updated successfully, false otherwise
     */
    public boolean updateTrackType(Integer trackTypeId, TrackType updatedTrackType) {
        return repository.updateTrackType(trackTypeId, updatedTrackType);
    }

    /**
     * Deletes a track type from the system.
     *
     * @param trackTypeId the unique identifier of the track type to delete
     * @return true if the track type was deleted successfully, false otherwise
     */
    public boolean deleteTrackType(Integer trackTypeId) {
        return repository.deleteTrackType(trackTypeId);
    }

    /**
     * Gets the next available track type ID.
     *
     * @return the next track type ID to be used
     */
    public Integer getNextTrackTypeId() {
        return repository.getNextTrackTypeId();
    }

    /**
     * Clears all track types from the system.
     */
    public void clearAllTrackTypes() {
        repository.clear();
    }
}

