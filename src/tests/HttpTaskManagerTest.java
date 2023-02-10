package tests;

import com.google.gson.Gson;
import managers.InMemoryTaskManager;
import managers.Managers;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskManager;
import server.KVServer;
import server.KVTaskClient;

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

        Assertions.assertTrue(manager.getListTask().contains(task), "Задача task не была добавлена");
        Assertions.assertTrue(manager.getListSubTask().contains(subTask), "Задача subTask не была добавлена");
        Assertions.assertTrue(manager.getListEpicTask().contains(epicTask), "Задача epicTask не была добавлена");
    }

    @Test
    public void shouldIsEmptyListTasks() {
        manager.save();

        Assertions.assertTrue(manager.getListTask().isEmpty(), "Файл загружен с ошибками, список задач Task должен быть пустрой");
        Assertions.assertTrue(manager.getListEpicTask().isEmpty(), "Файл загружен с ошибками, список задач EpicTask должен быть пустрой");
        Assertions.assertTrue(manager.getListSubTask().isEmpty(), "Файл загружен с ошибками, список задач SubTask должен быть пустрой");
    }

    @Test
    public void shouldIsEmptyListHistory() {
        manager.save();
        Assertions.assertTrue(manager.getHistoryManager().getHistory().isEmpty(), "История задач должна быть пустрой");
    }

    @Test
    public void shouldSaveAndLoadTask() throws IOException, InterruptedException {
        KVTaskClient client = new KVTaskClient("http://localhost:");
        Gson gson = new Gson();

        task = new Task("Название одиночной задачи", "Описание одиночной задачи");

        String jsonObject1 = gson.toJson(task);
        String jsonObject2 = gson.toJson(epicTask);
        String jsonObject3 = gson.toJson(subTask);

        client.put("111", jsonObject1);
        client.put("222", jsonObject2);
        client.put("333", jsonObject3);

        Assertions.assertEquals(gson.fromJson(client.load("111"), Task.class), task, "Задача task не была добавлена");
        Assertions.assertEquals(gson.fromJson(client.load("222"), EpicTask.class), epicTask, "Задача subTask не была добавлена");
        Assertions.assertEquals(gson.fromJson(client.load("333"), SubTask.class), subTask, "Задача epicTask не была добавлена");
    }
}

