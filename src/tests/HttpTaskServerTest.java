package tests;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import managers.Managers;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskManager;
import server.HttpTaskServer;
import server.KVServer;
import services.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

/**
 * Класс описывающий реализацию ТЕСТОВ менеджера HttpTaskServerTest.
 */

public class HttpTaskServerTest {
    private HttpTaskManager manager;
    private EpicTask epicTask;
    private SubTask subTask;
    private Task task;
    private Gson gson;
    private HttpTaskServer server;
    private KVServer kvServer;
    private String getAllTaskUrl = "http://localhost:8080/tasks";
    private final String getTaskUrl = "http://localhost:8080/tasks/task";
    private final String getEpcTaskUrl = "http://localhost:8080/tasks/epic";
    private final String getSubTaskUrl = "http://localhost:8080/tasks/subTask";
    private final String getSubTaskEpicUrl = "http://localhost:8080/tasks/epic/subTask";
    private final String getHistoryUrl = "http://localhost:8080/tasks/history";

    @BeforeEach
    void createHttpServer() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        server.start();
        gson = new Gson();

        epicTask = new EpicTask(1, "Название большой задачи!", Status.NEW, "Описание большой задачи", Instant.now(), 0);
        subTask = new SubTask(2, "Название подзадачи", Status.NEW, "Описание подзадачи", Instant.now(), 180, 1);
        task = new Task(3, "Название одиночной задачи", Status.NEW, "Описание одиночной задачи", Instant.now().plusSeconds(500), 1000);
    }

    @AfterEach
    void stopServer() {
        server.stop();
        kvServer.stop();
    }

    @Test
    public void shouldSaveAndGetAllTask() {
        manager.addEpicTask(epicTask);
        manager.addSubTask(subTask, epicTask);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getAllTaskUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray array = JsonParser.parseString(httpResponse.body()).getAsJsonArray();
            Assertions.assertEquals(3, array.size(), "Задачи не добавлены на сервер");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSaveAndGetTask() {
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getTaskUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray array = JsonParser.parseString(httpResponse.body()).getAsJsonArray();
            Assertions.assertEquals(1, array.size(), "Задачи Task не добавлены на сервер");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSaveAndGetTaskById() {
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getTaskUrl + "/?id=1");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(task, gson.fromJson(httpResponse.body(), Task.class), "Задачи Task не получена с сервера");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldPostTask() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getTaskUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task))).build();

        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(1, manager.getListTask().size(), "Задачи Task не добавлены на сервер");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSDeleteTask() {
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getTaskUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(0, manager.getListTask().size(), "Задачи Task не были удалены с сервера");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSDeleteTaskById() {
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getTaskUrl + "/?id=1");

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(0, manager.getListTask().size(), "Задачи Task не были удалены с сервера");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSaveAndGetEpicTask() {
        manager.addEpicTask(epicTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getEpcTaskUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray array = JsonParser.parseString(httpResponse.body()).getAsJsonArray();
            Assertions.assertEquals(1, array.size(), "Задачи EpicTask не добавлены на сервер");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSaveAndGetEpicTaskById() {
        manager.addEpicTask(epicTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getEpcTaskUrl + "/?id=1");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(epicTask, gson.fromJson(httpResponse.body(), EpicTask.class), "Задачи EpicTask ID не получена с сервера");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldPostEpicTask() {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getEpcTaskUrl);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epicTask))).build();

        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(1, manager.getListEpicTask().size(), "Задачи EpicTask не добавлены на сервер");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSDeleteEpicTask() {
        manager.addEpicTask(epicTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getEpcTaskUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(0, manager.getListEpicTask().size(), "Задачи EpicTask не были удалены с сервера");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSDeleteEpicTaskById() {
        manager.addEpicTask(epicTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getEpcTaskUrl + "/?id=1");

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(0, manager.getListEpicTask().size(), "Задачи EpicTask не были удалены с сервера");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSaveAndGetSubTask() {
        manager.addEpicTask(epicTask);
        manager.addSubTask(subTask, epicTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getSubTaskUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray array = JsonParser.parseString(httpResponse.body()).getAsJsonArray();
            Assertions.assertEquals(1, array.size(), "Задачи SubTask не добавлены на сервер");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldSaveAndGetSubTaskById() {
        manager.addEpicTask(epicTask);
        manager.addSubTask(subTask, epicTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getSubTaskUrl + "/?id=2");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(subTask, gson.fromJson(httpResponse.body(), SubTask.class), "Задачи SubTask ID не получена с сервера");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldPostSubTask() {
        EpicTask epicTask1 = new EpicTask("Имя 1 большой задачи", "Описание  1 большой задачи");
        manager.addEpicTask(epicTask1);
        SubTask subTask1 = new SubTask("Название подзадачи", "Описание подзадачи", Instant.now(), 180, epicTask1.getId());


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getSubTaskUrl);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask1)))
                .build();

        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(1, manager.getListSubTask().size(), "Задачи SubTask не добавлены на сервер");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void shouldDeleteSubTask() {
        manager.addEpicTask(epicTask);
        manager.addSubTask(subTask, epicTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getSubTaskUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(0, manager.getListSubTask().size(), "Задачи SubTask не были удалены с сервера");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldDeleteSubTaskById() {
        manager.addEpicTask(epicTask);
        manager.addSubTask(subTask, epicTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getSubTaskUrl + "/?id=2");

        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            Assertions.assertEquals(0, manager.getListSubTask().size(), "Задачи Task не были удалены с сервера");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldDEpicSubTaskHandler() {
        manager.addEpicTask(epicTask);
        manager.addSubTask(subTask, epicTask);
        manager.addSubTask(subTask, epicTask);


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getSubTaskEpicUrl + "/?id=1");

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray array = JsonParser.parseString(httpResponse.body()).getAsJsonArray();
            Assertions.assertEquals(2, array.size(), "Не найти задачи SubTask по ID EpicTask");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void shouldHistoryHandler() {
        manager.addTask(task);
        manager.getTaskById(task.getId());
        manager.addEpicTask(epicTask);
        manager.getEpicTaskById(epicTask.getId());
        manager.getTaskById(task.getId());


        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(getHistoryUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        try {
            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray array = JsonParser.parseString(httpResponse.body()).getAsJsonArray();
            Assertions.assertEquals(2, array.size(), "Задачи в истории просмотра задач не совпадают");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

}
