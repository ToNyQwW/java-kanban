package service;

import model.*;
import org.junit.jupiter.api.*;
import service.interfaces.TaskManager;
import util.Managers;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {

    private static TaskManager taskManager;
    private Task task;
    private SubTask subTask;
    private EpicTask epicTask;

    @BeforeAll
    static void setUp() {
        taskManager = Managers.getDefault();
    }

    @BeforeEach
    void setUpEach() {
        task = new Task("Name", "Description");
        taskManager.addTask(task);

        epicTask = new EpicTask("Name", "Description");
        taskManager.addEpicTask(epicTask);

        subTask = new SubTask("Name", "Description", epicTask.getId());
        taskManager.addSubTask(subTask);
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


        assertEquals(1, taskManager.getSubTasksList().size());
        assertEquals(subTask, taskManager.getSubTasksList().getFirst());
    }

    @Test
    void removeTaskAndCheckIt() {
        taskManager.removeTask(task.getId());
        assertEquals(0, taskManager.getTasksList().size());
    }

    @Test
    void removeSubtaskAndCheckIt() {

        EpicTask EpicFromSub = taskManager.getEpicTask(subTask.getEpicId());
        assertEquals(TaskStatus.NEW, EpicFromSub.getStatus());

        taskManager.removeSubTask(subTask.getId());
        assertEquals(0, taskManager.getSubTasksList().size());
        assertEquals(0, epicTask.getSubInEpic().size());
        assertEquals(TaskStatus.DONE, EpicFromSub.getStatus());
    }

    @Test
    void removeEpicTaskAndCheckIt() {
        taskManager.removeEpicTask(epicTask.getId());
        assertEquals(0, taskManager.getEpicTasksList().size());
        assertEquals(0, taskManager.getSubTasksList().size());
    }

    @Test
    void updateTaskAndCheckIt() {
        Task updatetask = new Task(task.getId(), "NewName", "NewDescription");
        taskManager.updateTask(updatetask);

        assertEquals(1, taskManager.getTasksList().size());
        assertEquals(updatetask, taskManager.getTasksList().getFirst());

    }

    @Test
    void updateSubTaskAndCheckIt() {
        SubTask updateSubTask = new SubTask(subTask.getId(), "NewName", "NewDescription",
                TaskStatus.IN_PROGRESS, epicTask.getId());
        taskManager.updateSubTask(updateSubTask);

        assertEquals(1, taskManager.getSubTasksList().size());
        assertEquals(updateSubTask, taskManager.getSubTasksList().getFirst());
        assertEquals(TaskStatus.IN_PROGRESS, epicTask.getStatus());

        updateSubTask = new SubTask(subTask.getId(), "NewName", "NewDescription",
                TaskStatus.DONE, epicTask.getId());

        taskManager.updateSubTask(updateSubTask);
        assertEquals(TaskStatus.DONE, epicTask.getStatus());
    }

    @Test
    void updateEpicTaskAndCheckIt() {
        EpicTask updateEpicTask = new EpicTask(epicTask.getId(), "NewName", "NewDescription");
        taskManager.updateEpicTask(updateEpicTask);

        assertEquals(1, taskManager.getEpicTasksList().size());
        assertEquals(updateEpicTask, taskManager.getEpicTasksList().getFirst());

        assertEquals(TaskStatus.NEW, epicTask.getStatus());
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
        SubTask subTask2 = new SubTask("TestName", "", epicTask.getId());
        taskManager.addSubTask(subTask2);
        taskManager.getSubTask(subTask.getId());
        taskManager.getSubTask(subTask2.getId());
        assertEquals(2, taskManager.getHistory().size());
        taskManager.clearEpicTasksMap();
        assertEquals(0, taskManager.getHistory().size());
    }

    @Test
    void shouldRemovedTasksFromHistoryWhenClearMaps() {

        Task task2 = new Task("TestName", "");
        taskManager.addTask(task2);

        EpicTask epicTask2 = new EpicTask("TestName", "");
        taskManager.addEpicTask(epicTask2);

        SubTask subTask2 = new SubTask("TestName", "", epicTask.getId());
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
}