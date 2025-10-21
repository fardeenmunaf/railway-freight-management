package app.controller;

import app.model.TrainStop;
import app.repositories.TrainStopRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing TrainStop entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for TrainStop objects.
 * This controller manages train stop data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainStopController {
    private final TrainStopRepository repository;

    /**
     * Default constructor that initializes the controller with the train stop repository.
     */
    public TrainStopController() {
        this.repository = Repositories.getInstance().getTrainStopRepository();
    }

    /**
     * Creates and adds a new train stop to the system.
     *
     * @param trainStop the TrainStop object to be created
     * @return the created TrainStop object
     */
    public TrainStop createTrainStop(TrainStop trainStop) {
        if (trainStop != null) {
            repository.addTrainStop(trainStop);
        }
        return trainStop;
    }

    /**
     * Retrieves all train stops from the system.
     *
     * @return a list of all TrainStop objects
     */
    public List<TrainStop> getAllTrainStops() {
        return repository.getAllTrainStops();
    }

    /**
     * Retrieves a train stop by its unique identifier.
     *
     * @param trainStopId the unique identifier of the train stop
     * @return an Optional containing the TrainStop if found, or empty if not found
     */
    public Optional<TrainStop> getTrainStopById(Integer trainStopId) {
        TrainStop trainStop = repository.getTrainStopById(trainStopId);
        return Optional.ofNullable(trainStop);
    }

    /**
     * Updates an existing train stop in the system.
     *
     * @param trainStopId the unique identifier of the train stop to update
     * @param updatedTrainStop the TrainStop object with updated information
     * @return true if the train stop was updated successfully, false otherwise
     */
    public boolean updateTrainStop(Integer trainStopId, TrainStop updatedTrainStop) {
        return repository.updateTrainStop(trainStopId, updatedTrainStop);
    }

    /**
     * Deletes a train stop from the system.
     *
     * @param trainStopId the unique identifier of the train stop to delete
     * @return true if the train stop was deleted successfully, false otherwise
     */
    public boolean deleteTrainStop(Integer trainStopId) {
        return repository.deleteTrainStop(trainStopId);
    }

    /**
     * Gets the next available train stop ID.
     *
     * @return the next train stop ID to be used
     */
    public Integer getNextTrainStopId() {
        return repository.getNextStopId();
    }

    /**
     * Clears all train stops from the system.
     */
    public void clearAllTrainStops() {
        repository.clear();
    }
}

