package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import modules.EpicTask;

import java.io.IOException;

public class EpicSubTaskHandler extends EpicHandler {

    public EpicSubTaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String parameters = exchange.getRequestURI().getRawQuery();

        if (parameters != null && !parameters.isBlank()) {
            try {
                EpicTask epicTask = manager.getEpicTaskById(getId(parameters));
                writeResponse(exchange, gson.toJson(manager.getListSubEpicTask(epicTask)), 200);
            } catch (IOException | NullPointerException | NumberFormatException e) {
                writeResponse(exchange, "Неверный формат ID", 400);
            }
        } else {
            writeResponse(exchange, "Вы не предали ID. Список доступных задач: " +
                    gson.toJson(manager.getListEpicTask()), 400);
        }
    }
}