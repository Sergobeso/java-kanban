package services;

public class Node<E> {
    private E element;
    private Node<E> next;
    private Node<E> prev;

    public Node(Node<E> prev, E element, Node<E> next){
        this.element = element;
        this.next = next;
        this.prev = prev;
    }

    public E getElement() {
        return element;
    }

    public void setElement(E task) {
        this.element = task;
    }

    public Node<E> getNext() {
        return next;
    }

    public void setNext(Node<E> next) {
        this.next = next;
    }

    public Node<E> getPrev() {
        return prev;
    }

    public void setPrev(Node<E> prev) {
        this.prev = prev;
    }
}