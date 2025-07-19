package service;

import model.*;

import java.nio.file.Path;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Path saveFile;

    public FileBackedTaskManager(Path saveFile) {
        super();
        this.saveFile = saveFile;
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

    private void save() {

    }

    public String toString(Task task){
        String result;
        if(task instanceof SubTask sub){
            result = String.format("%d,%s,%s,%s,%s,%d",
                    sub.getId(),
                    TaskType.SUB_TASK,
                    sub.getName(),
                    sub.getDescription(),
                    sub.getStatus(),
                    sub.getEpicId());
        }else if(task instanceof EpicTask epic){
            result = String.format("%d,%s,%s,%s,,",
                    epic.getId(),
                    TaskType.EPIC_TASK,
                    epic.getName(),
                    epic.getDescription());
        }else {
            result = String.format("%d,%s,%s,%s,%s,",
                    task.getId(),
                    TaskType.TASK,
                    task.getName(),
                    task.getDescription(),
                    task.getStatus());
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
}
