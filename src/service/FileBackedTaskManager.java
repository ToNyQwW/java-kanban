package service;

import model.*;
import service.exceptions.ManagerLoadException;
import service.exceptions.ManagerSaveException;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path saveFile;

    public FileBackedTaskManager(Path saveFile) {
        super();
        this.saveFile = saveFile;
    }

    public static FileBackedTaskManager loadFromFile(Path file) throws ManagerLoadException {
        FileBackedTaskManager result = new FileBackedTaskManager(file);
        try {
            List<String> taskManagerFile = Files.readAllLines(file);
            if (taskManagerFile.isEmpty() || taskManagerFile.size() == 1) {
                return result;
            }
            for (int i = 1; i < taskManagerFile.size(); i++) {
                Task task = result.fromString(taskManagerFile.get(i));
                switch (task.getTaskType()) {
                    case TASK -> result.addTask(task);
                    case SUB_TASK -> result.addSubTask((SubTask) task);
                    case EPIC_TASK -> result.addEpicTask((EpicTask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerLoadException("ManagerLoadException", e);
        }
        return result;
    }

    public Task fromString(String value) {
        String[] taskString = value.split(",");
        Task result = null;
        switch (TaskType.valueOf(taskString[1])) {
            case TASK -> result = new Task(Integer.parseInt(taskString[0]), taskString[2], taskString[3],
                    TaskStatus.valueOf(taskString[4]));

            case SUB_TASK -> result = new SubTask(Integer.parseInt(taskString[0]), taskString[2], taskString[3],
                    TaskStatus.valueOf(taskString[4]), Integer.parseInt(taskString[5]));

            case EPIC_TASK -> result = new EpicTask(Integer.parseInt(taskString[0]), taskString[2], taskString[3]);
        }
        return result;
    }

    private void save() throws ManagerSaveException {
        try {
            Files.writeString(saveFile, "id,type,name,description,status,epic\n", UTF_8, CREATE, TRUNCATE_EXISTING);
            for (Task task : getTasksList()) {
                Files.writeString(saveFile, task + "\n", UTF_8, APPEND);
            }
            for (EpicTask epicTask : getEpicTasksList()) {
                Files.writeString(saveFile, epicTask + "\n", UTF_8, APPEND);
            }
            for (SubTask subTask : getSubTasksList()) {
                Files.writeString(saveFile, subTask + "\n", UTF_8, APPEND);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("ManagerSaveException", e);
        }
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        super.addEpicTask(epicTask);
        save();
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public boolean updateSubTask(SubTask subTasks) {
        boolean result = super.updateSubTask(subTasks);
        save();
        return result;
    }

    @Override
    public boolean updateEpicTask(EpicTask epicTask) {
        boolean result = super.updateEpicTask(epicTask);
        save();
        return result;
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpicTask(int id) {
        super.removeEpicTask(id);
        save();
    }

    @Override
    public void clearTasksMap() {
        super.clearTasksMap();
        save();
    }

    @Override
    public void clearSubtasksMap() {
        super.clearSubtasksMap();
        save();
    }

    @Override
    public void clearEpicTasksMap() {
        super.clearEpicTasksMap();
        save();
    }
}