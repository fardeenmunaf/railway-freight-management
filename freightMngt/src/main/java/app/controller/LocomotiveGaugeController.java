package app.controller;

import app.model.LocomotiveGauge;
import app.repositories.LocomotiveGaugeRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing LocomotiveGauge entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for LocomotiveGauge objects.
 * This controller manages locomotive gauge relationships in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveGaugeController {
    private final LocomotiveGaugeRepository repository;

    /**
     * Default constructor that initializes the controller with the locomotive gauge repository.
     */
    public LocomotiveGaugeController() {
        this.repository = Repositories.getInstance().getLocomotiveGaugeRepository();
    }

    /**
     * Creates and adds a new locomotive gauge relationship to the system.
     *
     * @param locomotiveGauge the LocomotiveGauge object to be created
     * @return the created LocomotiveGauge object
     */
    public LocomotiveGauge createLocomotiveGauge(LocomotiveGauge locomotiveGauge) {
        if (locomotiveGauge != null) {
            repository.addLocomotiveGauge(locomotiveGauge);
        }
        return locomotiveGauge;
    }

    /**
     * Retrieves all locomotive gauges from the system.
     *
     * @return a list of all LocomotiveGauge objects
     */
    public List<LocomotiveGauge> getAllLocomotiveGauges() {
        return repository.getAllLocomotiveGauges();
    }

    /**
     * Retrieves locomotive gauges by locomotive ID.
     *
     * @param locomotiveId the locomotive identifier
     * @return a list of LocomotiveGauge objects for the locomotive
     */
    public List<LocomotiveGauge> getLocomotiveGaugesByLocomotiveId(Integer locomotiveId) {
        return repository.getLocomotiveGaugesByLocomotiveId(locomotiveId);
    }

    /**
     * Deletes a locomotive gauge from the system.
     *
     * @param locomotiveId the locomotive identifier
     * @param gaugeId the gauge identifier
     * @return true if the locomotive gauge was deleted successfully, false otherwise
     */
    public boolean deleteLocomotiveGauge(Integer locomotiveId, Integer gaugeId) {
        return repository.deleteLocomotiveGauge(locomotiveId, gaugeId);
    }

    /**
     * Clears all locomotive gauges from the system.
     */
    public void clearAllLocomotiveGauges() {
        repository.clear();
    }
}
