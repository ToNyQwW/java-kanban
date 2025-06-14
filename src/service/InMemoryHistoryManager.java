package service;

import model.Task;
import service.interfaces.HistoryManager;

import java.util.ArrayDeque;
import java.util.Deque;

public class InMemoryHistoryManager implements HistoryManager {

    public static final int MAX_SIZE_HISTORY = 10;

    private final Deque<Task> viewedTasksHistory;


    public InMemoryHistoryManager() {
        viewedTasksHistory = new ArrayDeque<>();
    }

    @Override
    public void add(Task task) {
        if (viewedTasksHistory.size() >= MAX_SIZE_HISTORY) {
            viewedTasksHistory.removeFirst();
        }
        viewedTasksHistory.addLast(task);
    }

    @Override
    public Deque<Task> getHistory() {
        return viewedTasksHistory;
    }
}
