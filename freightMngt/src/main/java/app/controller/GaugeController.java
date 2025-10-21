package app.controller;

import app.model.Gauge;
import app.repositories.GaugeRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Gauge entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Gauge objects.
 * This controller manages gauge data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class GaugeController {
    private final GaugeRepository repository;

    /**
     * Default constructor that initializes the controller with the gauge repository.
     */
    public GaugeController() {
        this.repository = Repositories.getInstance().getGaugeRepository();
    }

    /**
     * Creates and adds a new gauge to the system.
     *
     * @param gauge the Gauge object to be created
     * @return the created Gauge object
     */
    public Gauge createGauge(Gauge gauge) {
        if (gauge != null) {
            repository.addGauge(gauge);
        }
        return gauge;
    }

    /**
     * Retrieves all gauges from the system.
     *
     * @return a list of all Gauge objects
     */
    public List<Gauge> getAllGauges() {
        return repository.getAllGauges();
    }

    /**
     * Retrieves a gauge by its unique identifier.
     *
     * @param gaugeId the unique identifier of the gauge
     * @return an Optional containing the Gauge if found, or empty if not found
     */
    public Optional<Gauge> getGaugeById(Integer gaugeId) {
        Gauge gauge = repository.getGaugeById(gaugeId);
        return Optional.ofNullable(gauge);
    }

    /**
     * Retrieves a gauge by its name.
     *
     * @param gaugeName the name of the gauge
     * @return an Optional containing the Gauge if found, or empty if not found
     */
    public Optional<Gauge> getGaugeByName(String gaugeName) {
        Gauge gauge = repository.getGaugeByName(gaugeName);
        return Optional.ofNullable(gauge);
    }

    /**
     * Updates an existing gauge in the system.
     *
     * @param gaugeId the unique identifier of the gauge to update
     * @param updatedGauge the Gauge object with updated information
     * @return true if the gauge was updated successfully, false otherwise
     */
    public boolean updateGauge(Integer gaugeId, Gauge updatedGauge) {
        return repository.updateGauge(gaugeId, updatedGauge);
    }

    /**
     * Deletes a gauge from the system.
     *
     * @param gaugeId the unique identifier of the gauge to delete
     * @return true if the gauge was deleted successfully, false otherwise
     */
    public boolean deleteGauge(Integer gaugeId) {
        return repository.deleteGauge(gaugeId);
    }

    /**
     * Gets the next available gauge ID.
     *
     * @return the next gauge ID to be used
     */
    public Integer getNextGaugeId() {
        return repository.getNextGaugeId();
    }

    /**
     * Checks if a gauge with the given name exists in the repository.
     *
     * @param gaugeName the gauge name
     * @return true if a gauge with this name exists, false otherwise
     */
    public boolean containsName(String gaugeName) {
        return repository.containsName(gaugeName);
    }

    /**
     * Clears all gauges from the system.
     */
    public void clearAllGauges() {
        repository.clear();
    }
}
