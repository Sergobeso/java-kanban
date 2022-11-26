package services;

import modules.Task;

import java.util.List;

/**
 * Утилитарный класс, овечает за создание менеджера задач
 * Имеет метод getDefaultHistory - возвращать объект InMemoryHistoryManager (историю просмотров)
 */

public class Managers {

    public TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static List<Task> getDefaultHistory(){
        return new InMemoryHistoryManager().getHistory();
    }
}
