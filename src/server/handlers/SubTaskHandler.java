package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import modules.EpicTask;
import modules.SubTask;
import java.io.IOException;

public class SubTaskHandler extends TaskHandlerAbstract {
    public SubTaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
    }

    void addTaskInManagerHandler(HttpExchange exchange, String parameters) throws IOException {
        SubTask subTask = manager.getSubTaskById(getId(parameters));
        writeResponse(exchange, gson.toJson(subTask), 200);
    }

    @Override
    void getListTaskHandler(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getListSubTask()), 200);
    }

    @Override
    void postTaskHandler(String stringJson) {
        SubTask subTask = gson.fromJson(stringJson, SubTask.class);
        int idEpic = subTask.getEpicId();
        EpicTask epicTask = manager.getEpicTaskById(idEpic);
        manager.addSubTask(subTask, epicTask);
    }

    @Override
    void updateTaskHandler(String stringJson) {
        SubTask subTask = gson.fromJson(stringJson, SubTask.class);
        manager.updateSubTask(subTask);
    }

    @Override
    void deleteTaskHandler(HttpExchange exchange) throws IOException {
        manager.clearSubTaskMap();
        writeResponse(exchange, "Все задачи EpicTask удалены", 200);
    }

    @Override
    void deleteTaskHandlerById(HttpExchange exchange, String parameters) throws IOException {
        SubTask subTask = manager.getSubTaskById(getId(parameters));
        manager.removeByIdSubTask(subTask.getId());
        writeResponse(exchange, "Задача с ID: " + getId(parameters) + " удалена!", 200);
    }
}
