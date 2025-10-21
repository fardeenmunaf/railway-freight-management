package app.controller;

import app.model.LocomotiveEletric;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing LocomotiveEletric entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for LocomotiveEletric objects.
 * This controller manages electric locomotive data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class LocomotiveEletricController {
    private List<LocomotiveEletric> locomotiveEletrics;

    /**
     * Default constructor that initializes the locomotive electric list.
     */
    public LocomotiveEletricController() {
        this.locomotiveEletrics = new ArrayList<>();
    }

    /**
     * Creates and adds a new electric locomotive to the system.
     *
     * @param locomotiveEletric the LocomotiveEletric object to be created
     * @return the created LocomotiveEletric object
     */
    public LocomotiveEletric createLocomotiveEletric(LocomotiveEletric locomotiveEletric) {
        if (locomotiveEletric != null) {
            locomotiveEletrics.add(locomotiveEletric);
        }
        return locomotiveEletric;
    }

    /**
     * Retrieves all electric locomotives from the system.
     *
     * @return a list of all LocomotiveEletric objects
     */
    public List<LocomotiveEletric> getAllLocomotiveEletrics() {
        return new ArrayList<>(locomotiveEletrics);
    }

    /**
     * Retrieves an electric locomotive by its unique identifier.
     *
     * @param locomotiveEletricId the unique identifier of the electric locomotive
     * @return an Optional containing the LocomotiveEletric if found, or empty if not found
     */
    public Optional<LocomotiveEletric> getLocomotiveEletricById(Integer locomotiveEletricId) {
        return locomotiveEletrics.stream()
                .filter(locomotiveEletric -> locomotiveEletric.getLocomotiveEletricId().equals(locomotiveEletricId))
                .findFirst();
    }

    /**
     * Updates an existing electric locomotive in the system.
     *
     * @param locomotiveEletricId the unique identifier of the electric locomotive to update
     * @param updatedLocomotiveEletric the LocomotiveEletric object with updated information
     * @return true if the electric locomotive was updated successfully, false otherwise
     */
    public boolean updateLocomotiveEletric(Integer locomotiveEletricId, LocomotiveEletric updatedLocomotiveEletric) {
        Optional<LocomotiveEletric> existingLocomotiveEletric = getLocomotiveEletricById(locomotiveEletricId);
        if (existingLocomotiveEletric.isPresent()) {
            int index = locomotiveEletrics.indexOf(existingLocomotiveEletric.get());
            locomotiveEletrics.set(index, updatedLocomotiveEletric);
            return true;
        }
        return false;
    }

    /**
     * Deletes an electric locomotive from the system.
     *
     * @param locomotiveEletricId the unique identifier of the electric locomotive to delete
     * @return true if the electric locomotive was deleted successfully, false otherwise
     */
    public boolean deleteLocomotiveEletric(Integer locomotiveEletricId) {
        return locomotiveEletrics.removeIf(locomotiveEletric -> locomotiveEletric.getLocomotiveEletricId().equals(locomotiveEletricId));
    }
}

