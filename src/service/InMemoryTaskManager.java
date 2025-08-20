package service;

import model.EpicTask;
import model.SubTask;
import model.Task;
import model.TaskStatus;
import service.exceptions.NotFoundTaskException;
import service.interfaces.HistoryManager;
import service.interfaces.PriorityManager;
import service.interfaces.TaskManager;
import util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasksMap;
    private final Map<Integer, SubTask> subtasksMap;
    private final Map<Integer, EpicTask> epicTasksMap;
    private final Map<Integer, Task> unPriorityTaskMap;

    private final HistoryManager historyManager;
    private final PriorityManager priorityManager;

    private int id;

    public InMemoryTaskManager() {
        tasksMap = new HashMap<>();
        subtasksMap = new HashMap<>();
        epicTasksMap = new HashMap<>();
        unPriorityTaskMap = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        priorityManager = Managers.getDefaultPriority();
    }

    private void increaseId() {
        id++;
    }

    /*
        Раздел add
     */
    @Override
    public void addTask(Task task) {
        increaseId();
        task.setId(id);
        tasksMap.put(id, task);
        if (!priorityManager.addTask(task)) {
            unPriorityTaskMap.put(id, task);
        }
    }

    @Override
    public void addSubTask(SubTask subTask) {
        increaseId();
        subTask.setId(id);
        subtasksMap.put(id, subTask);

        EpicTask epicTask = epicTasksMap.get(subTask.getEpicId());
        epicTask.put(subTask);

        updateEpicTaskStatus(epicTask);
        updateEpicTaskTime(epicTask);
        if (!priorityManager.addTask(subTask)) {
            unPriorityTaskMap.put(id, subTask);
        }
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        increaseId();
        epicTask.setId(id);
        epicTasksMap.put(id, epicTask);
    }

    private void addInHistory(Task task) {
        if (task != null) {
            historyManager.add(task);
        }
    }

    /*
        Раздел update
    */
    @Override
    public boolean updateTask(Task task) {
        if (!tasksMap.containsKey(task.getId())) {
            return false;
        }
        tasksMap.put(task.getId(), task);
        if (priorityManager.removeTask(task)) {
            priorityManager.addTask(task);
            updatePrioritizedTasks();
        }
        return true;
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
        if (priorityManager.removeTask(subTasks)) {
            priorityManager.addTask(subTasks);
            updatePrioritizedTasks();
        }
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
                .filter(subTask -> subTask.isTaskWithTime() && subTask.getStatus() != TaskStatus.DONE)
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

    //Если удалена/изменена приоритетная задача, то ищем место для другой вне приоритета
    private void updatePrioritizedTasks() {
        unPriorityTaskMap.values().removeIf(priorityManager::addTask);
    }

    /*
        Раздел remove
    */
    @Override
    public void removeTask(int id) throws NotFoundTaskException {
        Task taskToRemove = tasksMap.remove(id);

        if (taskToRemove == null) {
            throw new NotFoundTaskException("Task with id " + id + " not found");
        }
        historyManager.remove(id);

        if (priorityManager.removeTask(taskToRemove)) {
            updatePrioritizedTasks();
        }
        unPriorityTaskMap.remove(id);
    }

    @Override
    public void removeSubTask(int id) throws NotFoundTaskException {
        SubTask subTaskToRemove = subtasksMap.remove(id);

        if (subTaskToRemove == null) {
            throw new NotFoundTaskException("SubTask with id " + id + " not found");
        }
        EpicTask epicTask = epicTasksMap.get(subTaskToRemove.getEpicId());
        epicTask.getSubInEpic().remove(id);
        updateEpicTaskStatus(epicTask);
        updateEpicTaskTime(epicTask);
        historyManager.remove(id);

        if (priorityManager.removeTask(subTaskToRemove)) {
            updatePrioritizedTasks();
        }
        unPriorityTaskMap.remove(id);
    }

    // считаю, что без эпика подзадачи не существуют
    @Override
    public void removeEpicTask(int id) throws NotFoundTaskException {
        EpicTask epicTaskToRemove = epicTasksMap.remove(id);

        if (epicTaskToRemove == null) {
            throw new NotFoundTaskException("EpicTask with id " + id + " not found");
        }
        subtasksMap.values().stream().filter(subTask -> subTask.getEpicId() == id)
                .forEach(subTask -> {
                    historyManager.remove(subTask.getId());
                    if (priorityManager.removeTask(subTask)) {
                        updatePrioritizedTasks();
                    }
                });
        subtasksMap.values().removeIf(subTask -> subTask.getEpicId() == id);
        historyManager.remove(id);
    }

    /*
        Раздел clear
    */
    @Override
    public void clearTasksMap() {
        tasksMap.values().forEach(task -> {
            historyManager.remove(task.getId());
            if (priorityManager.removeTask(task)) {
                updatePrioritizedTasks();
            }
            unPriorityTaskMap.remove(task.getId());
        });
        tasksMap.clear();
    }

    @Override
    public void clearSubtasksMap() {
        subtasksMap.values().forEach(subTask -> {
            historyManager.remove(subTask.getId());
            if (priorityManager.removeTask(subTask)) {
                updatePrioritizedTasks();
            }
            unPriorityTaskMap.remove(subTask.getId());
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
        subtasksMap.values().forEach(subTask -> {
            historyManager.remove(subTask.getId());
            if (priorityManager.removeTask(subTask)) {
                updatePrioritizedTasks();
            }
            unPriorityTaskMap.remove(subTask.getId());
        });
        subtasksMap.clear();
        epicTasksMap.clear();
    }

    /*
        Раздел get
    */
    @Override
    public Optional<Task> getTask(int id) {
        Task task = tasksMap.get(id);
        addInHistory(task);
        return Optional.ofNullable(task);
    }

    @Override
    public Optional<SubTask> getSubTask(int id) {
        SubTask subTask = subtasksMap.get(id);
        addInHistory(subTask);
        return Optional.ofNullable(subTask);
    }

    @Override
    public Optional<EpicTask> getEpicTask(int id) {
        EpicTask epicTask = epicTasksMap.get(id);
        addInHistory(epicTask);
        return Optional.ofNullable(epicTask);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return priorityManager.getPrioritizedTasks();
    }

    public List<Task> getUnprioritizedTasks() {
        return new ArrayList<>(unPriorityTaskMap.values());
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
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public LocalDateTime getBaseTime() {
        return priorityManager.getBaseTime();
    }
}
