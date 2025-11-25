package app.repositories;

import app.model.TrainDispatch;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository for managing TrainDispatch entities in memory.
 * Provides CRUD operations and query methods for train dispatches.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainDispatchRepository {

    private final Map<Integer, TrainDispatch> dispatches;
    private Integer nextId;

    /**
     * Constructor that initializes the repository with an empty map.
     */
    public TrainDispatchRepository() {
        this.dispatches = new HashMap<>();
        this.nextId = 1;
    }

    /**
     * Adds a new train dispatch to the repository.
     *
     * @param dispatch the train dispatch to add
     * @return the added train dispatch
     */
    public TrainDispatch addTrainDispatch(TrainDispatch dispatch) {
        if (dispatch.getDispatchId() == null) {
            dispatch.setDispatchId(nextId++);
        } else {
            if (dispatch.getDispatchId() >= nextId) {
                nextId = dispatch.getDispatchId() + 1;
            }
        }
        dispatches.put(dispatch.getDispatchId(), dispatch);
        return dispatch;
    }

    /**
     * Gets a train dispatch by its ID.
     *
     * @param dispatchId the ID of the dispatch to retrieve
     * @return the train dispatch, or null if not found
     */
    public TrainDispatch getTrainDispatchById(Integer dispatchId) {
        return dispatches.get(dispatchId);
    }

    /**
     * Gets all train dispatches.
     *
     * @return a list of all train dispatches
     */
    public List<TrainDispatch> getAllTrainDispatches() {
        return new ArrayList<>(dispatches.values());
    }

    /**
     * Updates an existing train dispatch.
     *
     * @param dispatchId the ID of the dispatch to update
     * @param updatedDispatch the updated dispatch information
     * @return the updated dispatch, or null if not found
     */
    public TrainDispatch updateTrainDispatch(Integer dispatchId, TrainDispatch updatedDispatch) {
        if (dispatches.containsKey(dispatchId)) {
            updatedDispatch.setDispatchId(dispatchId);
            dispatches.put(dispatchId, updatedDispatch);
            return updatedDispatch;
        }
        return null;
    }

    /**
     * Deletes a train dispatch by its ID.
     *
     * @param dispatchId the ID of the dispatch to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteTrainDispatch(Integer dispatchId) {
        return dispatches.remove(dispatchId) != null;
    }

    /**
     * Gets all dispatches for a specific train service.
     *
     * @param trainServiceId the ID of the train service
     * @return a list of dispatches for the train service
     */
    public List<TrainDispatch> getDispatchesByTrainService(Integer trainServiceId) {
        return dispatches.values().stream()
                .filter(d -> d.getTrainService() != null &&
                        d.getTrainService().getServiceId().equals(trainServiceId))
                .collect(Collectors.toList());
    }

    /**
     * Gets all dispatches with a specific status.
     *
     * @param status the status to filter by
     * @return a list of dispatches with the specified status
     */
    public List<TrainDispatch> getDispatchesByStatus(String status) {
        return dispatches.values().stream()
                .filter(d -> status.equals(d.getStatus()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all dispatches planned to depart within a time range.
     *
     * @param startTime the start of the time range
     * @param endTime the end of the time range
     * @return a list of dispatches within the time range
     */
    public List<TrainDispatch> getDispatchesByDepartureTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return dispatches.values().stream()
                .filter(d -> d.getPlannedDepartureTime() != null &&
                        !d.getPlannedDepartureTime().isBefore(startTime) &&
                        !d.getPlannedDepartureTime().isAfter(endTime))
                .collect(Collectors.toList());
    }

    /**
     * Gets all dispatches with manual routes.
     *
     * @return a list of dispatches with manual routing
     */
    public List<TrainDispatch> getManualDispatches() {
        return dispatches.values().stream()
                .filter(d -> "MANUAL".equals(d.getRouteType()))
                .collect(Collectors.toList());
    }

    /**
     * Gets all dispatches with automatic routes.
     *
     * @return a list of dispatches with automatic routing
     */
    public List<TrainDispatch> getAutomaticDispatches() {
        return dispatches.values().stream()
                .filter(d -> "AUTOMATIC".equals(d.getRouteType()))
                .collect(Collectors.toList());
    }

    /**
     * Clears all dispatches from the repository.
     */
    public void clearAll() {
        dispatches.clear();
        nextId = 1;
    }

    /**
     * Gets the count of all dispatches.
     *
     * @return the total number of dispatches
     */
    public int getCount() {
        return dispatches.size();
    }
}

