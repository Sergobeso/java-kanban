package services;

/**
 * Утилитарный класс, овечает за создание менеджера задач
 * Имеет метод getDefaultHistory - возвращать объект InMemoryHistoryManager (историю просмотров)
 */

public class Managers {

    public static TaskManager getDefault(){
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

}
