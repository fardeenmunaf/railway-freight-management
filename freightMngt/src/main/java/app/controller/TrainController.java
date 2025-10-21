package app.controller;

import app.model.Train;
import java.util.ArrayList;
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
    private List<Train> trains;

    /**
     * Default constructor that initializes the train list.
     */
    public TrainController() {
        this.trains = new ArrayList<>();
    }

    /**
     * Creates and adds a new train to the system.
     *
     * @param train the Train object to be created
     * @return the created Train object
     */
    public Train createTrain(Train train) {
        if (train != null) {
            trains.add(train);
        }
        return train;
    }

    /**
     * Retrieves all trains from the system.
     *
     * @return a list of all Train objects
     */
    public List<Train> getAllTrains() {
        return new ArrayList<>(trains);
    }

    /**
     * Retrieves a train by its unique identifier.
     *
     * @param trainId the unique identifier of the train
     * @return an Optional containing the Train if found, or empty if not found
     */
    public Optional<Train> getTrainById(Integer trainId) {
        return trains.stream()
                .filter(train -> train.getTrainId().equals(trainId))
                .findFirst();
    }

    /**
     * Updates an existing train in the system.
     *
     * @param trainId the unique identifier of the train to update
     * @param updatedTrain the Train object with updated information
     * @return true if the train was updated successfully, false otherwise
     */
    public boolean updateTrain(Integer trainId, Train updatedTrain) {
        Optional<Train> existingTrain = getTrainById(trainId);
        if (existingTrain.isPresent()) {
            int index = trains.indexOf(existingTrain.get());
            trains.set(index, updatedTrain);
            return true;
        }
        return false;
    }

    /**
     * Deletes a train from the system.
     *
     * @param trainId the unique identifier of the train to delete
     * @return true if the train was deleted successfully, false otherwise
     */
    public boolean deleteTrain(Integer trainId) {
        return trains.removeIf(train -> train.getTrainId().equals(trainId));
    }
}

