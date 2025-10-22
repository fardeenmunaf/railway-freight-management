package app.repositories;

import app.model.Wagon;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing wagons in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WagonRepository {

    private final List<Wagon> wagons = new ArrayList<>();

    public void addWagon(Wagon wagon) {
        if (wagon != null && wagon.getWagonId() != null) {
            if (!containsWagon(wagon.getWagonId())) {
                wagons.add(wagon);
            }
        }
    }

    public List<Wagon> getAllWagons() {
        return new ArrayList<>(wagons);
    }

    public Wagon getWagonById(Integer wagonId) {
        for (Wagon wagon : wagons) {
            if (wagonId.equals(wagon.getWagonId())) {
                return wagon;
            }
        }
        return null;
    }

    public boolean updateWagon(Integer wagonId, Wagon updatedWagon) {
        Wagon existingWagon = getWagonById(wagonId);
        if (existingWagon != null) {
            int index = wagons.indexOf(existingWagon);
            wagons.set(index, updatedWagon);
            return true;
        }
        return false;
    }

    public boolean deleteWagon(Integer wagonId) {
        return wagons.removeIf(wagon -> wagonId.equals(wagon.getWagonId()));
    }

    public Integer getNextWagonId() {
        int maxId = 0;
        for (Wagon wagon : wagons) {
            if (wagon.getWagonId() != null && wagon.getWagonId() > maxId) {
                maxId = wagon.getWagonId();
            }
        }
        return maxId + 1;
    }

    private boolean containsWagon(Integer wagonId) {
        return getWagonById(wagonId) != null;
    }

    public void clear() {
        wagons.clear();
    }
}

