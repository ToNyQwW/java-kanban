package util;

import model.*;

public final class ConverterToTask {

    private ConverterToTask() {
    }

    public static Task fromString(String value) {
        String[] taskString = value.split(",");
        Task result = null;
        switch (TaskType.valueOf(taskString[1])) {
            case TASK -> result = new Task(Integer.parseInt(taskString[0]), taskString[2], taskString[3],
                    TaskStatus.valueOf(taskString[4]));

            case SUB_TASK -> result = new SubTask(Integer.parseInt(taskString[0]), taskString[2], taskString[3],
                    TaskStatus.valueOf(taskString[4]), Integer.parseInt(taskString[7]));

            case EPIC_TASK -> result = new EpicTask(Integer.parseInt(taskString[0]), taskString[2], taskString[3]);
        }
        return result;
    }
}
