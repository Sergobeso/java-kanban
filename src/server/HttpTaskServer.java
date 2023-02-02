package server;

import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTasksManager;
import managers.Managers;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    public HttpServer httpServer;
    FileBackedTasksManager taskManager;

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public HttpTaskServer(FileBackedTasksManager taskManager) throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);

       // FileBackedTasksManager taskManager = Managers.getDefaultFBTM();
        this.taskManager = taskManager;

        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/task", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/epic", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/subTask", new TaskHandler(taskManager));
        httpServer.createContext("/tasks/history", new TaskHandler(taskManager));
    }

    public void start(){
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop(){
        httpServer.stop(1);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
    }
}
