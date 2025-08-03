package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static model.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    private static EpicTask epicTask;
    private static SubTask subTask;

    @BeforeAll
    static void setUp() {
        epicTask = new EpicTask(1, "EpicName", "EpicDescription");
        subTask = new SubTask(2, "SubName", "SubDescription", NEW, 1);
    }

    @Test
    void checkGetters() {
        assertEquals(2, subTask.getId());
        assertEquals("SubName", subTask.getName());
        assertEquals("SubDescription", subTask.getDescription());
        assertEquals(NEW, subTask.getStatus());
        assertEquals(1, epicTask.getId());
    }

    @Test
    void getEpicId() {
        assertEquals(epicTask.getId(), subTask.getEpicId());
    }

    @Test
    void checkEqualsSubTasks() {
        assertEquals(subTask, subTask);
        SubTask testSubTask = new SubTask(subTask.getId(), "", "", DONE, epicTask.getId());
        assertEquals(subTask, testSubTask);
    }

    @Test
    void testToString() {
        String test = "2,SUB_TASK,SubName,SubDescription,NEW, ,0,1";
        assertEquals(test, subTask.toString());
    }
}