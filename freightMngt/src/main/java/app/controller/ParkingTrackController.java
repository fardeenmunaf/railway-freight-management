package app.controller;

import app.model.ParkingTrack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing ParkingTrack entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for ParkingTrack objects.
 * This controller manages parking track data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ParkingTrackController {
    private List<ParkingTrack> parkingTracks;

    /**
     * Default constructor that initializes the parking track list.
     */
    public ParkingTrackController() {
        this.parkingTracks = new ArrayList<>();
    }

    /**
     * Creates and adds a new parking track to the system.
     *
     * @param parkingTrack the ParkingTrack object to be created
     * @return the created ParkingTrack object
     */
    public ParkingTrack createParkingTrack(ParkingTrack parkingTrack) {
        if (parkingTrack != null) {
            parkingTracks.add(parkingTrack);
        }
        return parkingTrack;
    }

    /**
     * Retrieves all parking tracks from the system.
     *
     * @return a list of all ParkingTrack objects
     */
    public List<ParkingTrack> getAllParkingTracks() {
        return new ArrayList<>(parkingTracks);
    }

    /**
     * Retrieves a parking track by its unique identifier.
     *
     * @param parkingTrackId the unique identifier of the parking track
     * @return an Optional containing the ParkingTrack if found, or empty if not found
     */
    public Optional<ParkingTrack> getParkingTrackById(Integer parkingTrackId) {
        return parkingTracks.stream()
                .filter(parkingTrack -> parkingTrack.getParkingTrackId().equals(parkingTrackId))
                .findFirst();
    }

    /**
     * Updates an existing parking track in the system.
     *
     * @param parkingTrackId the unique identifier of the parking track to update
     * @param updatedParkingTrack the ParkingTrack object with updated information
     * @return true if the parking track was updated successfully, false otherwise
     */
    public boolean updateParkingTrack(Integer parkingTrackId, ParkingTrack updatedParkingTrack) {
        Optional<ParkingTrack> existingParkingTrack = getParkingTrackById(parkingTrackId);
        if (existingParkingTrack.isPresent()) {
            int index = parkingTracks.indexOf(existingParkingTrack.get());
            parkingTracks.set(index, updatedParkingTrack);
            return true;
        }
        return false;
    }

    /**
     * Deletes a parking track from the system.
     *
     * @param parkingTrackId the unique identifier of the parking track to delete
     * @return true if the parking track was deleted successfully, false otherwise
     */
    public boolean deleteParkingTrack(Integer parkingTrackId) {
        return parkingTracks.removeIf(parkingTrack -> parkingTrack.getParkingTrackId().equals(parkingTrackId));
    }
}

