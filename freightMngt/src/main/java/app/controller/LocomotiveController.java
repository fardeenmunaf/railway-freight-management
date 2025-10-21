package app.controller;

import app.model.Locomotive;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Locomotive entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Locomotive objects.
 * This controller manages locomotive data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveController {
    private List<Locomotive> locomotives;

    /**
     * Default constructor that initializes the locomotive list.
     */
    public LocomotiveController() {
        this.locomotives = new ArrayList<>();
    }

    /**
     * Creates and adds a new locomotive to the system.
     *
     * @param locomotive the Locomotive object to be created
     * @return the created Locomotive object
     */
    public Locomotive createLocomotive(Locomotive locomotive) {
        if (locomotive != null) {
            locomotives.add(locomotive);
        }
        return locomotive;
    }

    /**
     * Retrieves all locomotives from the system.
     *
     * @return a list of all Locomotive objects
     */
    public List<Locomotive> getAllLocomotives() {
        return new ArrayList<>(locomotives);
    }

    /**
     * Retrieves a locomotive by its unique identifier.
     *
     * @param locomotiveId the unique identifier of the locomotive
     * @return an Optional containing the Locomotive if found, or empty if not found
     */
    public Optional<Locomotive> getLocomotiveById(Integer locomotiveId) {
        return locomotives.stream()
                .filter(locomotive -> locomotive.getLocomotiveId().equals(locomotiveId))
                .findFirst();
    }

    /**
     * Updates an existing locomotive in the system.
     *
     * @param locomotiveId the unique identifier of the locomotive to update
     * @param updatedLocomotive the Locomotive object with updated information
     * @return true if the locomotive was updated successfully, false otherwise
     */
    public boolean updateLocomotive(Integer locomotiveId, Locomotive updatedLocomotive) {
        Optional<Locomotive> existingLocomotive = getLocomotiveById(locomotiveId);
        if (existingLocomotive.isPresent()) {
            int index = locomotives.indexOf(existingLocomotive.get());
            locomotives.set(index, updatedLocomotive);
            return true;
        }
        return false;
    }

    /**
     * Deletes a locomotive from the system.
     *
     * @param locomotiveId the unique identifier of the locomotive to delete
     * @return true if the locomotive was deleted successfully, false otherwise
     */
    public boolean deleteLocomotive(Integer locomotiveId) {
        return locomotives.removeIf(locomotive -> locomotive.getLocomotiveId().equals(locomotiveId));
    }
}

