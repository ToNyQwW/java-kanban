package service;

import model.EpicTask;
import model.SubTask;
import model.Task;
import service.exceptions.ManagerLoadException;
import service.exceptions.ManagerSaveException;
import util.ConverterToTask;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path saveFile;

    public FileBackedTaskManager(Path saveFile) {
        super();
        this.saveFile = saveFile;
    }

    public static FileBackedTaskManager loadFromFile(Path file) throws ManagerLoadException {
        FileBackedTaskManager result = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            List<String> taskManagerFile = new ArrayList<>();
            reader.readLine(); // пропускаем первую строку с полями задач
            reader.lines().forEach(taskManagerFile::add);
            if (taskManagerFile.isEmpty()) {
                return result;
            }
            for (String taskToAdd : taskManagerFile) {
                Task task = ConverterToTask.fromString(taskToAdd);
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

    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.toFile(), UTF_8))) {
            writer.write("id,type,name,description,status,epic\n");
            for (Task task : getTasksList()) {
                writer.write(task + "\n");
            }
            for (EpicTask epicTask : getEpicTasksList()) {
                writer.write(epicTask + "\n");
            }
            for (SubTask subTask : getSubTasksList()) {
                writer.write(subTask + "\n");
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