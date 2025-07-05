package service;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.interfaces.HistoryManager;
import util.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void getHistoryWithEmptyViewedTasksHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void addTaskToViewedTasksHistory() {
        historyManager.add(new Task("Name", "Description"));
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void add3TypesTasksToViewedTasksHistory() {
        historyManager.add(new Task(1, "Name", "Description"));
        historyManager.add(new EpicTask(2, "Name", "Description"));
        historyManager.add(new SubTask("Name", "Description", 2));
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
    }

    @Test
    void shouldNotAddDuplicateTask() {
        Task task1 = new Task(1, "Name", "Description");
        historyManager.add(task1);
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());

        EpicTask epicTask1 = new EpicTask(2, "Name", "Description");
        historyManager.add(epicTask1);
        historyManager.add(epicTask1);
        history = historyManager.getHistory();
        assertEquals(2, history.size());

        SubTask subTask1 = new SubTask("Name", "Description", 2);
        historyManager.add(subTask1);
        historyManager.add(subTask1);
        history = historyManager.getHistory();
        assertEquals(3, history.size());
    }

    @Test
    void shouldRemoveTaskFromHistory() {

        Task task1 = new Task(1, "Task1", "");
        historyManager.add(task1);
        EpicTask epicTask1 = new EpicTask(2, "EpicTask1", "");
        historyManager.add(epicTask1);
        SubTask subTask1 = new SubTask("subTask1", "from epicTask1", epicTask1.getId());
        historyManager.add(subTask1);

        assertEquals(3, historyManager.getHistory().size());
        historyManager.remove(subTask1.getId());
        assertEquals(2, historyManager.getHistory().size());
        historyManager.remove(epicTask1.getId());
        assertEquals(1, historyManager.getHistory().size());
        historyManager.remove(task1.getId());
        assertEquals(0, historyManager.getHistory().size());
    }
}