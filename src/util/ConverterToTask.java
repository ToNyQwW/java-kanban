package util;

import model.*;
import service.exceptions.ConvertToTaskException;

import java.time.Duration;
import java.time.LocalDateTime;

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
            int id = Integer.parseInt(taskString[ID_INDEX]);
            TaskType type = TaskType.valueOf(taskString[TYPE_INDEX]);
            String name = taskString[NAME_INDEX];
            String description = taskString[DESCRIPTION_INDEX];
            TaskStatus status = TaskStatus.valueOf(taskString[STATUS_INDEX]);
            LocalDateTime startTime = taskString[START_TIME_INDEX].equals(" ") ?
                    null : LocalDateTime.parse(taskString[START_TIME_INDEX]);
            Duration duration = taskString[DURATION_INDEX].equals("0") ?
                    Duration.ZERO : Duration.ofMinutes(Long.parseLong(taskString[DURATION_INDEX]));

            if (type == TaskType.EPIC_TASK) {
                return new EpicTask(id, name, description, status, startTime, duration);
            } else {
                if (type == TaskType.TASK) {
                    return new Task(id, name, description, status, startTime, duration);
                } else {
                    int epicId = Integer.parseInt(taskString[EPIC_ID_INDEX]);
                    return new SubTask(id, name, description, status, startTime, duration, epicId);
                }
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            throw new ConvertToTaskException("string value have incorrect format", e);
        }
    }
}
