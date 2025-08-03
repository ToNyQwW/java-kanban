package service;

import model.EpicTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.interfaces.TaskManager;
import util.ConverterToTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static model.TaskStatus.NEW;
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

        task = new Task("Name", "Description", NEW);
        fileBackedTaskManager.addTask(task);

        epicTask = new EpicTask("Name", "Description");
        fileBackedTaskManager.addEpicTask(epicTask);

        subTask = new SubTask("Name", "Description", NEW, epicTask.getId());
        fileBackedTaskManager.addSubTask(subTask);
    }

    @Test
    void testFromString() {
        String testTask = "1,TASK,Name,Description,NEW, ,0";
        Task taskFromString = ConverterToTask.fromString(testTask);
        assertEquals(task, taskFromString);
        assertEquals(testTask, task.toString());

        String testSubTask = "3,SUB_TASK,Name,Description,NEW, ,0,2";
        Task subTaskFromString = ConverterToTask.fromString(testSubTask);
        assertEquals(subTask, subTaskFromString);
        assertEquals(testSubTask, subTask.toString());

        String testEpicTask = "2,EPIC_TASK,Name,Description,NEW, ,0";
        Task epicTaskFromString = ConverterToTask.fromString(testEpicTask);
        assertEquals(epicTask, epicTaskFromString);
        assertEquals(testEpicTask, epicTask.toString());
    }

    @Test
    void testSave() throws IOException {
        List<String> taskManagerFile = Files.readAllLines(tempFile);
        assertEquals("id,type,name,description,status,epic", taskManagerFile.get(0));
        assertEquals(task.toString(), taskManagerFile.get(1));
        assertEquals(epicTask.toString(), taskManagerFile.get(2));
        assertEquals(subTask.toString(), taskManagerFile.get(3));
    }

    @Test
    void testLoadFromFile() {
        FileBackedTaskManager loadedTaskManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(loadedTaskManager.getTasksList(), fileBackedTaskManager.getTasksList());
        assertEquals(loadedTaskManager.getSubTasksList(), fileBackedTaskManager.getSubTasksList());
        assertEquals(loadedTaskManager.getEpicTasksList(), fileBackedTaskManager.getEpicTasksList());
    }

    @Test
    void saveAndLoadEmptyFile_shouldReturnEmptyTaskManager() throws IOException {
        Path emptyFile = Files.createTempFile("test", ".txt");
        TaskManager taskManager = new FileBackedTaskManager(emptyFile);
        taskManager.addTask(task);
        taskManager.removeTask(task.getId());
        FileBackedTaskManager testManager = FileBackedTaskManager.loadFromFile(emptyFile);
        assertEquals(0, testManager.getTasksList().size());
    }
}