package service.interfaces;

import model.Task;

import java.util.Deque;

public interface HistoryManager {

    void add(Task task);

    Deque<Task> getHistory();
}
