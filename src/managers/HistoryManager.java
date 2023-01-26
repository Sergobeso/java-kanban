package managers;

import modules.Task;

import java.util.List;

/**
 * Интерфейс для управления историей просмотров.
 * Имеет методы для добавления, возвращения и удаления задач в истории просмотра
 */

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(int id);

}
