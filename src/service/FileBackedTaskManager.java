package service;

import model.EpicTask;
import model.SubTask;
import model.Task;
import service.exceptions.ConvertToTaskException;
import service.exceptions.ManagerLoadException;
import service.exceptions.ManagerSaveException;
import util.ConverterToTask;

import java.io.*;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.TreeSet;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String CSV_HEADER = "id,type,name,description,status,startTime,duration,epicId";

    private final Path saveFile;

    public FileBackedTaskManager(Path saveFile) {
        super();
        this.saveFile = saveFile;
    }

    public static FileBackedTaskManager loadFromFile(Path file) throws ManagerLoadException {
        FileBackedTaskManager result = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file.toFile()))) {
            if (!reader.readLine().equals(CSV_HEADER)) {
                throw new ManagerLoadException("CSV header not correct");
            }
            TreeSet<String> taskManagerFile = new TreeSet<>(); // для сортировки по id
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
        } catch (IOException | ConvertToTaskException e) {
            throw new ManagerLoadException("ManagerLoadException", e);
        }
        return result;
    }

    //сортирую задачу по id перед сохранением, чтоб не было проблем при загрузке их из файла
    private void save() throws ManagerSaveException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.toFile(), UTF_8))) {
            writer.write(CSV_HEADER + "\n");
            for (Task task : getSortedTasks()) {
                writer.write(task.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("ManagerSaveException", e);
        }
    }

    private TreeSet<Task> getSortedTasks() {
        TreeSet<Task> result = new TreeSet<>(Comparator.comparingInt(Task::getId));
        result.addAll(getTasksList());
        result.addAll(getEpicTasksList());
        result.addAll(getSubTasksList());
        return result;
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