package app.controller;

import app.model.ParkingQueue;
import app.repositories.ParkingQueueRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing ParkingQueue entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for ParkingQueue objects.
 * This controller manages parking queue data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ParkingQueueController {
    private final ParkingQueueRepository repository;

    /**
     * Default constructor that initializes the controller with the parking queue repository.
     */
    public ParkingQueueController() {
        this.repository = Repositories.getInstance().getParkingQueueRepository();
    }

    /**
     * Creates and adds a new parking queue to the system.
     *
     * @param parkingQueue the ParkingQueue object to be created
     * @return the created ParkingQueue object
     */
    public ParkingQueue createParkingQueue(ParkingQueue parkingQueue) {
        if (parkingQueue != null) {
            repository.addParkingQueue(parkingQueue);
        }
        return parkingQueue;
    }

    /**
     * Retrieves all parking queues from the system.
     *
     * @return a list of all ParkingQueue objects
     */
    public List<ParkingQueue> getAllParkingQueues() {
        return repository.getAllParkingQueues();
    }

    /**
     * Retrieves a parking queue by its unique identifier.
     *
     * @param parkingQueueId the unique identifier of the parking queue
     * @return an Optional containing the ParkingQueue if found, or empty if not found
     */
    public Optional<ParkingQueue> getParkingQueueById(Integer parkingQueueId) {
        ParkingQueue parkingQueue = repository.getParkingQueueById(parkingQueueId);
        return Optional.ofNullable(parkingQueue);
    }

    /**
     * Updates an existing parking queue in the system.
     *
     * @param parkingQueueId the unique identifier of the parking queue to update
     * @param updatedParkingQueue the ParkingQueue object with updated information
     * @return true if the parking queue was updated successfully, false otherwise
     */
    public boolean updateParkingQueue(Integer parkingQueueId, ParkingQueue updatedParkingQueue) {
        return repository.updateParkingQueue(parkingQueueId, updatedParkingQueue);
    }

    /**
     * Deletes a parking queue from the system.
     *
     * @param parkingQueueId the unique identifier of the parking queue to delete
     * @return true if the parking queue was deleted successfully, false otherwise
     */
    public boolean deleteParkingQueue(Integer parkingQueueId) {
        return repository.deleteParkingQueue(parkingQueueId);
    }

    /**
     * Gets the next available parking queue ID.
     *
     * @return the next parking queue ID to be used
     */
    public Integer getNextParkingQueueId() {
        return repository.getNextQueueId();
    }

    /**
     * Clears all parking queues from the system.
     */
    public void clearAllParkingQueues() {
        repository.clear();
    }
}
