package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import modules.EpicTask;

import java.io.IOException;

public class EpicHandler extends TaskHandlerAbstract {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
    }

    void addTaskInManagerHandler(HttpExchange exchange, String parameters) throws IOException {
        EpicTask epicTask = manager.getEpicTaskById(getId(parameters));
        writeResponse(exchange, gson.toJson(epicTask), 200);
    }

    @Override
    void getListTaskHandler(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getListEpicTask()), 200);
    }

    @Override
    void postTaskHandler(String stringJson) {
        EpicTask epicTask = gson.fromJson(stringJson, EpicTask.class);
        manager.addEpicTask(epicTask);
    }

    @Override
    void updateTaskHandler(String stringJson) {
        EpicTask epicTask = gson.fromJson(stringJson, EpicTask.class);
        manager.updateEpicTaskMap(epicTask);
    }

    @Override
    void deleteTaskHandler(HttpExchange exchange) throws IOException {
        manager.clearEpicTaskMap();
        writeResponse(exchange, "Все задачи EpicTask удалены", 200);
    }

    @Override
    void deleteTaskHandlerById(HttpExchange exchange, String parameters) throws IOException {
        EpicTask epicTask = manager.getEpicTaskById(getId(parameters));
        manager.removeByIdEpicTask(epicTask.getId());
        writeResponse(exchange, "Задача с ID: " + getId(parameters) + " удалена!", 200);
    }
}