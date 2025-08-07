package util;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static model.TaskStatus.NEW;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskTimeIntersectionCheckerTest {

    private static TaskTimeIntersectionChecker taskChecker;
    Task task;

    @BeforeEach
    void setUp() {
        taskChecker = new TaskTimeIntersectionChecker();
        LocalDateTime startTime = taskChecker.getBaseTIme();
        task = new Task(1, "Name", "Description", NEW, startTime, Duration.ofMinutes(30));
    }


    @Test
    void addIntervalFromTask() {
        taskChecker.addIntervalFromTask(task);
        boolean[] intervalArray = taskChecker.getIntervalArray();
        assertTrue(intervalArray[0]);
        assertTrue(intervalArray[1]);
        assertFalse(intervalArray[2]);
    }

    @Test
    void removeIntervalFromTask() {
        taskChecker.addIntervalFromTask(task);
        taskChecker.removeIntervalFromTask(task);
        boolean[] intervalArray = taskChecker.getIntervalArray();
        assertFalse(intervalArray[0]);
        assertFalse(intervalArray[1]);
        assertFalse(intervalArray[2]);
    }

    @Test
    void shouldReturnFalseWhenIntersection() {
        taskChecker.addIntervalFromTask(task);
        Task testTask1 = new Task(2, "Name", "Description", NEW,
                task.getStartTime().plusMinutes(29), Duration.ofMinutes(30));
        Task testTask2 = new Task(3, "Name", "Description", NEW,
                task.getStartTime().minusMinutes(30), Duration.ofMinutes(31));
        assertFalse(taskChecker.addIntervalFromTask(testTask1));
        assertFalse(taskChecker.addIntervalFromTask(testTask2));
        assertFalse(taskChecker.addIntervalFromTask(task));
    }

    @Test
    void shouldAddIntervalCorrectly() {
        Task testTask1 = new Task(2, "Name", "Description", NEW,
                task.getStartTime().plusMinutes(30), Duration.ofMinutes(30));
        Task testTask2 = new Task(3, "Name", "Description", NEW,
                testTask1.getStartTime().plusMinutes(30), Duration.ofMinutes(30));
        taskChecker.addIntervalFromTask(task);
        taskChecker.addIntervalFromTask(testTask1);
        taskChecker.addIntervalFromTask(testTask2);
        boolean[] intervalArray = taskChecker.getIntervalArray();
        IntStream.range(0, 6).forEach(i -> assertTrue(intervalArray[i]));
    }

    @Test
    void shouldRemoveIntervalCorrectly() {
        Task testTask1 = new Task(2, "Name", "Description", NEW,
                task.getStartTime().plusMinutes(30), Duration.ofMinutes(30));
        Task testTask2 = new Task(3, "Name", "Description", NEW,
                testTask1.getStartTime().plusMinutes(30), Duration.ofMinutes(30));
        taskChecker.addIntervalFromTask(task);
        taskChecker.addIntervalFromTask(testTask1);
        taskChecker.addIntervalFromTask(testTask2);

        taskChecker.removeIntervalFromTask(task);
        boolean[] intervalArray = taskChecker.getIntervalArray();
        IntStream.range(0, 2).forEach(i -> assertFalse(intervalArray[i]));

        taskChecker.removeIntervalFromTask(testTask1);
        IntStream.range(2, 4).forEach(i -> assertFalse(intervalArray[i]));

        taskChecker.removeIntervalFromTask(testTask2);
        IntStream.range(4, 6).forEach(i -> assertFalse(intervalArray[i]));
    }

    @Test
    void shouldReturnFalseIfIndexOutOfIntervalArray() {
        Task testTask1 = new Task(2, "Name", "Description", NEW,
                taskChecker.getBaseTIme().minusMinutes(15), Duration.ofMinutes(30));
        assertFalse(taskChecker.addIntervalFromTask(testTask1));

        Task testTask2 = new Task(3, "Name", "Description", NEW,
                taskChecker.getBaseTIme(), Duration.ofDays(400));
        assertFalse(taskChecker.addIntervalFromTask(testTask2));

        Task testTask3 = new Task(4, "Name", "Description", NEW,
                taskChecker.getBaseTIme().plusDays(400), Duration.ofDays(1));
        assertFalse(taskChecker.addIntervalFromTask(testTask3));
    }
}