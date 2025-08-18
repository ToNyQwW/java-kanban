package service;

import model.Node;
import model.Task;
import service.interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> viewedTasksHistory;
    private final Node<Task> head;
    private final Node<Task> tail;

    public InMemoryHistoryManager() {
        viewedTasksHistory = new HashMap<>();
        head = new Node<>(null);
        tail = new Node<>(null);
        head.setNext(tail);
        tail.setPrev(head);
    }

    @Override
    public void add(Task task) {
        Node<Task> node = viewedTasksHistory.get(task.getId());
        if (node != null) {
            removeNode(node);
        } else {
            node = new Node<>(task);
            viewedTasksHistory.put(task.getId(), node);
        }
        linkLast(node);
    }

    @Override
    public boolean remove(int id) {
        Node<Task> remove = viewedTasksHistory.remove(id);
        if (remove != null) {
            removeNode(remove);
            return true;
        }
        return false;
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node<Task> node = head.getNext();
        while (node != tail) {
            history.add(node.getTask());
            node = node.getNext();
        }
        return history;
    }

    private void linkLast(Node<Task> node) {
        Node<Task> lastNode = tail.getPrev();
        lastNode.setNext(node);
        node.setPrev(lastNode);
        node.setNext(tail);
        tail.setPrev(node);
    }

    private void removeNode(Node<Task> node) {
        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
    }
}
