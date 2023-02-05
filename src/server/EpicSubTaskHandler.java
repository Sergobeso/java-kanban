package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import modules.EpicTask;
import modules.SubTask;
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

    private int getId(String parametrs) {
        int id = -1;

        if (!parametrs.isBlank()) {
            id = Integer.parseInt(parametrs.substring(3));
        }
        return id;
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