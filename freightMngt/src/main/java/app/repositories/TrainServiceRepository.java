package app.repositories;

import app.model.TrainService;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing train services in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainServiceRepository {

    private final List<TrainService> trainServices = new ArrayList<>();

    public void addTrainService(TrainService trainService) {
        if (trainService != null && trainService.getServiceId() != null) {
            if (!containsTrainService(trainService.getServiceId())) {
                trainServices.add(trainService);
            }
        }
    }

    public List<TrainService> getAllTrainServices() {
        return new ArrayList<>(trainServices);
    }

    public TrainService getTrainServiceById(Integer serviceId) {
        for (TrainService trainService : trainServices) {
            if (serviceId.equals(trainService.getServiceId())) {
                return trainService;
            }
        }
        return null;
    }

    public boolean updateTrainService(Integer serviceId, TrainService updatedTrainService) {
        TrainService existingTrainService = getTrainServiceById(serviceId);
        if (existingTrainService != null) {
            int index = trainServices.indexOf(existingTrainService);
            trainServices.set(index, updatedTrainService);
            return true;
        }
        return false;
    }

    public boolean deleteTrainService(Integer serviceId) {
        return trainServices.removeIf(trainService -> serviceId.equals(trainService.getServiceId()));
    }

    public Integer getNextServiceId() {
        int maxId = 0;
        for (TrainService trainService : trainServices) {
            if (trainService.getServiceId() != null && trainService.getServiceId() > maxId) {
                maxId = trainService.getServiceId();
            }
        }
        return maxId + 1;
    }

    private boolean containsTrainService(Integer serviceId) {
        return getTrainServiceById(serviceId) != null;
    }

    public void clear() {
        trainServices.clear();
    }
}

