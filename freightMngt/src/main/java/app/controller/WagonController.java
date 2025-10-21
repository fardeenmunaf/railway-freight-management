package app.controller;

import app.model.Wagon;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Wagon entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Wagon objects.
 * This controller manages wagon data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WagonController {
    private List<Wagon> wagons;

    /**
     * Default constructor that initializes the wagon list.
     */
    public WagonController() {
        this.wagons = new ArrayList<>();
    }

    /**
     * Creates and adds a new wagon to the system.
     *
     * @param wagon the Wagon object to be created
     * @return the created Wagon object
     */
    public Wagon createWagon(Wagon wagon) {
        if (wagon != null) {
            wagons.add(wagon);
        }
        return wagon;
    }

    /**
     * Retrieves all wagons from the system.
     *
     * @return a list of all Wagon objects
     */
    public List<Wagon> getAllWagons() {
        return new ArrayList<>(wagons);
    }

    /**
     * Retrieves a wagon by its unique identifier.
     *
     * @param wagonId the unique identifier of the wagon
     * @return an Optional containing the Wagon if found, or empty if not found
     */
    public Optional<Wagon> getWagonById(Integer wagonId) {
        return wagons.stream()
                .filter(wagon -> wagon.getWagonId().equals(wagonId))
                .findFirst();
    }

    /**
     * Updates an existing wagon in the system.
     *
     * @param wagonId the unique identifier of the wagon to update
     * @param updatedWagon the Wagon object with updated information
     * @return true if the wagon was updated successfully, false otherwise
     */
    public boolean updateWagon(Integer wagonId, Wagon updatedWagon) {
        Optional<Wagon> existingWagon = getWagonById(wagonId);
        if (existingWagon.isPresent()) {
            int index = wagons.indexOf(existingWagon.get());
            wagons.set(index, updatedWagon);
            return true;
        }
        return false;
    }

    /**
     * Deletes a wagon from the system.
     *
     * @param wagonId the unique identifier of the wagon to delete
     * @return true if the wagon was deleted successfully, false otherwise
     */
    public boolean deleteWagon(Integer wagonId) {
        return wagons.removeIf(wagon -> wagon.getWagonId().equals(wagonId));
    }
}

