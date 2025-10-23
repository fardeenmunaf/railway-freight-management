package app.controller;

import app.model.ParkingTrack;
import app.repositories.ParkingTrackRepository;
import app.repositories.Repositories;
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
    private final ParkingTrackRepository repository;

    /**
     * Default constructor that initializes the controller with the parking track repository.
     */
    public ParkingTrackController() {
        this.repository = Repositories.getInstance().getParkingTrackRepository();
    }

    /**
     * Creates and adds a new parking track to the system.
     *
     * @param parkingTrack the ParkingTrack object to be created
     * @return the created ParkingTrack object
     */
    public ParkingTrack createParkingTrack(ParkingTrack parkingTrack) {
        if (parkingTrack != null) {
            repository.addParkingTrack(parkingTrack);
        }
        return parkingTrack;
    }

    /**
     * Retrieves all parking tracks from the system.
     *
     * @return a list of all ParkingTrack objects
     */
    public List<ParkingTrack> getAllParkingTracks() {
        return repository.getAllParkingTracks();
    }

    /**
     * Retrieves a parking track by its unique identifier.
     *
     * @param parkingTrackId the unique identifier of the parking track
     * @return an Optional containing the ParkingTrack if found, or empty if not found
     */
    public Optional<ParkingTrack> getParkingTrackById(Integer parkingTrackId) {
        ParkingTrack parkingTrack = repository.getParkingTrackById(parkingTrackId);
        return Optional.ofNullable(parkingTrack);
    }

    /**
     * Updates an existing parking track in the system.
     *
     * @param parkingTrackId the unique identifier of the parking track to update
     * @param updatedParkingTrack the ParkingTrack object with updated information
     * @return true if the parking track was updated successfully, false otherwise
     */
    public boolean updateParkingTrack(Integer parkingTrackId, ParkingTrack updatedParkingTrack) {
        return repository.updateParkingTrack(parkingTrackId, updatedParkingTrack);
    }

    /**
     * Deletes a parking track from the system.
     *
     * @param parkingTrackId the unique identifier of the parking track to delete
     * @return true if the parking track was deleted successfully, false otherwise
     */
    public boolean deleteParkingTrack(Integer parkingTrackId) {
        return repository.deleteParkingTrack(parkingTrackId);
    }

    /**
     * Gets the next available parking track ID.
     *
     * @return the next parking track ID to be used
     */
    public Integer getNextParkingTrackId() {
        return repository.getNextTrackId();
    }

    /**
     * Clears all parking tracks from the system.
     */
    public void clearAllParkingTracks() {
        repository.clear();
    }
}
