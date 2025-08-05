package service;

import model.Task;
import service.interfaces.PriorityManager;
import util.TaskTimeIntersectionChecker;

import java.util.*;

public class PriorityTaskManager implements PriorityManager {

    private final Set<Task> prioritizedTasks;
    private final TaskTimeIntersectionChecker timeChecker;

    public PriorityTaskManager() {
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        this.timeChecker = new TaskTimeIntersectionChecker();

    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean addTask(Task taskToAdd) {
        if (!taskToAdd.isTaskWithTime()) {
            return false;
        }
        if (timeChecker.addIntervalFromTask(taskToAdd)) {
            prioritizedTasks.add(taskToAdd);
            return true;
        }
        return false;
    }

    public boolean removeTask(Task taskToRemove) {
        if (!taskToRemove.isTaskWithTime()) {
            return false;
        }
        if (timeChecker.removeIntervalFromTask(taskToRemove)) {
            return prioritizedTasks.remove(taskToRemove);
        }
        return false;
    }

    public boolean containsTask(Task taskToCheck) {
        return prioritizedTasks.contains(taskToCheck);
    }

    //старая реализация, до выполнения доп задания
//    private boolean isIntersection(Task task1, Task task2) {
//        LocalDateTime startTime1 = task1.getStartTime();
//        LocalDateTime startTime2 = task2.getStartTime();
//        LocalDateTime maxStartTime = startTime1.isAfter(startTime2) ? startTime1 : startTime2;
//
//        LocalDateTime endTime1 = task1.getEndTime();
//        LocalDateTime endTime2 = task2.getEndTime();
//        LocalDateTime minEndTime = endTime1.isBefore(endTime2) ? endTime1 : endTime2;
//
//        return maxStartTime.isBefore(minEndTime);
//    }
}