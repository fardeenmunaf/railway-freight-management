package app.controller;

import app.model.Wagon;
import app.repositories.WagonRepository;
import app.repositories.Repositories;
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
    private final WagonRepository repository;

    /**
     * Default constructor that initializes the controller with the wagon repository.
     */
    public WagonController() {
        this.repository = Repositories.getInstance().getWagonRepository();
    }

    /**
     * Creates and adds a new wagon to the system.
     *
     * @param wagon the Wagon object to be created
     * @return the created Wagon object
     */
    public Wagon createWagon(Wagon wagon) {
        if (wagon != null) {
            repository.addWagon(wagon);
        }
        return wagon;
    }

    /**
     * Retrieves all wagons from the system.
     *
     * @return a list of all Wagon objects
     */
    public List<Wagon> getAllWagons() {
        return repository.getAllWagons();
    }

    /**
     * Retrieves a wagon by its unique identifier.
     *
     * @param wagonId the unique identifier of the wagon
     * @return an Optional containing the Wagon if found, or empty if not found
     */
    public Optional<Wagon> getWagonById(Integer wagonId) {
        Wagon wagon = repository.getWagonById(wagonId);
        return Optional.ofNullable(wagon);
    }

    /**
     * Updates an existing wagon in the system.
     *
     * @param wagonId the unique identifier of the wagon to update
     * @param updatedWagon the Wagon object with updated information
     * @return true if the wagon was updated successfully, false otherwise
     */
    public boolean updateWagon(Integer wagonId, Wagon updatedWagon) {
        return repository.updateWagon(wagonId, updatedWagon);
    }

    /**
     * Deletes a wagon from the system.
     *
     * @param wagonId the unique identifier of the wagon to delete
     * @return true if the wagon was deleted successfully, false otherwise
     */
    public boolean deleteWagon(Integer wagonId) {
        return repository.deleteWagon(wagonId);
    }

    /**
     * Gets the next available wagon ID.
     *
     * @return the next wagon ID to be used
     */
    public Integer getNextWagonId() {
        return repository.getNextWagonId();
    }

    /**
     * Clears all wagons from the system.
     */
    public void clearAllWagons() {
        repository.clear();
    }
}

