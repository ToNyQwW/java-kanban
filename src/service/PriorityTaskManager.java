package service;

import model.Task;
import service.interfaces.PriorityManager;

import java.time.LocalDateTime;
import java.util.*;

public class PriorityTaskManager implements PriorityManager {

    private final Set<Task> prioritizedTasks;

    public PriorityTaskManager() {
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean addTask(Task taskToAdd) {
        if (!taskToAdd.isTaskWithTime()) {
            return false;
        }
        boolean hasIntersection = prioritizedTasks.stream()
                .anyMatch(task -> isIntersection(task, taskToAdd));

        return !hasIntersection && prioritizedTasks.add(taskToAdd);
    }

    public boolean removeTask(Task taskToRemove) {
        if (taskToRemove.isTaskWithTime()) {
            return prioritizedTasks.remove(taskToRemove);
        }
        return false;
    }

    public boolean containsTask(Task taskToCheck) {
        return prioritizedTasks.contains(taskToCheck);
    }

    private boolean isIntersection(Task task1, Task task2) {
        LocalDateTime startTime1 = task1.getStartTime();
        LocalDateTime startTime2 = task2.getStartTime();
        LocalDateTime maxStartTime = startTime1.isAfter(startTime2) ? startTime1 : startTime2;

        LocalDateTime endTime1 = task1.getEndTime();
        LocalDateTime endTime2 = task2.getEndTime();
        LocalDateTime minEndTime = endTime1.isBefore(endTime2) ? endTime1 : endTime2;

        return maxStartTime.isBefore(minEndTime);
    }
}