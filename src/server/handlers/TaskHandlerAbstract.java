package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import services.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class TaskHandlerAbstract implements HttpHandler {
    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected TaskManager manager;
    protected Gson gson = new Gson();

    public TaskHandlerAbstract(TaskManager manager) {
        this.manager = manager;
    }

    protected int getId(String parametrs) {
        try {
            return Integer.parseInt(parametrs.substring(3));
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
    }

    protected Endpoint getEndpoint(String requestMethod) {
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

    protected void writeResponse(HttpExchange exchange,
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

    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
        String parameters = exchange.getRequestURI().getRawQuery();

        switch (endpoint) {
            case GET_TASK: {
                handleGet(exchange, parameters);
                break;
            }
            case POST_TASK: {
                handlePost(exchange, parameters);
                break;
            }
            case DELETE_TASK: {
                handleDelete(exchange, parameters);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    public void handleGet(HttpExchange exchange, String parameters) throws IOException {
        if (parameters != null && !parameters.isBlank()) {
            try {
                addTaskInManagerHandler(exchange, parameters);
            } catch (NullPointerException | NumberFormatException e) {
                writeResponse(exchange, "Неверный ID", 400);
            }
        } else {
            getListTaskHandler(exchange);
        }
    }

    public void handlePost(HttpExchange exchange, String parameters) throws IOException {
        try {
            if (parameters == null || parameters.isBlank() || !manager.getTaskMap().containsKey(getId(parameters))) {
                try (InputStream body = exchange.getRequestBody()) {
                    byte[] byteJson = body.readAllBytes();
                    String stringJson = new String(byteJson, StandardCharsets.UTF_8);
                    postTaskHandler(stringJson);
                    writeResponse(exchange, "Задача успешно добавлена", 200);
                } catch (JsonIOException e) {
                    writeResponse(exchange, "Неверный JSON формат", 400);
                }
            } else {
                try (InputStream body = exchange.getRequestBody()) {
                    byte[] byteJson = body.readAllBytes();
                    String stringJson = new String(byteJson, StandardCharsets.UTF_8);
                    updateTaskHandler(stringJson);
                    writeResponse(exchange, "Задача успешно обновлена", 200);

                } catch (JsonIOException e) {
                    writeResponse(exchange, "Неверный JSON формат", 400);
                }
            }
        } catch (NumberFormatException e) {
            writeResponse(exchange, "Неверный формат ID", 400);
        }
    }

    public void handleDelete(HttpExchange exchange, String parameters) throws IOException {
        if (parameters == null || parameters.isBlank()) {
            deleteTaskHandler(exchange);
        } else {
            try {
                deleteTaskHandlerById(exchange, parameters);
            } catch (NullPointerException | NumberFormatException e) {
                writeResponse(exchange, "Неверный ID", 400);
            }
        }
    }

    abstract void addTaskInManagerHandler(HttpExchange exchange, String parameters) throws IOException;

    abstract void getListTaskHandler(HttpExchange exchange) throws IOException;

    abstract void postTaskHandler(String stringJson);

    abstract void updateTaskHandler(String stringJson);

    abstract void deleteTaskHandler(HttpExchange exchange) throws IOException;

    abstract void deleteTaskHandlerById(HttpExchange exchange, String parameters) throws IOException;
}