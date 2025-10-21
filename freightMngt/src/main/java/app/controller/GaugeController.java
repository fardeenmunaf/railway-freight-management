package app.controller;

import app.model.Gauge;
import java.util.ArrayList;
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
    private List<Gauge> gauges;

    /**
     * Default constructor that initializes the gauge list.
     */
    public GaugeController() {
        this.gauges = new ArrayList<>();
    }

    /**
     * Creates and adds a new gauge to the system.
     *
     * @param gauge the Gauge object to be created
     * @return the created Gauge object
     */
    public Gauge createGauge(Gauge gauge) {
        if (gauge != null) {
            gauges.add(gauge);
        }
        return gauge;
    }

    /**
     * Retrieves all gauges from the system.
     *
     * @return a list of all Gauge objects
     */
    public List<Gauge> getAllGauges() {
        return new ArrayList<>(gauges);
    }

    /**
     * Retrieves a gauge by its unique identifier.
     *
     * @param gaugeId the unique identifier of the gauge
     * @return an Optional containing the Gauge if found, or empty if not found
     */
    public Optional<Gauge> getGaugeById(Integer gaugeId) {
        return gauges.stream()
                .filter(gauge -> gauge.getGaugeId().equals(gaugeId))
                .findFirst();
    }

    /**
     * Updates an existing gauge in the system.
     *
     * @param gaugeId the unique identifier of the gauge to update
     * @param updatedGauge the Gauge object with updated information
     * @return true if the gauge was updated successfully, false otherwise
     */
    public boolean updateGauge(Integer gaugeId, Gauge updatedGauge) {
        Optional<Gauge> existingGauge = getGaugeById(gaugeId);
        if (existingGauge.isPresent()) {
            int index = gauges.indexOf(existingGauge.get());
            gauges.set(index, updatedGauge);
            return true;
        }
        return false;
    }

    /**
     * Deletes a gauge from the system.
     *
     * @param gaugeId the unique identifier of the gauge to delete
     * @return true if the gauge was deleted successfully, false otherwise
     */
    public boolean deleteGauge(Integer gaugeId) {
        return gauges.removeIf(gauge -> gauge.getGaugeId().equals(gaugeId));
    }
}

