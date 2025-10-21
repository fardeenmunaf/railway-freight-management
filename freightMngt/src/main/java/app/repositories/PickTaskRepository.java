package app.repositories;

import app.model.PickTask;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing pick tasks in the freight management system.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class PickTaskRepository {

    private final List<PickTask> pickTasks = new ArrayList<>();

    public void addPickTask(PickTask pickTask) {
        if (pickTask != null && pickTask.getPickTaskId() != null) {
            if (!containsPickTask(pickTask.getPickTaskId())) {
                pickTasks.add(pickTask);
            }
        }
    }

    public List<PickTask> getAllPickTasks() {
        return new ArrayList<>(pickTasks);
    }

    public PickTask getPickTaskById(Integer taskId) {
        for (PickTask pickTask : pickTasks) {
            if (taskId.equals(pickTask.getPickTaskId())) {
                return pickTask;
            }
        }
        return null;
    }

    public boolean updatePickTask(Integer taskId, PickTask updatedPickTask) {
        PickTask existingPickTask = getPickTaskById(taskId);
        if (existingPickTask != null) {
            int index = pickTasks.indexOf(existingPickTask);
            pickTasks.set(index, updatedPickTask);
            return true;
        }
        return false;
    }

    public boolean deletePickTask(Integer taskId) {
        return pickTasks.removeIf(pickTask -> taskId.equals(pickTask.getPickTaskId()));
    }

    public Integer getNextTaskId() {
        int maxId = 0;
        for (PickTask pickTask : pickTasks) {
            if (pickTask.getPickTaskId() != null && pickTask.getPickTaskId() > maxId) {
                maxId = pickTask.getPickTaskId();
            }
        }
        return maxId + 1;
    }

    private boolean containsPickTask(Integer taskId) {
        return getPickTaskById(taskId) != null;
    }

    public void clear() {
        pickTasks.clear();
    }
}
