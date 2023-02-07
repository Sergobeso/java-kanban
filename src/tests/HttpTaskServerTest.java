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

public class HttpTaskServerTest {
    protected HttpTaskManager manager;
    protected EpicTask epicTask;
    protected SubTask subTask;
    protected Task task;
    private Gson gson;
    private HttpTaskServer server;
    private KVServer kvServer;
    private String getAllTaskUrl = "http://localhost:8080/tasks";
    private String getTaskUrl = "http://localhost:8080/tasks/task";
    private String getEpcTaskUrl = "http://localhost:8080/tasks/epic";
    private String getSubTaskUrl = "http://localhost:8080/tasks/subTask";
    private String getSubTaskEpicUrl = "http://localhost:8080/tasks/subTask/epic";
    private String getHistoryUrl = "http://localhost:8080/tasks/history";

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
    void stopServer(){
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
        } catch (IOException | InterruptedException e){
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
        } catch (IOException | InterruptedException e){
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
            Assertions.assertEquals(task , gson.fromJson(httpResponse.body(), Task.class), "Задачи Task не получена с сервера");
        } catch (IOException | InterruptedException e){
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
            Assertions.assertEquals(1 ,manager.getListTask().size(), "Задачи Task не добавлены на сервер");
        } catch (IOException | InterruptedException e){
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
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}
