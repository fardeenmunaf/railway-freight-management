package app.repositories;

import app.model.Train;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing trains in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class TrainRepository {

    private final List<Train> trains = new ArrayList<>();

    public void addTrain(Train train) {
        if (train != null && train.getTrainId() != null) {
            if (!containsTrain(train.getTrainId())) {
                trains.add(train);
            }
        }
    }

    public List<Train> getAllTrains() {
        return new ArrayList<>(trains);
    }

    public Train getTrainById(Integer trainId) {
        for (Train train : trains) {
            if (trainId.equals(train.getTrainId())) {
                return train;
            }
        }
        return null;
    }

    public boolean updateTrain(Integer trainId, Train updatedTrain) {
        Train existingTrain = getTrainById(trainId);
        if (existingTrain != null) {
            int index = trains.indexOf(existingTrain);
            trains.set(index, updatedTrain);
            return true;
        }
        return false;
    }

    public boolean deleteTrain(Integer trainId) {
        return trains.removeIf(train -> trainId.equals(train.getTrainId()));
    }

    public Integer getNextTrainId() {
        int maxId = 0;
        for (Train train : trains) {
            if (train.getTrainId() != null && train.getTrainId() > maxId) {
                maxId = train.getTrainId();
            }
        }
        return maxId + 1;
    }

    private boolean containsTrain(Integer trainId) {
        return getTrainById(trainId) != null;
    }

    public void clear() {
        trains.clear();
    }
}

