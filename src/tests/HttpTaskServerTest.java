package tests;

import managers.Managers;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import server.HttpTaskManager;
import server.HttpTaskServer;
import server.KVServer;
import services.Status;

import java.io.IOException;
import java.time.Instant;

public class HttpTaskServerTest {
    protected HttpTaskManager taskManager;
    protected EpicTask epicTask;
    protected SubTask subTask;
    protected Task task;
    private HttpTaskServer server;
    private KVServer kvServer;
    @BeforeEach
    void createHttpServer() throws IOException, InterruptedException {
        taskManager = Managers.getDefault();
        server = new HttpTaskServer(taskManager);
        server.start();
        kvServer = new KVServer();
        kvServer.start();

        epicTask = new EpicTask(1, "Название большой задачи!", Status.NEW, "Описание большой задачи", Instant.now(), 0);
        subTask = new SubTask(2, "Название подзадачи", Status.NEW, "Описание подзадачи", Instant.now(), 180, 1);
        task = new Task(3, "Название одиночной задачи", Status.NEW, "Описание одиночной задачи", Instant.now().plusSeconds(500), 1000);
    }

    @AfterEach
    void stopServer(){
        server.stop();
        kvServer.stop();
    }
}
