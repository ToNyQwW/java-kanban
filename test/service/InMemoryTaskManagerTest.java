package service;

import model.EpicTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.interfaces.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

import static model.TaskStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private static TaskManager taskManager;
    private Task task;
    private SubTask subTask;
    private SubTask subTask2;
    private EpicTask epicTask;

    private LocalDateTime time1;
    private LocalDateTime time2;
    private Duration duration1;
    private Duration duration2;

    @BeforeAll
    static void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @BeforeEach
    void setUpEach() {
        time1 = LocalDateTime.now();
        time2 = time1.plusHours(1);
        duration1 = Duration.ofMinutes(30);
        duration2 = Duration.ofMinutes(45);

        task = new Task("Name", "Description", NEW, time1, duration1);
        taskManager.addTask(task);

        epicTask = new EpicTask("Name", "Description");
        taskManager.addEpicTask(epicTask);

        subTask = new SubTask("Name", "Description", NEW, time1, duration1, epicTask.getId());
        subTask2 = new SubTask("Name", "Description", NEW, time2, duration2, epicTask.getId());
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(subTask2);
    }

    @AfterEach
    void afterEach() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void checkGetLists() {

        assertEquals(1, taskManager.getTasksList().size());
        assertEquals(task, taskManager.getTasksList().getFirst());


        assertEquals(1, taskManager.getEpicTasksList().size());
        assertEquals(epicTask, taskManager.getEpicTasksList().getFirst());


        assertEquals(2, taskManager.getSubTasksList().size());
        assertEquals(subTask, taskManager.getSubTasksList().getFirst());
    }

    @Test
    void removeTaskAndCheckIt() {
        taskManager.removeTask(task.getId());
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    void removeSubtaskAndCheckIt() {

        EpicTask EpicFromSub = taskManager.getEpicTask(subTask.getEpicId()).get();
        assertEquals(NEW, EpicFromSub.getStatus());

        taskManager.removeSubTask(subTask.getId());
        taskManager.removeSubTask(subTask2.getId());
        assertEquals(0, taskManager.getSubTasksList().size());
        assertEquals(0, epicTask.getSubInEpic().size());
        assertEquals(DONE, EpicFromSub.getStatus());
    }

    @Test
    void removeEpicTaskAndCheckIt() {
        taskManager.removeEpicTask(epicTask.getId());
        assertEquals(0, taskManager.getEpicTasksList().size());
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    void updateTaskAndCheckIt() {
        Task updatetask = new Task(task.getId(), "NewName", "NewDescription", NEW);
        taskManager.updateTask(updatetask);

        assertEquals(1, taskManager.getTasksList().size());
        assertEquals(updatetask, taskManager.getTasksList().getFirst());

    }

    @Test
    void updateSubTaskAndCheckIt() {
        SubTask updateSubTask = new SubTask(subTask.getId(), "NewName", "NewDescription",
                IN_PROGRESS, epicTask.getId());
        taskManager.updateSubTask(updateSubTask);
        taskManager.removeSubTask(subTask2.getId());

        assertEquals(1, taskManager.getSubTasksList().size());
        assertEquals(updateSubTask, taskManager.getSubTasksList().getFirst());
        assertEquals(IN_PROGRESS, epicTask.getStatus());

        updateSubTask = new SubTask(subTask.getId(), "NewName", "NewDescription",
                DONE, epicTask.getId());

        taskManager.updateSubTask(updateSubTask);
        assertEquals(DONE, epicTask.getStatus());
    }

    @Test
    void updateEpicTaskAndCheckIt() {
        EpicTask updateEpicTask = new EpicTask(epicTask.getId(), "NewName", "NewDescription");
        taskManager.updateEpicTask(updateEpicTask);

        assertEquals(1, taskManager.getEpicTasksList().size());
        assertEquals(updateEpicTask, taskManager.getEpicTasksList().getFirst());

        assertEquals(NEW, epicTask.getStatus());
    }

    @Test
    void checkFullClear() {
        taskManager.clearTasksMap();
        assertEquals(0, taskManager.getTasksList().size());

        taskManager.clearSubtasksMap();
        assertEquals(0, taskManager.getSubTasksList().size());

        taskManager.clearEpicTasksMap();
        assertEquals(0, taskManager.getEpicTasksList().size());
    }

    @Test
    void ifGetTaskReturnNullDontAddInHistory() {
        taskManager.getTask(10000);
        taskManager.getSubTask(20000);
        taskManager.getEpicTask(30000);
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void shouldRemoveSubTasksInHistoryWhenRemoveEpic() {
        taskManager.getSubTask(subTask.getId());
        taskManager.getEpicTask(epicTask.getId());

        taskManager.removeEpicTask(epicTask.getId());
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    void shouldRemovedSubTasksFromHistoryWhenClearEpicMap() {
        SubTask subTask2 = new SubTask("TestName", "Description", NEW, epicTask.getId());
        taskManager.addSubTask(subTask2);
        taskManager.getSubTask(subTask.getId());
        taskManager.getSubTask(subTask2.getId());
        assertEquals(2, taskManager.getHistory().size());
        taskManager.clearEpicTasksMap();
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void shouldRemovedTasksFromHistoryWhenClearMaps() {

        Task task2 = new Task("TestName", "Description", NEW);
        taskManager.addTask(task2);

        EpicTask epicTask2 = new EpicTask("TestName", "Description");
        taskManager.addEpicTask(epicTask2);

        SubTask subTask2 = new SubTask("TestName", "Description", NEW, epicTask.getId());
        taskManager.addSubTask(subTask2);

        taskManager.getTask(task.getId());
        taskManager.getTask(task2.getId());
        taskManager.clearTasksMap();
        assertEquals(0, taskManager.getTasksList().size());

        taskManager.getSubTask(subTask2.getId());
        taskManager.getSubTask(subTask2.getId());
        taskManager.clearSubtasksMap();
        assertEquals(0, taskManager.getHistory().size());

        taskManager.getEpicTask(epicTask.getId());
        taskManager.getEpicTask(epicTask2.getId());
        taskManager.clearEpicTasksMap();
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void shouldCalculateTimeCorrectlyInEpicWhenAdd2SubTask() {
        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = time1.plusHours(1);
        Duration duration1 = Duration.ofMinutes(30);
        Duration duration2 = Duration.ofMinutes(45);

        EpicTask epicTask = new EpicTask("TestName", "TestDescription");
        taskManager.addEpicTask(epicTask);
        SubTask subTask1 = new SubTask("TestName", "Description", NEW, time1, duration1, epicTask.getId());
        SubTask subTask2 = new SubTask("TestName", "Description", NEW, time2, duration2, epicTask.getId());
        taskManager.addSubTask(subTask1);

        assertEquals(time1, epicTask.getStartTime());
        assertEquals(duration1, epicTask.getDuration());
        assertEquals(time1.plus(duration1), epicTask.getEndTime());

        taskManager.addSubTask(subTask2);
        assertEquals(time1, epicTask.getStartTime());
        assertEquals(duration1.plus(duration2), epicTask.getDuration());
        assertEquals(time2.plus(duration2), epicTask.getEndTime());
    }

    @Test
    void shouldCalculateTimeCorrectlyInEpicWhenAdd2SubTaskAndRemoveThem() {
        assertEquals(time1, epicTask.getStartTime());
        assertEquals(duration1.plus(duration2), epicTask.getDuration());
        assertEquals(time2.plus(duration2), epicTask.getEndTime());

        taskManager.removeSubTask(subTask.getId());
        assertEquals(time2, epicTask.getStartTime());
        assertEquals(duration2, epicTask.getDuration());
        assertEquals(time2.plus(duration2), epicTask.getEndTime());

        taskManager.removeSubTask(subTask2.getId());
        assertNull(epicTask.getStartTime());
        assertEquals(Duration.ZERO, epicTask.getDuration());
        assertNull(epicTask.getEndTime());
    }

    @Test
    void shouldCalculateTimeCorrectlyInEpicWhenClearSubTaskMap() {
        taskManager.clearSubtasksMap();
        assertNull(epicTask.getStartTime());
        assertEquals(Duration.ZERO, epicTask.getDuration());
        assertNull(epicTask.getEndTime());
    }

    @Test
    void updatePrioritizedTasksTest() {
        SubTask subTask3 = new SubTask("", "", NEW, time1, duration1, epicTask.getId());
        taskManager.addSubTask(subTask3);
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        taskManager.removeSubTask(subTask.getId());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
        taskManager.removeTask(task.getId());
        assertEquals(subTask3, taskManager.getPrioritizedTasks().getFirst());

        taskManager.clearSubtasksMap();

        Task task2 = new Task("", "", NEW, time1, duration1);
        taskManager.addTask(task2);
        taskManager.addTask(task);
        System.out.println(taskManager.getPrioritizedTasks());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        taskManager.removeTask(task2.getId());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
        assertEquals(task, taskManager.getPrioritizedTasks().getFirst());
    }

    @Test
    void unPriorityCorrectlyRemovedSubTasksTest() {
        assertEquals(subTask, taskManager.getUnprioritizedTasks().getFirst(),
                "subtask должен быть в unPriority");
        taskManager.removeSubTask(subTask.getId());
        assertEquals(0, taskManager.getUnprioritizedTasks().size());
    }

    @Test
    void unPriorityCorrectlyRemovedTasksTest() {
        Task task2 = new Task("", "", NEW, time1, duration1);
        taskManager.addTask(task2);
        assertTrue(taskManager.getUnprioritizedTasks().contains(task2),
                "task2 должен быть в unPriority");
        taskManager.removeTask(task2.getId());
        assertFalse(taskManager.getUnprioritizedTasks().contains(task2));
    }
}