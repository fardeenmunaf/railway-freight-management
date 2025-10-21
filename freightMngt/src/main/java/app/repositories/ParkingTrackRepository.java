package app.repositories;

import app.model.ParkingTrack;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing parking tracks in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ParkingTrackRepository {

    private final List<ParkingTrack> parkingTracks = new ArrayList<>();

    public void addParkingTrack(ParkingTrack parkingTrack) {
        if (parkingTrack != null && parkingTrack.getParkingTrackId() != null) {
            if (!containsParkingTrack(parkingTrack.getParkingTrackId())) {
                parkingTracks.add(parkingTrack);
            }
        }
    }

    public List<ParkingTrack> getAllParkingTracks() {
        return new ArrayList<>(parkingTracks);
    }

    public ParkingTrack getParkingTrackById(Integer trackId) {
        for (ParkingTrack parkingTrack : parkingTracks) {
            if (trackId.equals(parkingTrack.getParkingTrackId())) {
                return parkingTrack;
            }
        }
        return null;
    }

    public boolean updateParkingTrack(Integer trackId, ParkingTrack updatedParkingTrack) {
        ParkingTrack existingParkingTrack = getParkingTrackById(trackId);
        if (existingParkingTrack != null) {
            int index = parkingTracks.indexOf(existingParkingTrack);
            parkingTracks.set(index, updatedParkingTrack);
            return true;
        }
        return false;
    }

    public boolean deleteParkingTrack(Integer trackId) {
        return parkingTracks.removeIf(parkingTrack -> trackId.equals(parkingTrack.getParkingTrackId()));
    }

    public Integer getNextTrackId() {
        int maxId = 0;
        for (ParkingTrack parkingTrack : parkingTracks) {
            if (parkingTrack.getParkingTrackId() != null && parkingTrack.getParkingTrackId() > maxId) {
                maxId = parkingTrack.getParkingTrackId();
            }
        }
        return maxId + 1;
    }

    private boolean containsParkingTrack(Integer trackId) {
        return getParkingTrackById(trackId) != null;
    }

    public void clear() {
        parkingTracks.clear();
    }
}
