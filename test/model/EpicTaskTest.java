package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EpicTaskTest {

    private static EpicTask epicTask;
    private static SubTask subTask;

    @BeforeAll
    static void setUp() {
        epicTask = new EpicTask(1, "EpicName", "EpicDescription");
        subTask = new SubTask(2, "SubName", "SubDescription", NEW, epicTask.getId());

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
        assertEquals(NEW, epicTask.getStatus());
    }

    @Test
    void checkEqualsEpicTasks() {
        assertEquals(epicTask, epicTask);
        EpicTask testTask = new EpicTask(epicTask.getId(), "", "");
        assertEquals(epicTask, testTask);
    }

    @Test
    void testToString() {
        String test = "1,EPIC_TASK,EpicName,EpicDescription,NEW, ,0";
        assertEquals(test, epicTask.toString());
    }
}