
package app.controller;

import app.model.TrainService;
import app.repositories.TrainServiceRepository;
import app.repositories.Repositories;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing TrainService entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for TrainService objects.
 * This controller manages train service data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainServiceController {
    private final TrainServiceRepository repository;

    /**
     * Default constructor that initializes the controller with the train service repository.
     */
    public TrainServiceController() {
        this.repository = Repositories.getInstance().getTrainServiceRepository();
    }

    /**
     * Creates and adds a new train service to the system.
     *
     * @param trainService the TrainService object to be created
     * @return the created TrainService object
     */
    public TrainService createTrainService(TrainService trainService) {
        if (trainService != null) {
            repository.addTrainService(trainService);
        }
        return trainService;
    }

    /**
     * Retrieves all train services from the system.
     *
     * @return a list of all TrainService objects
     */
    public List<TrainService> getAllTrainServices() {
        return repository.getAllTrainServices();
    }

    /**
     * Retrieves a train service by its unique identifier.
     *
     * @param trainServiceId the unique identifier of the train service
     * @return an Optional containing the TrainService if found, or empty if not found
     */
    public Optional<TrainService> getTrainServiceById(Integer trainServiceId) {
        TrainService trainService = repository.getTrainServiceById(trainServiceId);
        return Optional.ofNullable(trainService);
    }

    /**
     * Updates an existing train service in the system.
     *
     * @param trainServiceId      the unique identifier of the train service to update
     * @param updatedTrainService the TrainService object with updated information
     * @return true if the train service was updated successfully, false otherwise
     */
    public boolean updateTrainService(Integer trainServiceId, TrainService updatedTrainService) {
        return repository.updateTrainService(trainServiceId, updatedTrainService);
    }

    /**
     * Deletes a train service from the system.
     *
     * @param trainServiceId the unique identifier of the train service to delete
     * @return true if the train service was deleted successfully, false otherwise
     */
    public boolean deleteTrainService(Integer trainServiceId) {
        return repository.deleteTrainService(trainServiceId);
    }

    /**
     * Gets the next available train service ID.
     *
     * @return the next train service ID to be used
     */
    public Integer getNextTrainServiceId() {
        return repository.getNextServiceId();
    }

    /**
     * Clears all train services from the system.
     */
    public void clearAllTrainServices() {
        repository.clear();
    }
}