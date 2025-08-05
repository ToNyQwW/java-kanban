package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static model.TaskStatus.DONE;
import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    private static Task task;

    @BeforeAll
    static void setUp() {
        task = new Task(153, "Name", "Description", NEW);
    }

    @Test
    void checkGetters() {
        assertEquals(153, task.getId());
        assertEquals("Name", task.getName());
        assertEquals("Description", task.getDescription());
        assertEquals(NEW, task.getStatus());
    }

    @Test
    void checkSetters() {
        task.setName("NewName");
        assertEquals("NewName", task.getName());
        task.setDescription("NewDescription");
        assertEquals("NewDescription", task.getDescription());
        task.setStatus(NEW);
        assertEquals(NEW, task.getStatus());
    }

    @Test
    void checkEqualsTasks() {
        assertEquals(task, task);
        Task testTask = new Task(153, "", "", DONE);
        assertEquals(task, testTask);
    }

    @Test
    void testToString() {
        String test = "153,TASK,Name,Description,NEW, ,0";
        assertEquals(test, task.toString());
    }
}