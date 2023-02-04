package server;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import modules.Task;
import services.Endpoint;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private TaskManager manager;
    private Gson gson = new Gson();

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
        String parametrs = exchange.getRequestURI().getRawQuery();

        switch (endpoint) {
            case GET_TASK: {
                if (parametrs!=null && !parametrs.isBlank()){
                    try{
                        Task task = manager.getTaskById(getId(parametrs));
                        System.out.println(gson.toJson(task));
                        writeResponse(exchange, gson.toJson(task), 200);
                   } catch (IOException | NullPointerException | NumberFormatException e ){
                        writeResponse(exchange, "Неверный ID", 400);
                    }
                } else  {
                    writeResponse(exchange, gson.toJson(manager.getListTask()), 200);
                }
                break;
            }
            case POST_TASK: {
                try {
                    if (parametrs == null || parametrs.isBlank() || !manager.getTaskMap().containsKey(getId(parametrs))) {
                        try (InputStream body = exchange.getRequestBody()) {
                            byte[] byteJson = body.readAllBytes();
                            String stringJson = new String(byteJson, StandardCharsets.UTF_8);
                            Task task = gson.fromJson(stringJson, Task.class);
                            manager.addTask(task);
                            writeResponse(exchange, "Задача успешно добавлена", 200);
                            break;
                        } catch (JsonIOException e) {
                            writeResponse(exchange, "Неверный JSON формат", 400);
                        }
                    } else {
                        try (InputStream body = exchange.getRequestBody()) {
                            byte[] byteJson = body.readAllBytes();
                            String stringJson = new String(byteJson, StandardCharsets.UTF_8);
                            Task task = gson.fromJson(stringJson, Task.class);
                            manager.updateTask(task);
                            writeResponse(exchange, "Задача успешно обновлена", 200);
                            break;
                        } catch (JsonIOException e) {
                            writeResponse(exchange, "Неверный JSON формат", 400);
                        }
                    }
                } catch (NumberFormatException e){
                    writeResponse(exchange, "Неверный формат ID", 400);
                }
            }
            case DELETE_TASK: {
                if (parametrs==null || parametrs.isBlank()){
                    manager.clearTask();
                    writeResponse(exchange, "Все задачи Task удалены", 200);
                } else  {
                    try {
                        Task task = manager.getTaskById(getId(parametrs));
                        manager.removeByIdTask(task.getId());
                        writeResponse(exchange, "Задача с ID: "+ getId(parametrs) + " удалена!", 200);
                    } catch (IOException | NullPointerException | NumberFormatException e ){
                        writeResponse(exchange, "Неверный ID", 400);
                    }
                }
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    private int getId(String parametrs) {
       try {
           return Integer.parseInt(parametrs.substring(3));
        } catch (NumberFormatException e){
         throw  new NumberFormatException();
       }
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
