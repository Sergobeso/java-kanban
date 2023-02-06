package tests;

import managers.FileBackedTasksManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskManager;
import server.KVServer;

import java.io.IOException;
/**
 * Класс описывающий реализацию ТЕСТОВ менеджера HttpTaskManagerTest.
 */
public class HttpTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private KVServer server;
    private HttpTaskManager manager;
    @BeforeEach
    public void beforeEach() throws IOException, InterruptedException {
        taskManager = new InMemoryTaskManager();
        server = new KVServer();
        server.start();
        manager = Managers.getDefault();
    }

    @AfterEach
    public void afterEach() {
        server.stop();
    }

    @Test
    public void shouldSaveTask() {
        manager.addEpicTask(epicTask);
        manager.addSubTask(subTask, epicTask);
        manager.addTask(task);

        Assertions.assertTrue( manager.getListTask().contains(task), "Задача task не была добавлена");
        Assertions.assertTrue(manager.getListSubTask().contains(subTask), "Задача subTask не была добавлена");
        Assertions.assertTrue(manager.getListEpicTask().contains(epicTask), "Задача epicTask не была добавлена");
    }

    @Test
    public void shouldIsEmptyListTasks() throws IOException, InterruptedException {
        manager.save();

        Assertions.assertTrue(manager.getListTask().isEmpty(), "Файл загружен с ошибками, список задач Task должен быть пустрой");
        Assertions.assertTrue(manager.getListEpicTask().isEmpty(), "Файл загружен с ошибками, список задач EpicTask должен быть пустрой");
        Assertions.assertTrue(manager.getListSubTask().isEmpty(), "Файл загружен с ошибками, список задач SubTask должен быть пустрой");
    }

    @Test
    public void shouldIsEmptyListHistory() throws IOException, InterruptedException {
        manager.save();
        Assertions.assertTrue(manager.getHistoryManager().getHistory().isEmpty(), "История задач должна быть пустрой");
    }

}

