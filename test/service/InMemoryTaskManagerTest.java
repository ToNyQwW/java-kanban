package service;

import model.*;
import org.junit.jupiter.api.*;
import service.interfaces.TaskManager;
import utill.Managers;

import java.util.List;

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
    void CheckGetLists() {

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
    void checkGetHistoryWith12TaskInHistory() {
        for (int i = 0; i < 4; i++) {
            taskManager.getTask(task.getId());
            taskManager.getSubTask(subTask.getId());
            taskManager.getEpicTask(epicTask.getId());
        }
        List<Task> history = taskManager.getHistory();
        assertEquals(InMemoryHistoryManager.MAX_SIZE_HISTORY, history.size());
        assertEquals(epicTask, history.getFirst());
    }

    @Test
    void IfGetTaskReturnNullDontAddInHistory() {
        taskManager.getTask(10000);
        taskManager.getSubTask(20000);
        taskManager.getEpicTask(30000);
        assertEquals(0, taskManager.getHistory().size());
    }
}