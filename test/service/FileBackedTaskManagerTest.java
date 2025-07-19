package service;

import model.EpicTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {

    private static FileBackedTaskManager fileBackedTaskManager;
    private static Path tempFile;
    private Task task;
    private SubTask subTask;
    private EpicTask epicTask;

    @BeforeAll
    static void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".txt");
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);
    }

    @BeforeEach
    void setUpEach() {
        task = new Task("Name", "Description");
        fileBackedTaskManager.addTask(task);

        epicTask = new EpicTask("Name", "Description");
        fileBackedTaskManager.addEpicTask(epicTask);

        subTask = new SubTask("Name", "Description", epicTask.getId());
        fileBackedTaskManager.addSubTask(subTask);
    }

    @AfterEach
    void afterEach() {
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void testToString() {
        String testTask = "1,TASK,Name,Description,NEW,";
        assertEquals(testTask, fileBackedTaskManager.toString(task));

        String testSubTask = "3,SUB_TASK,Name,Description,NEW,2";
        assertEquals(testSubTask, fileBackedTaskManager.toString(subTask));

        String testEpicTask = "2,EPIC_TASK,Name,Description,,";
        assertEquals(testEpicTask, fileBackedTaskManager.toString(epicTask));
    }

    @Test
    void testFromString() {
        String testTask = "1,TASK,Name,Description,NEW,";
        Task taskFromString = fileBackedTaskManager.fromString(testTask);
        assertEquals(task, taskFromString);
        assertEquals(testTask, fileBackedTaskManager.toString(task));

        String testSubTask = "3,SUB_TASK,Name,Description,NEW,2";
        Task subTaskFromString = fileBackedTaskManager.fromString(testSubTask);
        assertEquals(subTask, subTaskFromString);
        assertEquals(testSubTask, fileBackedTaskManager.toString(subTask));

        String testEpicTask = "2,EPIC_TASK,Name,Description,,";
        Task epicTaskFromString = fileBackedTaskManager.fromString(testEpicTask);
        assertEquals(epicTask, epicTaskFromString);
        assertEquals(testEpicTask, fileBackedTaskManager.toString(epicTask));
    }
}