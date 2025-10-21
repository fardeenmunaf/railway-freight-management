package app.controller;

import app.model.TrainServiceComposition;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing TrainServiceComposition entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for TrainServiceComposition objects.
 * This controller manages train service composition data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainServiceCompositionController {
    private List<TrainServiceComposition> trainServiceCompositions;

    /**
     * Default constructor that initializes the train service composition list.
     */
    public TrainServiceCompositionController() {
        this.trainServiceCompositions = new ArrayList<>();
    }

    /**
     * Creates and adds a new train service composition to the system.
     *
     * @param trainServiceComposition the TrainServiceComposition object to be created
     * @return the created TrainServiceComposition object
     */
    public TrainServiceComposition createTrainServiceComposition(TrainServiceComposition trainServiceComposition) {
        if (trainServiceComposition != null) {
            trainServiceCompositions.add(trainServiceComposition);
        }
        return trainServiceComposition;
    }

    /**
     * Retrieves all train service compositions from the system.
     *
     * @return a list of all TrainServiceComposition objects
     */
    public List<TrainServiceComposition> getAllTrainServiceCompositions() {
        return new ArrayList<>(trainServiceCompositions);
    }

    /**
     * Retrieves a train service composition by its unique identifier.
     *
     * @param trainServiceCompositionId the unique identifier of the train service composition
     * @return an Optional containing the TrainServiceComposition if found, or empty if not found
     */
    public Optional<TrainServiceComposition> getTrainServiceCompositionById(Integer trainServiceCompositionId) {
        return trainServiceCompositions.stream()
                .filter(trainServiceComposition -> trainServiceComposition.getTrainServiceCompositionId().equals(trainServiceCompositionId))
                .findFirst();
    }

    /**
     * Updates an existing train service composition in the system.
     *
     * @param trainServiceCompositionId the unique identifier of the train service composition to update
     * @param updatedTrainServiceComposition the TrainServiceComposition object with updated information
     * @return true if the train service composition was updated successfully, false otherwise
     */
    public boolean updateTrainServiceComposition(Integer trainServiceCompositionId, TrainServiceComposition updatedTrainServiceComposition) {
        Optional<TrainServiceComposition> existingTrainServiceComposition = getTrainServiceCompositionById(trainServiceCompositionId);
        if (existingTrainServiceComposition.isPresent()) {
            int index = trainServiceCompositions.indexOf(existingTrainServiceComposition.get());
            trainServiceCompositions.set(index, updatedTrainServiceComposition);
            return true;
        }
        return false;
    }

    /**
     * Deletes a train service composition from the system.
     *
     * @param trainServiceCompositionId the unique identifier of the train service composition to delete
     * @return true if the train service composition was deleted successfully, false otherwise
     */
    public boolean deleteTrainServiceComposition(Integer trainServiceCompositionId) {
        return trainServiceCompositions.removeIf(trainServiceComposition -> trainServiceComposition.getTrainServiceCompositionId().equals(trainServiceCompositionId));
    }
}

