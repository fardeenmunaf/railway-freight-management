package app.controller;

import app.model.LocomotiveGauge;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing LocomotiveGauge entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for LocomotiveGauge objects.
 * This controller manages locomotive gauge data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveGaugeController {
    private List<LocomotiveGauge> locomotiveGauges;

    /**
     * Default constructor that initializes the locomotive gauge list.
     */
    public LocomotiveGaugeController() {
        this.locomotiveGauges = new ArrayList<>();
    }

    /**
     * Creates and adds a new locomotive gauge to the system.
     *
     * @param locomotiveGauge the LocomotiveGauge object to be created
     * @return the created LocomotiveGauge object
     */
    public LocomotiveGauge createLocomotiveGauge(LocomotiveGauge locomotiveGauge) {
        if (locomotiveGauge != null) {
            locomotiveGauges.add(locomotiveGauge);
        }
        return locomotiveGauge;
    }

    /**
     * Retrieves all locomotive gauges from the system.
     *
     * @return a list of all LocomotiveGauge objects
     */
    public List<LocomotiveGauge> getAllLocomotiveGauges() {
        return new ArrayList<>(locomotiveGauges);
    }

    /**
     * Retrieves a locomotive gauge by its unique identifier.
     *
     * @param locomotiveId the unique identifier of the locomotive gauge
     * @return an Optional containing the LocomotiveGauge if found, or empty if not found
     */
    public Optional<LocomotiveGauge> getLocomotiveGaugeById(Integer locomotiveId) {
        return locomotiveGauges.stream()
                .filter(locomotiveGauge -> locomotiveGauge.getLocomotive().getLocomotiveId().equals(locomotiveId))
                .findFirst();
    }

    /**
     * Updates an existing locomotive gauge in the system.
     *
     * @param locomotiveId the unique identifier of the locomotive gauge to update
     * @param updatedLocomotiveGauge the LocomotiveGauge object with updated information
     * @return true if the locomotive gauge was updated successfully, false otherwise
     */
    public boolean updateLocomotiveGauge(Integer locomotiveId, LocomotiveGauge updatedLocomotiveGauge) {
        Optional<LocomotiveGauge> existingLocomotiveGauge = getLocomotiveGaugeById(locomotiveId);
        if (existingLocomotiveGauge.isPresent()) {
            int index = locomotiveGauges.indexOf(existingLocomotiveGauge.get());
            locomotiveGauges.set(index, updatedLocomotiveGauge);
            return true;
        }
        return false;
    }

    /**
     * Deletes a locomotive gauge from the system.
     *
     * @param locomotiveId the unique identifier of the locomotive gauge to delete
     * @return true if the locomotive gauge was deleted successfully, false otherwise
     */
    public boolean deleteLocomotiveGauge(Integer locomotiveId) {
        return locomotiveGauges.removeIf(locomotiveGauge -> locomotiveGauge.getLocomotive().getLocomotiveId().equals(locomotiveId));
    }
}
