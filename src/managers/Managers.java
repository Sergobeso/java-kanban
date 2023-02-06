package managers;

import server.HttpTaskManager;

import java.io.File;
import java.io.IOException;

/**
 * Утилитарный класс, овечает за создание менеджера задач
 * Имеет метод getDefaultHistory - возвращать объект InMemoryHistoryManager (историю просмотров)
 */

public class Managers {

    public static TaskManager getDefaultInMemoryTaskManager(){
        return new InMemoryTaskManager();
    }

    public static HttpTaskManager getDefault() throws IOException, InterruptedException {
        return new HttpTaskManager("http://localhost:");
    }
    public static FileBackedTasksManager getDefaultFBTM(){
        return new FileBackedTasksManager(new File("./data/historyTest.csv"));
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

}
