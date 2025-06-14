package service;

import model.EpicTask;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Deque;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static InMemoryHistoryManager historyManager;

    @BeforeAll
    static void setUp(){
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void getHistoryWithEmptyViewedTasksHistory(){
        Deque<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void addTaskToViewedTasksHistory(){
        historyManager.add(new Task("Name", "Description"));
        Deque<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void add11TaskToViewedTasksHistory(){
        for (int i = 0; i < 11; i++) {
            historyManager.add(new Task(i,"Name", "Description"));
        }
        Deque<Task> history = historyManager.getHistory();
        assertEquals(InMemoryHistoryManager.MAX_SIZE_HISTORY, history.size());
        assertFalse(history.contains(new Task(0,"Name", "Description")));

    }

    @Test
    void add3TypesTasksToViewedTasksHistory(){
        historyManager.add(new Task("Name", "Description"));
        historyManager.add(new EpicTask(1,"Name", "Description"));
        historyManager.add(new SubTask("Name", "Description",1));
        Deque<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
    }
}