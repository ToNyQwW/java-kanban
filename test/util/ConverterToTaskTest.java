package util;

import model.EpicTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.exceptions.ConvertToTaskException;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static model.TaskStatus.DONE;
import static model.TaskStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ConverterToTaskTest {

    private static TaskManager taskManager;
    private static Task task;
    private static SubTask subTask;
    private static EpicTask epicTask;


    @BeforeAll
    static void setUp() throws IOException {
        taskManager = Managers.getDefault(Files.createTempFile("Test", ".txt"));
        LocalDateTime time = taskManager.getBaseTime();
        Duration duration = Duration.ofMinutes(30);
        task = new Task("Name", "Description", DONE, time, duration);
        taskManager.addTask(task);
        epicTask = new EpicTask("Name", "Description");
        taskManager.addEpicTask(epicTask);
        subTask = new SubTask("Name", "Description", IN_PROGRESS, time, duration, epicTask.getId());
        taskManager.addSubTask(subTask);
    }

    @Test
    void ShouldCorrectlyConvertToTask() {
        String taskString = task.toString();
        assertEquals(ConverterToTask.fromString(taskString), task);
    }

    @Test
    void ShouldCorrectlyConvertToSubTask() {
        String subTaskString = subTask.toString();
        assertEquals(ConverterToTask.fromString(subTaskString), subTask);
    }

    @Test
    void ShouldCorrectlyConvertToEpicTask() {
        String epicTaskString = epicTask.toString();
        assertEquals(ConverterToTask.fromString(epicTaskString), epicTask);
    }

    @Test
    public void testExceptions() {
        assertThrows(ConvertToTaskException.class, () -> ConverterToTask.fromString(null));
        String test = "asd,EPIC_TASK,EpicName,EpicDescription,NEW, ,0";
        assertThrows(ConvertToTaskException.class, () -> ConverterToTask.fromString(test));
        String test2 = "2,SUB_TASK,SubName,NEW, ,0,1";
        assertThrows(ConvertToTaskException.class, () -> ConverterToTask.fromString(test2));

    }

}