package services;

public class Node<E> {
    protected E task;
    protected Node<E> next;
    protected Node<E> prev;

    protected Node(Node<E> prev, E task, Node<E> next){
        this.task = task;
        this.next = next;
        this.prev = prev;
    }
}