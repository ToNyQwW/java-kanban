package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EpicTaskTest {

    private static EpicTask epicTask;
    private static SubTask subTask;

    @BeforeAll
    static void setUp() {
        epicTask = new EpicTask(1, "EpicName", "EpicDescription");
        subTask = new SubTask(2, "SubName", "SubDescription", TaskStatus.NEW, epicTask.getId());

    }

    @Test
    void putSubTaskInEpicAndGetSubInEpicMap() {
        epicTask.put(subTask);
        Map<Integer, SubTask> subInEpic = epicTask.getSubInEpic();
        assertNotNull(subInEpic);
    }


    @Test
    void checkGetters() {
        assertEquals(1, epicTask.getId());
        assertEquals("EpicName", epicTask.getName());
        assertEquals("EpicDescription", epicTask.getDescription());
        assertEquals(TaskStatus.NEW, epicTask.getStatus());
    }

    @Test
    void checkEqualsEpicTasks() {
        assertEquals(epicTask, epicTask);
        EpicTask testTask = new EpicTask(epicTask.getId(), "", "");
        assertEquals(epicTask, testTask);
    }

    @Test
    void testToString() {
        String test = "1,EPIC_TASK,EpicName,EpicDescription, ,";
        assertEquals(test, epicTask.toString());
    }
}