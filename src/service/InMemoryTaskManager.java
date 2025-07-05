package service;

import model.*;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;
import util.Managers;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {

    private final Map<Integer, Task> tasksMap;
    private final Map<Integer, SubTask> subtasksMap;
    private final Map<Integer, EpicTask> epicTasksMap;

    private final HistoryManager historyManager;

    private int id;

    public InMemoryTaskManager() {
        tasksMap = new HashMap<>();
        subtasksMap = new HashMap<>();
        epicTasksMap = new HashMap<>();
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
    }

    @Override
    public void addSubTask(SubTask subTask) {
        increaseId();
        subTask.setId(id);
        subtasksMap.put(subTask.getId(), subTask);

        EpicTask epicTask = epicTasksMap.get(subTask.getEpicId());
        epicTask.put(subTask);

        updateEpicTaskStatus(epicTask);
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        increaseId();
        epicTask.setId(id);
        epicTasksMap.put(epicTask.getId(), epicTask);
    }


    @Override
    public boolean updateTask(Task task) {
        if (!tasksMap.containsKey(task.getId())) {
            return false;
        }
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


    @Override
    public void removeTask(int id) {
        tasksMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeSubTask(int id) {
        EpicTask epicTask = epicTasksMap.get(getSubTask(id).getEpicId());
        epicTask.getSubInEpic().remove(id);

        updateEpicTaskStatus(epicTask);
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
        tasksMap.clear();
    }

    @Override
    public void clearSubtasksMap() {
        subtasksMap.clear();
        for (EpicTask epicTask : epicTasksMap.values()) {
            epicTask.getSubInEpic().clear();
            updateEpicTaskStatus(epicTask);
        }
    }

    @Override
    public void clearEpicTasksMap() {
        subtasksMap.clear();
        epicTasksMap.clear();
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
