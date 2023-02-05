package server.handlers;

import com.google.gson.JsonIOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import modules.EpicTask;
import modules.SubTask;
import services.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class SubTaskHandler extends TaskHandler implements HttpHandler {
    public SubTaskHandler(TaskManager manager) {
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
                        SubTask subTask = manager.getSubTaskById(getId(parametrs));
                        writeResponse(exchange, gson.toJson(subTask), 200);
                    } catch (IOException | NullPointerException | NumberFormatException e) {
                        writeResponse(exchange, "Неверный ID", 400);
                    }
                } else {
                    writeResponse(exchange, gson.toJson(manager.getListSubTask()), 200);
                }
                break;
            }
            case POST_TASK: {
                try {
                    if (parametrs == null || parametrs.isBlank() || !manager.getSubTaskMap().containsKey(getId(parametrs))) {
                        try (InputStream body = exchange.getRequestBody()) {
                            byte[] byteJson = body.readAllBytes();
                            String stringJson = new String(byteJson, StandardCharsets.UTF_8);
                            SubTask subTask = gson.fromJson(stringJson, SubTask.class);
                            EpicTask epicTask = manager.getEpicTaskById(subTask.getId());
                            manager.addSubTask(subTask,  epicTask);
                            writeResponse(exchange, "Задача успешно добавлена", 200);
                            break;
                        } catch (JsonIOException e) {
                            writeResponse(exchange, "Неверный JSON формат", 400);
                        }
                    } else {
                        try (InputStream body = exchange.getRequestBody()) {
                            byte[] byteJson = body.readAllBytes();
                            String stringJson = new String(byteJson, StandardCharsets.UTF_8);
                            SubTask subTask = gson.fromJson(stringJson, SubTask.class);
                            manager.updateSubTask(subTask);
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
                    manager.clearSubTaskMap();
                    writeResponse(exchange, "Все задачи EpicTask удалены", 200);
                } else {
                    try {
                        SubTask subTask = manager.getSubTaskById(getId(parametrs));
                        manager.removeByIdSubTask(subTask.getId());
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
