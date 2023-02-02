package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import modules.EpicTask;
import services.Endpoint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class EpicSubTaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private TaskManager manager;
    private Gson gson = new Gson();

    public EpicSubTaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
        String parametrs = exchange.getRequestURI().getRawQuery();

        switch (endpoint) {
            case GET_TASK: {
                EpicTask epicTask = manager.getEpicTaskById(getId(parametrs));
                writeResponse(exchange, gson.toJson(manager.getListSubEpicTask(epicTask)), 200);
                break;
            }
            case POST_TASK: {
                writeResponse(exchange, "Получен запрос на добавление задачи", 200);
                break;
            }
            case DELETE_TASK: {
                writeResponse(exchange, "Получен запрос на удаление задачи", 200);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private int getId(String parametrs) {
        int id = -1;

        if (!parametrs.isBlank()) {
            id = Integer.parseInt(parametrs.substring(3));
        }
        return id;
    }

    private Endpoint getEndpoint(String requestMethod) {
        switch (requestMethod) {
            case "GET":
                return Endpoint.GET_TASK;
            case "POST":
                return Endpoint.POST_TASK;
            case "DELETE":
                return Endpoint.DELETE_TASK;
            default:
                return Endpoint.UNKNOWN;
        }
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        if (responseString.isBlank()) {
            exchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
            exchange.sendResponseHeaders(responseCode, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

}