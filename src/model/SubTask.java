package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(int id, String name, String description, TaskStatus status,
                   LocalDateTime startTime, Duration duration, int epicId) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, TaskStatus status,
                   LocalDateTime startTime, Duration duration, int epicId) {
        this(DEFAULT_ID, name, description, status, startTime, duration, epicId);
    }

    public SubTask(int id, String name, String description, TaskStatus status, int epicId) {
        this(id, name, description, status, null, Duration.ZERO, epicId);
    }

    public SubTask(String name, String description, TaskStatus status, int epicId) {
        this(DEFAULT_ID, name, description, status, null, Duration.ZERO, epicId);
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
        return super.toString() + "," + getEpicId();
    }
}
