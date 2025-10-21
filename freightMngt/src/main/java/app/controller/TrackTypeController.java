
    /**
     * Creates and adds a new track type to the system.
     *
     * @param trackType the TrackType object to be created
     * @return the created TrackType object
     */
    public TrackType createTrackType(TrackType trackType) {
        if (trackType != null) {
            trackTypes.add(trackType);
        }
        return trackType;
    }

    /**
     * Retrieves all track types from the system.
     *
     * @return a list of all TrackType objects
     */
    public List<TrackType> getAllTrackTypes() {
        return new ArrayList<>(trackTypes);
    }

    /**
     * Retrieves a track type by its unique identifier.
     *
     * @param trackTypeId the unique identifier of the track type
     * @return an Optional containing the TrackType if found, or empty if not found
     */
    public Optional<TrackType> getTrackTypeById(Integer trackTypeId) {
        return trackTypes.stream()
                .filter(trackType -> trackType.getTrackTypeId().equals(trackTypeId))
                .findFirst();
    }

    /**
     * Updates an existing track type in the system.
     *
     * @param trackTypeId the unique identifier of the track type to update
     * @param updatedTrackType the TrackType object with updated information
     * @return true if the track type was updated successfully, false otherwise
     */
    public boolean updateTrackType(Integer trackTypeId, TrackType updatedTrackType) {
        Optional<TrackType> existingTrackType = getTrackTypeById(trackTypeId);
        if (existingTrackType.isPresent()) {
            int index = trackTypes.indexOf(existingTrackType.get());
            trackTypes.set(index, updatedTrackType);
            return true;
        }
        return false;
    }

    /**
     * Deletes a track type from the system.
     *
     * @param trackTypeId the unique identifier of the track type to delete
     * @return true if the track type was deleted successfully, false otherwise
     */
    public boolean deleteTrackType(Integer trackTypeId) {
        return trackTypes.removeIf(trackType -> trackType.getTrackTypeId().equals(trackTypeId));
    }
}
package app.controller;

import app.model.TrackType;
import java.util.ArrayList;
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
    private List<TrackType> trackTypes;

    /**
     * Default constructor that initializes the track type list.
     */
    public TrackTypeController() {
        this.trackTypes = new ArrayList<>();
    }

