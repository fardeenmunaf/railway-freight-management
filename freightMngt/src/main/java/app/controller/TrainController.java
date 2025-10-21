package app.controller;

import app.model.Train;
import app.repositories.TrainRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Train entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Train objects.
 * This controller manages train data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainController {
    private final TrainRepository repository;

    /**
     * Default constructor that initializes the controller with the train repository.
     */
    public TrainController() {
        this.repository = Repositories.getInstance().getTrainRepository();
    }

    /**
     * Creates and adds a new train to the system.
     *
     * @param train the Train object to be created
     * @return the created Train object
     */
    public Train createTrain(Train train) {
        if (train != null) {
            repository.addTrain(train);
        }
        return train;
    }

    /**
     * Retrieves all trains from the system.
     *
     * @return a list of all Train objects
     */
    public List<Train> getAllTrains() {
        return repository.getAllTrains();
    }

    /**
     * Retrieves a train by its unique identifier.
     *
     * @param trainId the unique identifier of the train
     * @return an Optional containing the Train if found, or empty if not found
     */
    public Optional<Train> getTrainById(Integer trainId) {
        Train train = repository.getTrainById(trainId);
        return Optional.ofNullable(train);
    }

    /**
     * Updates an existing train in the system.
     *
     * @param trainId the unique identifier of the train to update
     * @param updatedTrain the Train object with updated information
     * @return true if the train was updated successfully, false otherwise
     */
    public boolean updateTrain(Integer trainId, Train updatedTrain) {
        return repository.updateTrain(trainId, updatedTrain);
    }

    /**
     * Deletes a train from the system.
     *
     * @param trainId the unique identifier of the train to delete
     * @return true if the train was deleted successfully, false otherwise
     */
    public boolean deleteTrain(Integer trainId) {
        return repository.deleteTrain(trainId);
    }

    /**
     * Gets the next available train ID.
     *
     * @return the next train ID to be used
     */
    public Integer getNextTrainId() {
        return repository.getNextTrainId();
    }

    /**
     * Clears all trains from the system.
     */
    public void clearAllTrains() {
        repository.clear();
    }
}

