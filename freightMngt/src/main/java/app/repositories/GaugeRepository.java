package app.repositories;

import app.model.Gauge;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing gauges in the freight management system.
 * This class provides methods to add, retrieve, and manage gauge entities.
 * It maintains a collection of all gauges and allows searching by gauge ID or name.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class GaugeRepository {

    private final List<Gauge> gauges = new ArrayList<>();

    /**
     * Adds a new gauge to the repository if it doesn't already exist.
     *
     * @param gauge the gauge to add
     */
    public void addGauge(Gauge gauge) {
        if (gauge != null && gauge.getGaugeId() != null) {
            if (!containsGauge(gauge.getGaugeId())) {
                gauges.add(gauge);
            }
        }
    }

    /**
     * Retrieves all gauges in the repository.
     *
     * @return a new list containing all gauges
     */
    public List<Gauge> getAllGauges() {
        return new ArrayList<>(gauges);
    }

    /**
     * Retrieves a specific gauge by its ID.
     *
     * @param gaugeId the gauge identifier
     * @return the matching gauge, or null if not found
     */
    public Gauge getGaugeById(Integer gaugeId) {
        for (Gauge gauge : gauges) {
            if (gaugeId.equals(gauge.getGaugeId())) {
                return gauge;
            }
        }
        return null;
    }

    /**
     * Retrieves a specific gauge by its name.
     *
     * @param gaugeName the gauge name
     * @return the matching gauge, or null if not found
     */
    public Gauge getGaugeByName(String gaugeName) {
        for (Gauge gauge : gauges) {
            if (gaugeName != null && gaugeName.equals(gauge.getGaugeName())) {
                return gauge;
            }
        }
        return null;
    }

    /**
     * Updates an existing gauge in the repository.
     *
     * @param gaugeId the gauge identifier
     * @param updatedGauge the updated gauge
     * @return true if the gauge was updated successfully, false otherwise
     */
    public boolean updateGauge(Integer gaugeId, Gauge updatedGauge) {
        Gauge existingGauge = getGaugeById(gaugeId);
        if (existingGauge != null) {
            int index = gauges.indexOf(existingGauge);
            gauges.set(index, updatedGauge);
            return true;
        }
        return false;
    }

    /**
     * Deletes a gauge from the repository.
     *
     * @param gaugeId the gauge identifier
     * @return true if the gauge was deleted successfully, false otherwise
     */
    public boolean deleteGauge(Integer gaugeId) {
        return gauges.removeIf(gauge -> gaugeId.equals(gauge.getGaugeId()));
    }

    /**
     * Gets the next available gauge ID.
     * Finds the maximum gauge ID currently in use and returns the next sequential ID.
     *
     * @return the next available gauge ID (max current ID + 1)
     */
    public Integer getNextGaugeId() {
        int maxId = 0;
        for (Gauge gauge : gauges) {
            if (gauge.getGaugeId() != null && gauge.getGaugeId() > maxId) {
                maxId = gauge.getGaugeId();
            }
        }
        return maxId + 1;
    }

    /**
     * Checks if a gauge exists in the repository by its ID.
     *
     * @param gaugeId the gauge identifier
     * @return true if the gauge exists, false otherwise
     */
    private boolean containsGauge(Integer gaugeId) {
        return getGaugeById(gaugeId) != null;
    }

    /**
     * Checks if a gauge with the given name exists in the repository.
     *
     * @param gaugeName the gauge name
     * @return true if a gauge with this name exists, false otherwise
     */
    public boolean containsName(String gaugeName) {
        return getGaugeByName(gaugeName) != null;
    }

    /**
     * Clears all gauges from the repository.
     */
    public void clear() {
        gauges.clear();
    }
}
