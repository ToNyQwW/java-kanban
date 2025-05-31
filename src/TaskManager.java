import taskType.*;

import java.util.stream.Collectors;
import java.util.*;

public class TaskManager {

    private final Map<Integer, Task> tasksMap;
    private final Map<Integer, SubTask> subtasksMap;
    private final Map<Integer, EpicTask> epicTasksMap;

    private int id;

    public TaskManager() {
        tasksMap = new HashMap<>();
        subtasksMap = new HashMap<>();
        epicTasksMap = new HashMap<>();
        id = 0;
    }

    private void increaseId() {
        id++;
    }


    public void addTask(Task task) {
        increaseId();
        task.setId(id);
        tasksMap.put(task.getId(), task);
    }

    public void addSubTask(SubTask subTasks) {
        increaseId();
        subTasks.setId(id);
        subtasksMap.put(subTasks.getId(), subTasks);

        EpicTask epicTask = getEpicTask(subTasks.getEpicId());
        epicTask.addSubTask(subTasks);

        updateEpicTaskStatus(epicTask);
    }

    public void addEpicTask(EpicTask epicTask) {
        increaseId();
        epicTask.setId(id);
        epicTasksMap.put(epicTask.getId(), epicTask);
    }


    public boolean updateTask(Task task) {
        if (!tasksMap.containsKey(task.getId())) {
            return false;
        }
        return tasksMap.put(task.getId(), task) != null;
    }

    public boolean updateSubTask(SubTask subTasks) {
        if (!subtasksMap.containsKey(subTasks.getId())) {
            return false;
        }
        subtasksMap.put(subTasks.getId(), subTasks);

        EpicTask epicTask = getEpicTask(subTasks.getEpicId());
        epicTask.addSubTask(subTasks);

        updateEpicTaskStatus(epicTask);
        return true;
    }

    // сделал так, что эпик не может менять себе статус через конструктор
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


    public Task removeTask(int id) {
        return tasksMap.remove(id);
    }

    public SubTask removeSubTask(int id) {
        int epicId = getSubTask(id).getEpicId();
        getEpicTask(epicId).getSubInEpic().remove(id);

        updateEpicTaskStatus(getEpicTask(epicId));
        return subtasksMap.remove(id);
    }

    // считаю, что без эпика подзадачи не существуют
    public EpicTask removeEpicTasksMap(int id) {
        subtasksMap.values().removeIf(subTask -> subTask.getEpicId() == id);
        getEpicTask(id).getSubInEpic().clear();
        return epicTasksMap.remove(id);
    }


    public void clearTasksMap() {
        tasksMap.clear();
    }

    public void clearSubtasksMap() {
        for (EpicTask epicTask : epicTasksMap.values()) {
            epicTask.getSubInEpic().clear();
        }
        subtasksMap.clear();
    }

    public void clearEpicTasksMap() {
        clearSubtasksMap();
        epicTasksMap.clear();
    }


    public Collection<Task> getTasksList() {
        return tasksMap.values().stream().toList();
    }

    public Collection<SubTask> getSubTasksList() {
        return subtasksMap.values().stream().toList();
    }

    public List<EpicTask> getEpicTasksList() {
        return epicTasksMap.values().stream().toList();
    }

    public Map<Integer, SubTask> getSubTasksFromEpicTask(EpicTask epicTask) {
        return epicTask.getSubInEpic();
    }


    public Task getTask(int id) {
        return tasksMap.get(id);
    }

    public SubTask getSubTask(int id) {
        return subtasksMap.get(id);
    }

    public EpicTask getEpicTask(int id) {
        return epicTasksMap.get(id);
    }
}
