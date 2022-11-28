package services;

import modules.Task;

import java.util.LinkedList;

/**
 * Класс описывающий хранение и добавление истории задач
 */

public class InMemoryHistoryManager implements HistoryManager {
    private final LinkedList<Task> historyList = new LinkedList<>();
    private static final int MAX_LENGTH_IN_HISTORY = 10;

    @Override
    public LinkedList<Task> getHistory() {
        return historyList;
    }

    // LIFO - добавление задачи в начало списка, удаление задачи из конца списка
    public void add(Task task) {
        if (historyList.size() == MAX_LENGTH_IN_HISTORY) { historyList.removeLast();}
        historyList.add(0, task);
    }

}
