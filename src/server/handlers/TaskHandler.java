package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import modules.Task;

import java.io.IOException;

public class TaskHandler extends TaskHandlerAbstract {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
    }

    @Override
    void addTaskInManagerHandler(HttpExchange exchange, String parameters) throws IOException {
        Task task = manager.getTaskById(getId(parameters));
        writeResponse(exchange, gson.toJson(task), 200);
    }

    @Override
    void getListTaskHandler(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getListTask()), 200);
    }

    @Override
    void postTaskHandler(String stringJson) {
        Task task = gson.fromJson(stringJson, Task.class);
        manager.addTask(task);
    }

    @Override
    void updateTaskHandler(String stringJson) {
        Task task = gson.fromJson(stringJson, Task.class);
        manager.updateTask(task);
    }

    @Override
    void deleteTaskHandler(HttpExchange exchange) throws IOException {
        manager.clearTask();
        writeResponse(exchange, "Все задачи Task удалены", 200);
    }

    @Override
    void deleteTaskHandlerById(HttpExchange exchange, String parameters) throws IOException {
        Task task = manager.getTaskById(getId(parameters));
        manager.removeByIdTask(task.getId());
        writeResponse(exchange, "Задача с ID: " + getId(parameters) + " удалена!", 200);
    }
}
