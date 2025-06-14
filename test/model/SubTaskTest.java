package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    private static EpicTask epicTask;
    private static SubTask subTask;

    @BeforeAll
    static void setUp() {
        epicTask = new EpicTask(1, "EpicName", "EpicDescription");
        subTask = new SubTask(2, "SubName", "SubDescription",TaskStatus.NEW, 1);
    }

    @Test
    void checkGetters() {
        assertEquals(2, subTask.getId());
        assertEquals("SubName", subTask.getName());
        assertEquals("SubDescription", subTask.getDescription());
        assertEquals(TaskStatus.NEW, subTask.getStatus());
        assertEquals(1, epicTask.getId());
    }

    @Test
    void getEpicId() {
        assertEquals(epicTask.getId(), subTask.getEpicId());
    }

    @Test
    void checkEqualsSubTasks() {
        assertEquals(subTask, subTask);
        SubTask testSubTask = new SubTask(subTask.getId(), "", "", TaskStatus.DONE, epicTask.getId());
        assertEquals(subTask, testSubTask);
    }
}