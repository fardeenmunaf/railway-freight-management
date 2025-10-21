package app.repositories;

import app.model.TrainStop;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing train stops in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainStopRepository {

    private final List<TrainStop> trainStops = new ArrayList<>();

    public void addTrainStop(TrainStop trainStop) {
        if (trainStop != null && trainStop.getTrainStopId() != null) {
            if (!containsTrainStop(trainStop.getTrainStopId())) {
                trainStops.add(trainStop);
            }
        }
    }

    public List<TrainStop> getAllTrainStops() {
        return new ArrayList<>(trainStops);
    }

    public TrainStop getTrainStopById(Integer trainStopId) {
        for (TrainStop trainStop : trainStops) {
            if (trainStopId.equals(trainStop.getTrainStopId())) {
                return trainStop;
            }
        }
        return null;
    }

    public boolean updateTrainStop(Integer trainStopId, TrainStop updatedTrainStop) {
        TrainStop existingTrainStop = getTrainStopById(trainStopId);
        if (existingTrainStop != null) {
            int index = trainStops.indexOf(existingTrainStop);
            trainStops.set(index, updatedTrainStop);
            return true;
        }
        return false;
    }

    public boolean deleteTrainStop(Integer trainStopId) {
        return trainStops.removeIf(trainStop -> trainStopId.equals(trainStop.getTrainStopId()));
    }

    public Integer getNextStopId() {
        int maxId = 0;
        for (TrainStop trainStop : trainStops) {
            if (trainStop.getTrainStopId() != null && trainStop.getTrainStopId() > maxId) {
                maxId = trainStop.getTrainStopId();
            }
        }
        return maxId + 1;
    }

    private boolean containsTrainStop(Integer trainStopId) {
        return getTrainStopById(trainStopId) != null;
    }

    public void clear() {
        trainStops.clear();
    }
}
