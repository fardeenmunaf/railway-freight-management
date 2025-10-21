package app.controller;

import app.model.WagonModel;
import app.repositories.WagonModelRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing WagonModel entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for WagonModel objects.
 * This controller manages wagon model data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class WagonModelController {
    private final WagonModelRepository repository;

    /**
     * Default constructor that initializes the controller with the wagon model repository.
     */
    public WagonModelController() {
        this.repository = Repositories.getInstance().getWagonModelRepository();
    }

    /**
     * Creates and adds a new wagon model to the system.
     *
     * @param wagonModel the WagonModel object to be created
     * @return the created WagonModel object
     */
    public WagonModel createWagonModel(WagonModel wagonModel) {
        if (wagonModel != null) {
            repository.addWagonModel(wagonModel);
        }
        return wagonModel;
    }

    /**
     * Retrieves all wagon models from the system.
     *
     * @return a list of all WagonModel objects
     */
    public List<WagonModel> getAllWagonModels() {
        return repository.getAllWagonModels();
    }

    /**
     * Retrieves a wagon model by its unique identifier.
     *
     * @param wagonModelId the unique identifier of the wagon model
     * @return an Optional containing the WagonModel if found, or empty if not found
     */
    public Optional<WagonModel> getWagonModelById(Integer wagonModelId) {
        WagonModel wagonModel = repository.getWagonModelById(wagonModelId);
        return Optional.ofNullable(wagonModel);
    }

    /**
     * Retrieves a wagon model by its name.
     *
     * @param name the name of the wagon model
     * @return an Optional containing the WagonModel if found, or empty if not found
     */
    public Optional<WagonModel> getWagonModelByName(String name) {
        WagonModel wagonModel = repository.getWagonModelByName(name);
        return Optional.ofNullable(wagonModel);
    }

    /**
     * Updates an existing wagon model in the system.
     *
     * @param wagonModelId the unique identifier of the wagon model to update
     * @param updatedWagonModel the WagonModel object with updated information
     * @return true if the wagon model was updated successfully, false otherwise
     */
    public boolean updateWagonModel(Integer wagonModelId, WagonModel updatedWagonModel) {
        return repository.updateWagonModel(wagonModelId, updatedWagonModel);
    }

    /**
     * Deletes a wagon model from the system.
     *
     * @param wagonModelId the unique identifier of the wagon model to delete
     * @return true if the wagon model was deleted successfully, false otherwise
     */
    public boolean deleteWagonModel(Integer wagonModelId) {
        return repository.deleteWagonModel(wagonModelId);
    }

    /**
     * Gets the next available wagon model ID.
     *
     * @return the next wagon model ID to be used
     */
    public Integer getNextWagonModelId() {
        return repository.getNextWagonModelId();
    }

    /**
     * Clears all wagon models from the system.
     */
    public void clearAllWagonModels() {
        repository.clear();
    }
}