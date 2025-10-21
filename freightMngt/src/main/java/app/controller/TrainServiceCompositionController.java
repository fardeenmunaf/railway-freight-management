package app.controller;

import app.model.TrainServiceComposition;
import app.repositories.TrainServiceCompositionRepository;
import app.repositories.Repositories;
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
    private final TrainServiceCompositionRepository repository;

    /**
     * Default constructor that initializes the controller with the train service composition repository.
     */
    public TrainServiceCompositionController() {
        this.repository = Repositories.getInstance().getTrainServiceCompositionRepository();
    }

    /**
     * Creates and adds a new train service composition to the system.
     *
     * @param trainServiceComposition the TrainServiceComposition object to be created
     * @return the created TrainServiceComposition object
     */
    public TrainServiceComposition createTrainServiceComposition(TrainServiceComposition trainServiceComposition) {
        if (trainServiceComposition != null) {
            repository.addTrainServiceComposition(trainServiceComposition);
        }
        return trainServiceComposition;
    }

    /**
     * Retrieves all train service compositions from the system.
     *
     * @return a list of all TrainServiceComposition objects
     */
    public List<TrainServiceComposition> getAllTrainServiceCompositions() {
        return repository.getAllTrainServiceCompositions();
    }

    /**
     * Retrieves all train service compositions for a specific service.
     *
     * @param serviceId the service identifier
     * @return a list of all TrainServiceComposition objects for the specified service
     */
    public List<TrainServiceComposition> getTrainServiceCompositionsByService(Integer serviceId) {
        return repository.getTrainServiceCompositionsByServiceId(serviceId);
    }

    /**
     * Deletes a train service composition from the system.
     *
     * @param serviceId the service identifier
     * @param locomotiveId the locomotive identifier (optional, can be null)
     * @param wagonId the wagon identifier (optional, can be null)
     * @return true if the train service composition was deleted successfully, false otherwise
     */
    public boolean deleteTrainServiceComposition(Integer serviceId, Integer locomotiveId, Integer wagonId) {
        return repository.deleteTrainServiceComposition(serviceId, locomotiveId, wagonId);
    }

    /**
     * Clears all train service compositions from the system.
     */
    public void clearAllTrainServiceCompositions() {
        repository.clear();
    }
}
