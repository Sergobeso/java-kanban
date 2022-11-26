package services;

import modules.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс описывающий хранение и добавление истории задач
 */

public class InMemoryHistoryManager implements HistoryManager {
    private static List<Task> historyList = new ArrayList<>();
    private static final int MAX_LENGTH_IN_HISTORY = 10;

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    // метод проверяет список на величину списка и добавляет в историю просмотров
    public void add(Task task) {
        if (historyList.size() == MAX_LENGTH_IN_HISTORY) {
            historyList.remove(0);
            historyList.add(task);
        } else historyList.add(task);
    }

}
