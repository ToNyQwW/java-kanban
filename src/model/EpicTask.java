package model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EpicTask extends Task {

    private final Map<Integer, SubTask> subInEpic = new HashMap<>();
    private LocalDateTime endTime;


    public EpicTask(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    public EpicTask(String name, String description) {
        this(DEFAULT_ID, name, description);
    }

    public Map<Integer, SubTask> getSubInEpic() {
        return subInEpic;
    }

    public void put(SubTask subTasks) {
        subInEpic.put(subTasks.getId(), subTasks);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC_TASK;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }
}
