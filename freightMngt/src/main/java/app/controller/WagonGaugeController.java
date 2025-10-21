package app.controller;

import app.model.WagonGauge;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing WagonGauge entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for WagonGauge objects.
 * This controller manages wagon gauge data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WagonGaugeController {
    private List<WagonGauge> wagonGauges;

    /**
     * Default constructor that initializes the wagon gauge list.
     */
    public WagonGaugeController() {
        this.wagonGauges = new ArrayList<>();
    }

    /**
     * Creates and adds a new wagon gauge to the system.
     *
     * @param wagonGauge the WagonGauge object to be created
     * @return the created WagonGauge object
     */
    public WagonGauge createWagonGauge(WagonGauge wagonGauge) {
        if (wagonGauge != null) {
            wagonGauges.add(wagonGauge);
        }
        return wagonGauge;
    }

    /**
     * Retrieves all wagon gauges from the system.
     *
     * @return a list of all WagonGauge objects
     */
    public List<WagonGauge> getAllWagonGauges() {
        return new ArrayList<>(wagonGauges);
    }

    /**
     * Retrieves a wagon gauge by its unique identifier.
     *
     * @param wagonGaugeId the unique identifier of the wagon gauge
     * @return an Optional containing the WagonGauge if found, or empty if not found
     */
    public Optional<WagonGauge> getWagonGaugeById(Integer wagonGaugeId) {
        return wagonGauges.stream()
                .filter(wagonGauge -> wagonGauge.getWagonGaugeId().equals(wagonGaugeId))
                .findFirst();
    }

    /**
     * Updates an existing wagon gauge in the system.
     *
     * @param wagonGaugeId the unique identifier of the wagon gauge to update
     * @param updatedWagonGauge the WagonGauge object with updated information
     * @return true if the wagon gauge was updated successfully, false otherwise
     */
    public boolean updateWagonGauge(Integer wagonGaugeId, WagonGauge updatedWagonGauge) {
        Optional<WagonGauge> existingWagonGauge = getWagonGaugeById(wagonGaugeId);
        if (existingWagonGauge.isPresent()) {
            int index = wagonGauges.indexOf(existingWagonGauge.get());
            wagonGauges.set(index, updatedWagonGauge);
            return true;
        }
        return false;
    }

    /**
     * Deletes a wagon gauge from the system.
     *
     * @param wagonGaugeId the unique identifier of the wagon gauge to delete
     * @return true if the wagon gauge was deleted successfully, false otherwise
     */
    public boolean deleteWagonGauge(Integer wagonGaugeId) {
        return wagonGauges.removeIf(wagonGauge -> wagonGauge.getWagonGaugeId().equals(wagonGaugeId));
    }
}

