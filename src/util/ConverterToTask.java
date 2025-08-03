package util;

import model.*;
import service.exceptions.ConvertToTaskException;

public final class ConverterToTask {

    private static final int ID_INDEX = 0;
    private static final int TYPE_INDEX = 1;
    private static final int NAME_INDEX = 2;
    private static final int DESCRIPTION_INDEX = 3;
    private static final int STATUS_INDEX = 4;
    private static final int START_TIME_INDEX = 5;
    private static final int DURATION_INDEX = 6;
    private static final int EPIC_ID_INDEX = 7;

    private ConverterToTask() {
    }

    public static Task fromString(String value) throws ConvertToTaskException {

        if (value == null) {
            throw new ConvertToTaskException("value is null");
        }
        try {
            String[] taskString = value.split(",");
            Task result = null;
            int id = Integer.parseInt(taskString[ID_INDEX]);
            TaskStatus taskStatus = TaskStatus.valueOf(taskString[STATUS_INDEX]);
            switch (TaskType.valueOf(taskString[TYPE_INDEX])) {
                case TASK -> result = new Task(id, taskString[NAME_INDEX], taskString[DESCRIPTION_INDEX], taskStatus);

                case SUB_TASK -> result = new SubTask(id, taskString[NAME_INDEX], taskString[DESCRIPTION_INDEX],
                        taskStatus, Integer.parseInt(taskString[EPIC_ID_INDEX]));

                case EPIC_TASK -> result = new EpicTask(id, taskString[NAME_INDEX], taskString[DESCRIPTION_INDEX]);
            }
            return result;
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new ConvertToTaskException("string value have incorrect format", e);
        }
    }
}
