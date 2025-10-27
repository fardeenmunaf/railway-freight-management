package app.controller;

import app.model.PickTask;
import app.repositories.PickTaskRepository;
import app.repositories.Repositories;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing PickTask entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for PickTask objects.
 * This controller manages pick task data in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class PickTaskController {
    private final PickTaskRepository repository;

    /**
     * Default constructor that initializes the controller with the pick task repository.
     */
    public PickTaskController() {
        this.repository = Repositories.getInstance().getPickTaskRepository();
    }

    /**
     * Creates and adds a new pick task to the system.
     *
     * @param pickTask the PickTask object to be created
     * @return the created PickTask object
     */
    public PickTask createPickTask(PickTask pickTask) {
        if (pickTask != null) {
            repository.addPickTask(pickTask);
        }
        return pickTask;
    }

    /**
     * Retrieves all pick tasks from the system.
     *
     * @return a list of all PickTask objects
     */
    public List<PickTask> getAllPickTasks() {
        return repository.getAllPickTasks();
    }

    /**
     * Retrieves a pick task by its unique identifier.
     *
     * @param pickTaskId the unique identifier of the pick task
     * @return an Optional containing the PickTask if found, or empty if not found
     */
    public Optional<PickTask> getPickTaskById(Integer pickTaskId) {
        PickTask pickTask = repository.getPickTaskById(pickTaskId);
        return Optional.ofNullable(pickTask);
    }

    /**
     * Updates an existing pick task in the system.
     *
     * @param pickTaskId the unique identifier of the pick task to update
     * @param updatedPickTask the PickTask object with updated information
     * @return true if the pick task was updated successfully, false otherwise
     */
    public boolean updatePickTask(Integer pickTaskId, PickTask updatedPickTask) {
        return repository.updatePickTask(pickTaskId, updatedPickTask);
    }

    /**
     * Deletes a pick task from the system.
     *
     * @param pickTaskId the unique identifier of the pick task to delete
     * @return true if the pick task was deleted successfully, false otherwise
     */
    public boolean deletePickTask(Integer pickTaskId) {
        return repository.deletePickTask(pickTaskId);
    }

    /**
     * Gets the next available pick task ID.
     *
     * @return the next pick task ID to be used
     */
    public Integer getNextPickTaskId() {
        return repository.getNextTaskId();
    }

    /**
     * Clears all pick tasks from the system.
     */
    public void clearAllPickTasks() {
        repository.clear();
    }
}
