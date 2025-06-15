package service;

import model.Task;
import service.interfaces.HistoryManager;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public static final int MAX_SIZE_HISTORY = 10;

    private final List<Task> viewedTasksHistory;


    public InMemoryHistoryManager() {
        viewedTasksHistory = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (viewedTasksHistory.size() >= MAX_SIZE_HISTORY) {
            viewedTasksHistory.removeFirst();
        }
        viewedTasksHistory.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(viewedTasksHistory);
    }
}
