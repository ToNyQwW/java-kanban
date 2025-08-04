package service.interfaces;

import model.Task;

import java.util.List;

public interface PriorityManager {

    boolean addTask(Task taskToAdd);

    boolean removeTask(Task taskToRemove);

    boolean containsTask(Task taskToCheck);

    List<Task> getPrioritizedTasks();
}
