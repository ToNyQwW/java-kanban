package service.interfaces;

import model.*;

import java.util.List;

public interface TaskManager {

    void addTask(Task task);

    void addSubTask(SubTask subTasks);

    void addEpicTask(EpicTask epicTask);

    boolean updateTask(Task task);

    boolean updateSubTask(SubTask subTasks);

    boolean updateEpicTask(EpicTask epicTask);

    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpicTask(int id);

    void clearTasksMap();

    void clearSubtasksMap();

    void clearEpicTasksMap();

    List<Task> getPrioritizedTasks();

    List<Task> getTasksList();

    List<SubTask> getSubTasksList();

    List<EpicTask> getEpicTasksList();

    List<SubTask> getSubTasksFromEpicTaskId(int id);

    Task getTask(int id);

    SubTask getSubTask(int id);

    EpicTask getEpicTask(int id);

    List<Task> getHistory();
}
