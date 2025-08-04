package util;

import model.EpicTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.file.Files;

import static model.TaskStatus.DONE;
import static model.TaskStatus.IN_PROGRESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConverterToTaskTest {

    private static TaskManager taskManager;
    private static Task task;
    private static SubTask subTask;
    private static EpicTask epicTask;


    @BeforeAll
    static void setUp() throws IOException {
        taskManager = Managers.getDefault(Files.createTempFile("Test", ".txt"));
        task = new Task("Name", "Description", DONE);
        taskManager.addTask(task);
        epicTask = new EpicTask("Name", "Description");
        taskManager.addEpicTask(epicTask);
        subTask = new SubTask("Name", "Description", IN_PROGRESS, epicTask.getId());
        taskManager.addSubTask(subTask);
    }

    @Test
    void ShouldCorrectlyConvertToTask() throws IOException {
        String taskString = task.toString();
        assertEquals(ConverterToTask.fromString(taskString), task);
    }

    @Test
    void ShouldCorrectlyConvertToSubTask() throws IOException {
        String subTaskString = subTask.toString();
        assertEquals(ConverterToTask.fromString(subTaskString), subTask);
    }

    @Test
    void ShouldCorrectlyConvertToEpicTask() throws IOException {
        String epicTaskString = epicTask.toString();
        assertEquals(ConverterToTask.fromString(epicTaskString), epicTask);
    }

}