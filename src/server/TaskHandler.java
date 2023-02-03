package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import modules.Task;
import services.Endpoint;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

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
                if (parametrs!=null || !parametrs.isBlank()){
                    try{
                        Task task = manager.getTaskById(getId(parametrs));
                        if (task != null) {
                            writeResponse(exchange, gson.toJson(task), 200);
                        } else {
                            writeResponse(exchange, "Такого ID нет среди задач", 200);
                        }
                   } catch (IOException e){
                        writeResponse(exchange, "Проверьте ID неверный формат", 200);
                    }
                } else  {
                    writeResponse(exchange, gson.toJson(manager.getListTask()), 200);
                }
                break;
            }
            case POST_TASK: {
                writeResponse(exchange, "Получен запрос на добавление задачи", 200);
                break;
            }
            case DELETE_TASK: {
                if (!parametrs.isBlank()){
                    manager.clearTask();
                    writeResponse(exchange, "Все задачи Task удалены", 200);
                } else  {
                    manager.removeByIdTask(getId(parametrs));
                    writeResponse(exchange, "Задача с ID: "+ getId(parametrs) + "удалена!", 200);
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
