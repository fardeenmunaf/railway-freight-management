package app.controller;

import app.model.WagonGauge;
import app.repositories.WagonGaugeRepository;
import app.repositories.Repositories;
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
    private final WagonGaugeRepository repository;

    /**
     * Default constructor that initializes the controller with the wagon gauge repository.
     */
    public WagonGaugeController() {
        this.repository = Repositories.getInstance().getWagonGaugeRepository();
    }

    /**
     * Creates and adds a new wagon gauge relationship to the system.
     *
     * @param wagonGauge the WagonGauge object to be created
     * @return the created WagonGauge object
     */
    public WagonGauge createWagonGauge(WagonGauge wagonGauge) {
        if (wagonGauge != null) {
            repository.addWagonGauge(wagonGauge);
        }
        return wagonGauge;
    }

    /**
     * Retrieves all wagon gauges from the system.
     *
     * @return a list of all WagonGauge objects
     */
    public List<WagonGauge> getAllWagonGauges() {
        return repository.getAllWagonGauges();
    }

    /**
     * Retrieves wagon gauges by wagon ID.
     *
     * @param wagonId the unique identifier of the wagon
     * @return a list of WagonGauge objects associated with the wagon
     */
    public List<WagonGauge> getWagonGaugesByWagonId(Integer wagonId) {
        return repository.getWagonGaugesByWagonId(wagonId);
    }

    /**
     * Deletes a wagon gauge from the system.
     *
     * @param wagonId the wagon identifier
     * @param gaugeId the gauge identifier
     * @return true if the wagon gauge was deleted successfully, false otherwise
     */
    public boolean deleteWagonGauge(Integer wagonId, Integer gaugeId) {
        return repository.deleteWagonGauge(wagonId, gaugeId);
    }

    /**
     * Clears all wagon gauges from the system.
     */
    public void clearAllWagonGauges() {
        repository.clear();
    }
}
