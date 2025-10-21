package app.controller;

import app.model.ParkingQueue;
import java.util.ArrayList;
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
    private List<ParkingQueue> parkingQueues;

    /**
     * Default constructor that initializes the parking queue list.
     */
    public ParkingQueueController() {
        this.parkingQueues = new ArrayList<>();
    }

    /**
     * Creates and adds a new parking queue to the system.
     *
     * @param parkingQueue the ParkingQueue object to be created
     * @return the created ParkingQueue object
     */
    public ParkingQueue createParkingQueue(ParkingQueue parkingQueue) {
        if (parkingQueue != null) {
            parkingQueues.add(parkingQueue);
        }
        return parkingQueue;
    }

    /**
     * Retrieves all parking queues from the system.
     *
     * @return a list of all ParkingQueue objects
     */
    public List<ParkingQueue> getAllParkingQueues() {
        return new ArrayList<>(parkingQueues);
    }

    /**
     * Retrieves a parking queue by its unique identifier.
     *
     * @param parkingQueueId the unique identifier of the parking queue
     * @return an Optional containing the ParkingQueue if found, or empty if not found
     */
    public Optional<ParkingQueue> getParkingQueueById(Integer parkingQueueId) {
        return parkingQueues.stream()
                .filter(parkingQueue -> parkingQueue.getParkingQueueId().equals(parkingQueueId))
                .findFirst();
    }

    /**
     * Updates an existing parking queue in the system.
     *
     * @param parkingQueueId the unique identifier of the parking queue to update
     * @param updatedParkingQueue the ParkingQueue object with updated information
     * @return true if the parking queue was updated successfully, false otherwise
     */
    public boolean updateParkingQueue(Integer parkingQueueId, ParkingQueue updatedParkingQueue) {
        Optional<ParkingQueue> existingParkingQueue = getParkingQueueById(parkingQueueId);
        if (existingParkingQueue.isPresent()) {
            int index = parkingQueues.indexOf(existingParkingQueue.get());
            parkingQueues.set(index, updatedParkingQueue);
            return true;
        }
        return false;
    }

    /**
     * Deletes a parking queue from the system.
     *
     * @param parkingQueueId the unique identifier of the parking queue to delete
     * @return true if the parking queue was deleted successfully, false otherwise
     */
    public boolean deleteParkingQueue(Integer parkingQueueId) {
        return parkingQueues.removeIf(parkingQueue -> parkingQueue.getParkingQueueId().equals(parkingQueueId));
    }
}

