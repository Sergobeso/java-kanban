package services;

import modules.Task;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс описывающий хранение, добавление и удаление истории задач
 */

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Node> historyMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    public void add(Task task) {
        if (task != null) {
            if (historyMap.containsKey(task.getId())) {
                remove(task.getId());
            }
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    //  linkLast добавляет задачу в конец списка
    private void linkLast(Task task) {
        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        historyMap.put(task.getId(), newNode);
    }

    // getTasks собирает все задачи из списка обычный ArrayList
    private ArrayList<Task> getTasks() {
        ArrayList<Task> list = new ArrayList<>();
        Node<Task> tempNode = head;
        while (tempNode != null) {
            list.add(tempNode.task);
            tempNode = tempNode.next;
        }
        return list;
    }

    // удаляет узел связанного списка
    private void removeNode(Node<Task> node) {
        if (node == head && node != tail) {
            head = node.next;
            head.prev = null;
        } else if (node == tail && node != head) {
            tail = node.prev;
            tail.next = null;
        } else if (head == tail) {
            head = null;
            tail = null;
        } else {
            Node<Task> next = node.next;
            Node<Task> prev = node.prev;

            next.prev = prev;
            prev.next = next;
        }
    }
}
