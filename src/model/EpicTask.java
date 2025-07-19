package model;

import java.util.HashMap;
import java.util.Map;

public class EpicTask extends Task {

    private final Map<Integer, SubTask> subInEpic = new HashMap<>();


    public EpicTask(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    public EpicTask(String name, String description) {
        super(-1, name, description, TaskStatus.NEW);
    }


    public Map<Integer, SubTask> getSubInEpic() {
        return subInEpic;
    }

    public void put(SubTask subTasks) {
        subInEpic.put(subTasks.getId(), subTasks);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,", getId(), TaskType.EPIC_TASK,
                getName(), getDescription(), getStatus());
    }
}
