package app.controller;

import app.model.PickTask;
import java.util.ArrayList;
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
    private List<PickTask> pickTasks;

    /**
     * Default constructor that initializes the pick task list.
     */
    public PickTaskController() {
        this.pickTasks = new ArrayList<>();
    }

    /**
     * Creates and adds a new pick task to the system.
     *
     * @param pickTask the PickTask object to be created
     * @return the created PickTask object
     */
    public PickTask createPickTask(PickTask pickTask) {
        if (pickTask != null) {
            pickTasks.add(pickTask);
        }
        return pickTask;
    }

    /**
     * Retrieves all pick tasks from the system.
     *
     * @return a list of all PickTask objects
     */
    public List<PickTask> getAllPickTasks() {
        return new ArrayList<>(pickTasks);
    }

    /**
     * Retrieves a pick task by its unique identifier.
     *
     * @param pickTaskId the unique identifier of the pick task
     * @return an Optional containing the PickTask if found, or empty if not found
     */
    public Optional<PickTask> getPickTaskById(Integer pickTaskId) {
        return pickTasks.stream()
                .filter(pickTask -> pickTask.getPickTaskId().equals(pickTaskId))
                .findFirst();
    }

    /**
     * Updates an existing pick task in the system.
     *
     * @param pickTaskId the unique identifier of the pick task to update
     * @param updatedPickTask the PickTask object with updated information
     * @return true if the pick task was updated successfully, false otherwise
     */
    public boolean updatePickTask(Integer pickTaskId, PickTask updatedPickTask) {
        Optional<PickTask> existingPickTask = getPickTaskById(pickTaskId);
        if (existingPickTask.isPresent()) {
            int index = pickTasks.indexOf(existingPickTask.get());
            pickTasks.set(index, updatedPickTask);
            return true;
        }
        return false;
    }

    /**
     * Deletes a pick task from the system.
     *
     * @param pickTaskId the unique identifier of the pick task to delete
     * @return true if the pick task was deleted successfully, false otherwise
     */
    public boolean deletePickTask(Integer pickTaskId) {
        return pickTasks.removeIf(pickTask -> pickTask.getPickTaskId().equals(pickTaskId));
    }
}

