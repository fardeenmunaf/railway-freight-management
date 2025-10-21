package app.controller;

import app.model.TrainService;
import java.util.ArrayList;
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
    private List<TrainService> trainServices;

    /**
     * Default constructor that initializes the train service list.
     */
    public TrainServiceController() {
        this.trainServices = new ArrayList<>();
    }

    /**
     * Creates and adds a new train service to the system.
     *
     * @param trainService the TrainService object to be created
     * @return the created TrainService object
     */
    public TrainService createTrainService(TrainService trainService) {
        if (trainService != null) {
            trainServices.add(trainService);
        }
        return trainService;
    }

    /**
     * Retrieves all train services from the system.
     *
     * @return a list of all TrainService objects
     */
    public List<TrainService> getAllTrainServices() {
        return new ArrayList<>(trainServices);
    }

    /**
     * Retrieves a train service by its unique identifier.
     *
     * @param serviceId the unique identifier of the train service
     * @return an Optional containing the TrainService if found, or empty if not found
     */
    public Optional<TrainService> getTrainServiceById(Integer serviceId) {
        return trainServices.stream()
                .filter(trainService -> trainService.getServiceId().equals(serviceId))
                .findFirst();
    }

    /**
     * Updates an existing train service in the system.
     *
     * @param serviceId the unique identifier of the train service to update
     * @param updatedTrainService the TrainService object with updated information
     * @return true if the train service was updated successfully, false otherwise
     */
    public boolean updateTrainService(Integer serviceId, TrainService updatedTrainService) {
        Optional<TrainService> existingTrainService = getTrainServiceById(serviceId);
        if (existingTrainService.isPresent()) {
            int index = trainServices.indexOf(existingTrainService.get());
            trainServices.set(index, updatedTrainService);
            return true;
        }
        return false;
    }

    /**
     * Deletes a train service from the system.
     *
     * @param serviceId the unique identifier of the train service to delete
     * @return true if the train service was deleted successfully, false otherwise
     */
    public boolean deleteTrainService(Integer serviceId) {
        return trainServices.removeIf(trainService -> trainService.getServiceId().equals(serviceId));
    }
}
