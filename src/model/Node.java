package model;

public class Node<T extends Task> {

    private final T task;
    private Node<T> next;
    private Node<T> prev;

    public Node(T task) {
        this.task = task;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public T getTask() {
        return task;
    }
}
