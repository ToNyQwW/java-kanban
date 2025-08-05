package service.interfaces;

import model.Task;

import java.time.LocalDateTime;
import java.util.List;

public interface PriorityManager {

    boolean addTask(Task taskToAdd);

    boolean removeTask(Task taskToRemove);

    List<Task> getPrioritizedTasks();

    LocalDateTime getBaseTime();
}
