package service.interfaces;

import model.EpicTask;
import model.SubTask;
import model.Task;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    List<Task> getUnprioritizedTasks();

    List<Task> getTasksList();

    List<SubTask> getSubTasksList();

    List<EpicTask> getEpicTasksList();

    List<SubTask> getSubTasksFromEpicTaskId(int id);

    Optional<Task> getTask(int id);

    Optional<SubTask> getSubTask(int id);

    Optional<EpicTask> getEpicTask(int id);

    List<Task> getHistory();

    LocalDateTime getBaseTime();
}
