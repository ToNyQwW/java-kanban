package service;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.interfaces.HistoryManager;
import utill.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    void setUp(){
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void getHistoryWithEmptyViewedTasksHistory(){
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void addTaskToViewedTasksHistory(){
        historyManager.add(new Task("Name", "Description"));
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }

    @Test
    void add11TaskToViewedTasksHistory(){
        for (int i = 0; i < 11; i++) {
            historyManager.add(new Task(i,"Name", "Description"));
        }
        List<Task> history = historyManager.getHistory();
        assertEquals(InMemoryHistoryManager.MAX_SIZE_HISTORY, history.size());
        assertFalse(history.contains(new Task(0,"Name", "Description")));

    }

    @Test
    void add3TypesTasksToViewedTasksHistory(){
        historyManager.add(new Task("Name", "Description"));
        historyManager.add(new EpicTask(1,"Name", "Description"));
        historyManager.add(new SubTask("Name", "Description",1));
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
    }
}