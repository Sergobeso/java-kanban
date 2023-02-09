package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import modules.EpicTask;

import java.io.IOException;

public class EpicSubTaskHandler extends TaskHandlerAbstract {

    public EpicSubTaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String parametrs = exchange.getRequestURI().getRawQuery();

        if (parametrs != null && !parametrs.isBlank()) {
            try {
                EpicTask epicTask = manager.getEpicTaskById(getId(parametrs));
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