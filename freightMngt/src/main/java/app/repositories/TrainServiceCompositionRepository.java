package app.repositories;

import app.model.TrainServiceComposition;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing train service compositions in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainServiceCompositionRepository {

    private final List<TrainServiceComposition> trainServiceCompositions = new ArrayList<>();

    public void addTrainServiceComposition(TrainServiceComposition composition) {
        if (composition != null) {
            trainServiceCompositions.add(composition);
        }
    }

    public List<TrainServiceComposition> getAllTrainServiceCompositions() {
        return new ArrayList<>(trainServiceCompositions);
    }

    public List<TrainServiceComposition> getTrainServiceCompositionsByServiceId(Integer serviceId) {
        List<TrainServiceComposition> result = new ArrayList<>();
        for (TrainServiceComposition composition : trainServiceCompositions) {
            if (composition.getTrainService() != null && serviceId != null &&
                serviceId.equals(composition.getTrainService().getServiceId())) {
                result.add(composition);
            }
        }
        return result;
    }

    public boolean deleteTrainServiceComposition(Integer serviceId, Integer locomotiveId, Integer wagonId) {
        return trainServiceCompositions.removeIf(composition -> {
            boolean matchesService = composition.getTrainService() != null &&
                                    serviceId.equals(composition.getTrainService().getServiceId());
            boolean matchesLocomotive = locomotiveId == null ||
                                       (composition.getServiceLocomotive() != null &&
                                        composition.getServiceLocomotive().getLocomotive() != null &&
                                        locomotiveId.equals(composition.getServiceLocomotive().getLocomotive().getLocomotiveId()));
            boolean matchesWagon = wagonId == null ||
                                  (composition.getWagon() != null &&
                                   wagonId.equals(composition.getWagon().getWagonId()));
            return matchesService && matchesLocomotive && matchesWagon;
        });
    }

    public void clear() {
        trainServiceCompositions.clear();
    }
}
