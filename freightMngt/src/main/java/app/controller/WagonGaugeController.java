package app.controller;

import app.model.WagonGauge;
import app.model.Wagon;
import app.model.Gauge;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing WagonGauge entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for WagonGauge objects.
 * This controller manages wagon gauge relationships in the freight management system.
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
     * Creates and adds a new wagon gauge relationship to the system.
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
     * Retrieves a wagon gauge by wagon ID.
     *
     * @param wagonId the unique identifier of the wagon
     * @return an Optional containing the WagonGauge if found, or empty if not found
     */
    public Optional<WagonGauge> getWagonGaugeById(Integer wagonId) {
        return wagonGauges.stream()
                .filter(wagonGauge -> wagonGauge.getWagon() != null &&
                        wagonGauge.getWagon().getWagonId().equals(wagonId))
                .findFirst();
    }

    /**
     * Retrieves wagon gauges by wagon.
     *
     * @param wagon the wagon to search for
     * @return a list of WagonGauge objects associated with the wagon
     */
    public List<WagonGauge> getWagonGaugesByWagon(Wagon wagon) {
        List<WagonGauge> result = new ArrayList<>();
        for (WagonGauge wg : wagonGauges) {
            if (wg.getWagon() != null && wg.getWagon().equals(wagon)) {
                result.add(wg);
            }
        }
        return result;
    }

    /**
     * Retrieves wagon gauges by gauge.
     *
     * @param gauge the gauge to search for
     * @return a list of WagonGauge objects associated with the gauge
     */
    public List<WagonGauge> getWagonGaugesByGauge(Gauge gauge) {
        List<WagonGauge> result = new ArrayList<>();
        for (WagonGauge wg : wagonGauges) {
            if (wg.getGauge() != null && wg.getGauge().equals(gauge)) {
                result.add(wg);
            }
        }
        return result;
    }

    /**
     * Updates an existing wagon gauge in the system.
     *
     * @param wagonId the unique identifier of the wagon
     * @param updatedWagonGauge the WagonGauge object with updated information
     * @return true if the wagon gauge was updated successfully, false otherwise
     */
    public boolean updateWagonGauge(Integer wagonId, WagonGauge updatedWagonGauge) {
        Optional<WagonGauge> existingWagonGauge = getWagonGaugeById(wagonId);
        if (existingWagonGauge.isPresent()) {
            int index = wagonGauges.indexOf(existingWagonGauge.get());
            wagonGauges.set(index, updatedWagonGauge);
            return true;
        }
        return false;
    }

    /**
     * Deletes a wagon gauge from the system by wagon ID.
     *
     * @param wagonId the unique identifier of the wagon
     * @return true if the wagon gauge was deleted successfully, false otherwise
     */
    public boolean deleteWagonGauge(Integer wagonId) {
        return wagonGauges.removeIf(wagonGauge ->
            wagonGauge.getWagon() != null &&
            wagonGauge.getWagon().getWagonId().equals(wagonId));
    }

    /**
     * Deletes a wagon gauge relationship from the system.
     *
     * @param wagon the wagon in the relationship
     * @param gauge the gauge in the relationship
     * @return true if the wagon gauge was deleted successfully, false otherwise
     */
    public boolean deleteWagonGaugeByRelationship(Wagon wagon, Gauge gauge) {
        return wagonGauges.removeIf(wagonGauge ->
            wagonGauge.getWagon() != null && wagonGauge.getWagon().equals(wagon) &&
            wagonGauge.getGauge() != null && wagonGauge.getGauge().equals(gauge));
    }
}
