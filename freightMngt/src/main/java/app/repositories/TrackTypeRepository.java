package app.repositories;

import app.model.TrackType;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing track types in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrackTypeRepository {

    private final List<TrackType> trackTypes = new ArrayList<>();

    public void addTrackType(TrackType trackType) {
        if (trackType != null && trackType.getTrackTypeId() != null) {
            if (!containsTrackType(trackType.getTrackTypeId())) {
                trackTypes.add(trackType);
            }
        }
    }

    public List<TrackType> getAllTrackTypes() {
        return new ArrayList<>(trackTypes);
    }

    public TrackType getTrackTypeById(Integer trackTypeId) {
        for (TrackType trackType : trackTypes) {
            if (trackTypeId.equals(trackType.getTrackTypeId())) {
                return trackType;
            }
        }
        return null;
    }

    public boolean updateTrackType(Integer trackTypeId, TrackType updatedTrackType) {
        TrackType existingTrackType = getTrackTypeById(trackTypeId);
        if (existingTrackType != null) {
            int index = trackTypes.indexOf(existingTrackType);
            trackTypes.set(index, updatedTrackType);
            return true;
        }
        return false;
    }

    public boolean deleteTrackType(Integer trackTypeId) {
        return trackTypes.removeIf(trackType -> trackTypeId.equals(trackType.getTrackTypeId()));
    }

    public Integer getNextTrackTypeId() {
        int maxId = 0;
        for (TrackType trackType : trackTypes) {
            if (trackType.getTrackTypeId() != null && trackType.getTrackTypeId() > maxId) {
                maxId = trackType.getTrackTypeId();
            }
        }
        return maxId + 1;
    }

    private boolean containsTrackType(Integer trackTypeId) {
        return getTrackTypeById(trackTypeId) != null;
    }

    public void clear() {
        trackTypes.clear();
    }
}

