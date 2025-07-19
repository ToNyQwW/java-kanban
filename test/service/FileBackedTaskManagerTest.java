package service;

import model.EpicTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {

    private static FileBackedTaskManager fileBackedTaskManager;
    private static Path tempFile;
    private static Task task;
    private static SubTask subTask;
    private static EpicTask epicTask;

    @BeforeAll
    static void setUp() throws IOException {
        tempFile = Files.createTempFile("test", ".txt");
        fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        task = new Task("Name", "Description");
        fileBackedTaskManager.addTask(task);

        epicTask = new EpicTask("Name", "Description");
        fileBackedTaskManager.addEpicTask(epicTask);

        subTask = new SubTask("Name", "Description", epicTask.getId());
        fileBackedTaskManager.addSubTask(subTask);
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

    @Test
    void testSave() throws IOException {
        List<String> taskManagerFile = Files.readAllLines(tempFile);
        assertEquals("id,type,name,description,status,epic", taskManagerFile.get(0));
        assertEquals(fileBackedTaskManager.toString(task), taskManagerFile.get(1));
        assertEquals(fileBackedTaskManager.toString(epicTask), taskManagerFile.get(2));
        assertEquals(fileBackedTaskManager.toString(subTask), taskManagerFile.get(3));
    }
}