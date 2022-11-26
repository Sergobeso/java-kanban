package services;

import modules.Task;

import java.util.List;

/**
 * Интерфейс для управления историей просмотров.
 * Имеет методы для добавления и возвращения задач в истории просмотра
 */

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

}
