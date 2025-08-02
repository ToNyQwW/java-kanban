package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    public static final int DEFAULT_ID = -1;

    private int id;
    private String name;
    private String description;
    private TaskStatus status;
    private LocalDateTime startTime;
    private Duration duration;

    //оставил 4 конструктора на случаи, когда id не задается или не указываются параметры времени
    public Task(int id, String name, String description,
                TaskStatus status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(int id, String name, String description, TaskStatus status) {
        this(id, name, description, status, null, Duration.ZERO);
    }

    public Task(String name, String description, TaskStatus status, LocalDateTime startTime, Duration duration) {
        this(DEFAULT_ID, name, description, status, startTime, duration);
    }

    public Task(String name, String description, TaskStatus status) {
        this(DEFAULT_ID, name, description, status, null, Duration.ZERO);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null) {
            return null;
        }
        return startTime.plus(duration);
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public String startTimeToString() {
        return startTime != null ? startTime.toString() : " ";
    }

    public Long durationToLong() {
        return duration.toMinutes();
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%d",
                id,
                getTaskType(),
                name,
                description,
                status,
                startTimeToString(),
                durationToLong());
    }
}
