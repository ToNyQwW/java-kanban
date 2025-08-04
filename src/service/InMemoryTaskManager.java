package service;

import model.EpicTask;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;
import util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasksMap;
    private final Map<Integer, SubTask> subtasksMap;
    private final Map<Integer, EpicTask> epicTasksMap;
    private final Set<Task> prioritizedTasks;

    private final HistoryManager historyManager;

    private int id;

    public InMemoryTaskManager() {
        tasksMap = new HashMap<>();
        subtasksMap = new HashMap<>();
        epicTasksMap = new HashMap<>();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        historyManager = Managers.getDefaultHistory();
    }

    private void increaseId() {
        id++;
    }


    @Override
    public void addTask(Task task) {
        increaseId();
        task.setId(id);
        tasksMap.put(task.getId(), task);
        addTaskInPrioritizedTasks(task);
    }

    @Override
    public void addSubTask(SubTask subTask) {
        increaseId();
        subTask.setId(id);
        subtasksMap.put(subTask.getId(), subTask);

        EpicTask epicTask = epicTasksMap.get(subTask.getEpicId());
        epicTask.put(subTask);

        updateEpicTaskStatus(epicTask);
        updateEpicTaskTime(epicTask);
        addTaskInPrioritizedTasks(subTask);
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        increaseId();
        epicTask.setId(id);
        epicTasksMap.put(epicTask.getId(), epicTask);
    }

    private boolean addTaskInPrioritizedTasks(Task taskToAdd) {
        if (taskToAdd.getStartTime() == null || taskToAdd.getDuration() == Duration.ZERO) {
            return false;
        }
        boolean hasIntersection = prioritizedTasks.stream()
                .anyMatch(task -> isIntersection(task, taskToAdd));

        return !hasIntersection && prioritizedTasks.add(taskToAdd);
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

    //Если удалена приоритетная задача, то могло освободится место для другой вне списка
    private void updatePrioritizedTasks() {
        getTaskForUpdatePrioritizedTasks()
                .forEach(this::addTaskInPrioritizedTasks);
    }

    private List<Task> getTaskForUpdatePrioritizedTasks() {
        return Stream.concat(
                        tasksMap.values().stream(),
                        subtasksMap.values().stream())
                .filter(task -> !prioritizedTasks.contains(task))
                .toList();
    }

    @Override
    public boolean updateTask(Task task) {
        if (!tasksMap.containsKey(task.getId())) {
            return false;
        }
        prioritizedTasks.remove(task);
        addTaskInPrioritizedTasks(task);
        return tasksMap.put(task.getId(), task) != null;
    }

    @Override
    public boolean updateSubTask(SubTask subTasks) {
        if (!subtasksMap.containsKey(subTasks.getId())) {
            return false;
        }
        subtasksMap.put(subTasks.getId(), subTasks);

        EpicTask epicTask = epicTasksMap.get(subTasks.getEpicId());
        epicTask.put(subTasks);

        updateEpicTaskStatus(epicTask);
        updateEpicTaskTime(epicTask);
        prioritizedTasks.remove(subTasks);
        addTaskInPrioritizedTasks(subTasks);
        return true;
    }

    // сделал так, что эпик не может менять себе статус через конструктор
    @Override
    public boolean updateEpicTask(EpicTask epicTask) {
        if (!epicTasksMap.containsKey(epicTask.getId())) {
            return false;
        }
        epicTasksMap.put(epicTask.getId(), epicTask);

        return true;
    }

    private void updateEpicTaskStatus(EpicTask epicTask) {
        // если удалили все подзадачи, то считаю, что эпик выполнен
        if (epicTask.getSubInEpic().isEmpty()) {
            epicTask.setStatus(TaskStatus.DONE);
            return;
        }
        // иначе по состоянию подзадач меняю статус эпика
        Map<TaskStatus, Long> statusCountMap = epicTask.getSubInEpic().values().stream()
                .collect(Collectors.groupingBy(Task::getStatus, Collectors.counting()));

        if (statusCountMap.size() == 1) {
            epicTask.setStatus(statusCountMap.keySet().stream().findFirst().get());
        } else {
            epicTask.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void updateEpicTaskTime(EpicTask epicTask) {
        //обнуляю данные в Эпике в ситуации, когда удалили все подзадачи методом clearSubtasksMap
        if (epicTask.getSubInEpic().isEmpty()) {
            setNullTimeInEpicTask(epicTask);
            return;
        }
        List<SubTask> sortedSubTasks = epicTask.getSubInEpic().values().stream()
                .filter(subTask -> subTask.getStartTime() != null)
                .sorted(Comparator.comparing(Task::getStartTime)).toList();
        // если у эпика уже были произведены расчеты и удалили подзадачу, но оставшиеся подзадачи без времени
        if (sortedSubTasks.isEmpty()) {
            setNullTimeInEpicTask(epicTask);
        } else {
            epicTask.setStartTime(sortedSubTasks.getFirst().getStartTime());

            epicTask.setEndTime(sortedSubTasks.getLast().getEndTime());

            epicTask.setDuration(sortedSubTasks.stream()
                    .map(Task::getDuration)
                    .reduce(Duration.ZERO, Duration::plus));
        }
    }

    private void setNullTimeInEpicTask(EpicTask epicTask) {
        epicTask.setStartTime(null);
        epicTask.setDuration(Duration.ZERO);
        epicTask.setEndTime(null);
    }

    @Override
    public void removeTask(int id) {
        if (prioritizedTasks.remove(tasksMap.get(id))) {
            updatePrioritizedTasks();
        }
        tasksMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        if (prioritizedTasks.remove(subtasksMap.get(id))) {
            updatePrioritizedTasks();
        }
        EpicTask epicTask = epicTasksMap.get(subtasksMap.get(id).getEpicId());
        epicTask.getSubInEpic().remove(id);

        updateEpicTaskStatus(epicTask);
        updateEpicTaskTime(epicTask);
        subtasksMap.remove(id);
        historyManager.remove(id);
    }

    // считаю, что без эпика подзадачи не существуют
    @Override
    public void removeEpicTask(int id) {
        subtasksMap.values().stream().filter(subTask -> subTask.getEpicId() == id)
                .forEach(subTask -> historyManager.remove(subTask.getId()));
        subtasksMap.values().removeIf(subTask -> subTask.getEpicId() == id);
        epicTasksMap.get(id).getSubInEpic().clear();
        epicTasksMap.remove(id);
        historyManager.remove(id);
    }


    @Override
    public void clearTasksMap() {
        tasksMap.values().forEach(task -> {
            historyManager.remove(task.getId());
            if (prioritizedTasks.remove(task)) {
                updatePrioritizedTasks();
            }
        });
        tasksMap.clear();
    }

    @Override
    public void clearSubtasksMap() {
        subtasksMap.values().forEach(subTask -> {
            historyManager.remove(subTask.getId());
            if (prioritizedTasks.remove(subTask)) {
                updatePrioritizedTasks();
            }
        });
        subtasksMap.clear();
        for (EpicTask epicTask : epicTasksMap.values()) {
            epicTask.getSubInEpic().clear();
            updateEpicTaskStatus(epicTask);
            updateEpicTaskTime(epicTask);
        }
    }

    @Override
    public void clearEpicTasksMap() {
        epicTasksMap.values().forEach(epicTask -> historyManager.remove(epicTask.getId()));
        subtasksMap.values().forEach(subTask -> historyManager.remove(subTask.getId()));
        subtasksMap.clear();
        epicTasksMap.clear();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getTasksList() {
        return tasksMap.values().stream().toList();
    }

    @Override
    public List<SubTask> getSubTasksList() {
        return subtasksMap.values().stream().toList();
    }

    @Override
    public List<EpicTask> getEpicTasksList() {
        return epicTasksMap.values().stream().toList();
    }

    @Override
    public List<SubTask> getSubTasksFromEpicTaskId(int id) {
        return epicTasksMap.get(id).getSubInEpic().values().stream().toList();
    }


    @Override
    public Task getTask(int id) {
        Task task = tasksMap.get(id);
        addInHistory(task);
        return task;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask subTask = subtasksMap.get(id);
        addInHistory(subTask);
        return subTask;
    }

    @Override
    public EpicTask getEpicTask(int id) {
        EpicTask epicTask = epicTasksMap.get(id);
        addInHistory(epicTask);
        return epicTask;
    }


    private void addInHistory(Task task) {
        if (task != null) {
            historyManager.add(task);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }
}
