package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskTest {

    private static Task task;

    @BeforeAll
    static void setUp() {
       task = new Task(153, "Name", "Description", TaskStatus.NEW);
    }

    @Test
    void checkGetters() {
        assertEquals(153, task.getId());
        assertEquals("Name", task.getName());
        assertEquals("Description", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void checkSetters() {
        task.setName("NewName");
        assertEquals("NewName", task.getName());
        task.setDescription("NewDescription");
        assertEquals("NewDescription", task.getDescription());
        task.setStatus(TaskStatus.NEW);
        assertEquals(TaskStatus.NEW, task.getStatus());
    }

    @Test
    void checkEqualsTasks() {
        assertEquals(task, task);
        Task testTask = new Task(153, "", "", TaskStatus.DONE);
        assertEquals(task, testTask);
    }
}