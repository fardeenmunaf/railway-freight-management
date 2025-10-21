package app.repositories;

import app.model.ParkingQueue;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing parking queues in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class ParkingQueueRepository {

    private final List<ParkingQueue> parkingQueues = new ArrayList<>();

    public void addParkingQueue(ParkingQueue parkingQueue) {
        if (parkingQueue != null && parkingQueue.getParkingQueueId() != null) {
            if (!containsParkingQueue(parkingQueue.getParkingQueueId())) {
                parkingQueues.add(parkingQueue);
            }
        }
    }

    public List<ParkingQueue> getAllParkingQueues() {
        return new ArrayList<>(parkingQueues);
    }

    public ParkingQueue getParkingQueueById(Integer queueId) {
        for (ParkingQueue parkingQueue : parkingQueues) {
            if (queueId.equals(parkingQueue.getParkingQueueId())) {
                return parkingQueue;
            }
        }
        return null;
    }

    public boolean updateParkingQueue(Integer queueId, ParkingQueue updatedParkingQueue) {
        ParkingQueue existingParkingQueue = getParkingQueueById(queueId);
        if (existingParkingQueue != null) {
            int index = parkingQueues.indexOf(existingParkingQueue);
            parkingQueues.set(index, updatedParkingQueue);
            return true;
        }
        return false;
    }

    public boolean deleteParkingQueue(Integer queueId) {
        return parkingQueues.removeIf(parkingQueue -> queueId.equals(parkingQueue.getParkingQueueId()));
    }

    public Integer getNextQueueId() {
        int maxId = 0;
        for (ParkingQueue parkingQueue : parkingQueues) {
            if (parkingQueue.getParkingQueueId() != null && parkingQueue.getParkingQueueId() > maxId) {
                maxId = parkingQueue.getParkingQueueId();
            }
        }
        return maxId + 1;
    }

    private boolean containsParkingQueue(Integer queueId) {
        return getParkingQueueById(queueId) != null;
    }

    public void clear() {
        parkingQueues.clear();
    }
}
