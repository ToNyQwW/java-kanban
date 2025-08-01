package model;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(int id, String name, String description, TaskStatus status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status, int epicId) {
        super(-1, name, description, status);
        this.epicId = epicId;
    }

    public SubTask(int id, String name, String description, int epicId) {
        super(id, name, description, TaskStatus.NEW);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, int epicId) {
        super(-1, name, description, TaskStatus.NEW);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUB_TASK;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d",
                getId(),
                TaskType.SUB_TASK,
                getName(),
                getDescription(),
                getStatus(),
                getEpicId());
    }
}
