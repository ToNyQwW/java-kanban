package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

//Проверка пересечений в TaskTimeIntersectionCheckerTest
// здесь проверка сортировки + базово методы add и remove
class PriorityTaskManagerTest {

    private static PriorityTaskManager priorityManager;
    private static Task task1;
    private static Task task2;
    private static Task task3;

    @BeforeEach
    void setUp() {
        priorityManager = new PriorityTaskManager();
        task1 = new Task(1, "Name", "Description", NEW,
                LocalDateTime.now(), Duration.ofMinutes(30));
        task2 = new Task(2, "Name", "Description", NEW,
                task1.getStartTime().plusMinutes(30), Duration.ofMinutes(30));
        task3 = new Task(3, "Name", "Description", NEW);
    }

    @Test
    void addTask() {
        assertTrue(priorityManager.addTask(task1));
        assertTrue(priorityManager.addTask(task2));
        assertFalse(priorityManager.addTask(task1));
        assertFalse(priorityManager.addTask(task3));
    }

    @Test
    void removeTask() {
        priorityManager.addTask(task1);
        priorityManager.addTask(task2);
        assertTrue(priorityManager.removeTask(task1));
        assertTrue(priorityManager.removeTask(task2));
        assertFalse(priorityManager.removeTask(task3));

        assertFalse(priorityManager.removeTask(task1));
        assertFalse(priorityManager.removeTask(task2));
    }

    @Test
    void getTasks() {
        priorityManager.addTask(task2);
        priorityManager.addTask(task1);

        List<Task> prioritizedTasks = priorityManager.getPrioritizedTasks();
        assertEquals(task1, prioritizedTasks.getFirst());
        assertEquals(task2, prioritizedTasks.getLast());

        task3.setStartTime(task2.getEndTime().plusMinutes(30));
        task3.setDuration(Duration.ofMinutes(30));
        priorityManager.addTask(task3);

        Task task4 = new Task(4, "Name", "Description", NEW,
                task2.getEndTime(), Duration.ofMinutes(30));
        priorityManager.addTask(task4);

        prioritizedTasks = priorityManager.getPrioritizedTasks();
        assertEquals(task4, prioritizedTasks.get(2));
        assertEquals(task3, prioritizedTasks.getLast());
    }

}