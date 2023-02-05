package server.handlers;

import com.google.gson.JsonIOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import modules.EpicTask;
import services.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends TaskHandler implements HttpHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
        String parametrs = exchange.getRequestURI().getRawQuery();

        switch (endpoint) {
            case GET_TASK: {
                if (parametrs != null && !parametrs.isBlank()) {
                    try {
                        EpicTask epicTask = manager.getEpicTaskById(getId(parametrs));
                        writeResponse(exchange, gson.toJson(epicTask), 200);
                    } catch (IOException | NullPointerException | NumberFormatException e) {
                        writeResponse(exchange, "Неверный ID", 400);
                    }
                } else {
                    writeResponse(exchange, gson.toJson(manager.getListEpicTask()), 200);
                }
                break;
            }
            case POST_TASK: {
                try {
                    if (parametrs == null || parametrs.isBlank() || !manager.getEpicTaskMap().containsKey(getId(parametrs))) {
                        try (InputStream body = exchange.getRequestBody()) {
                            byte[] byteJson = body.readAllBytes();
                            String stringJson = new String(byteJson, StandardCharsets.UTF_8);
                            EpicTask epicTask = gson.fromJson(stringJson, EpicTask.class);
                            manager.addEpicTask(epicTask);
                            writeResponse(exchange, "Задача успешно добавлена", 200);
                            break;
                        } catch (JsonIOException e) {
                            writeResponse(exchange, "Неверный JSON формат", 400);
                        }
                    } else {
                        try (InputStream body = exchange.getRequestBody()) {
                            byte[] byteJson = body.readAllBytes();
                            String stringJson = new String(byteJson, StandardCharsets.UTF_8);
                            EpicTask epicTask = gson.fromJson(stringJson, EpicTask.class);
                            manager.updateEpicTaskMap(epicTask);
                            writeResponse(exchange, "Задача успешно обновлена", 200);
                            break;
                        } catch (JsonIOException e) {
                            writeResponse(exchange, "Неверный JSON формат", 400);
                        }
                    }
                } catch (NumberFormatException e) {
                    writeResponse(exchange, "Неверный формат ID", 400);
                }
            }
            case DELETE_TASK: {
                if (parametrs == null || parametrs.isBlank()) {
                    manager.clearEpicTaskMap();
                    writeResponse(exchange, "Все задачи EpicTask удалены", 200);
                } else {
                    try {
                        EpicTask epicTask = manager.getEpicTaskById(getId(parametrs));
                        manager.removeByIdEpicTask(epicTask.getId());
                        writeResponse(exchange, "Задача с ID: " + getId(parametrs) + " удалена!", 200);
                    } catch (IOException | NullPointerException | NumberFormatException e) {
                        writeResponse(exchange, "Неверный ID", 400);
                    }
                }
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }
}