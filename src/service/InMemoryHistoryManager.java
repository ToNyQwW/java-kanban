package service;

import model.Task;
import service.historyModel.Node;
import service.interfaces.HistoryManager;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node<Task>> viewedTasksHistory;
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager() {
        viewedTasksHistory = new HashMap<>();
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
        Node<Task> node = head;
        while (node != null) {
            history.add(node.getTask());
            node = node.getNext();
        }
        return history;
    }

    private void linkLast(Node<Task> node) {
        if (head == null) {
            head = node;
        } else {
            tail.setNext(node);
            node.setPrev(tail);
            node.setNext(null);
        }
        tail = node;
    }

    private void removeNode(Node<Task> node) {
        if (node == head) {
            head = node.getNext();
            if (head != null) {
                head.setPrev(null);
            }
        } else if (node == tail) {
            tail = node.getPrev();
            if (tail != null) {
                tail.setNext(null);
            }
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
    }
}
