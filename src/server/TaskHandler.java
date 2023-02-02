package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
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

public class TaskHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestMethod());
       // String parametrs = exchange.getRequestURI().getRawQuery();
        URI url = exchange.getRequestURI();
        Gson gson = new Gson();

        switch (endpoint) {
            case GET_TASK: {
                writeResponse(exchange, gson.toJson(manager.getTaskById(getId(url))), 200);
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

    private int getId(URI url) {
        int id = -1;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                id = jsonObject.get("id").getAsInt();
            } else {
                System.out.println("Что-то пошло не так. Сервер вернул код состояния: " + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
        return id;
    }

    private Endpoint getEndpoint(String requestMethod) {
        if (requestMethod.equals("GET")){
            return Endpoint.GET_TASK;
        } else if (requestMethod.equals("POST")){
            return Endpoint.POST_TASK;
        } else if (requestMethod.equals("DELETE")) {
            return Endpoint.DELETE_TASK;
        } else return Endpoint.UNKNOWN;
    }

    private void writeResponse(HttpExchange exchange,
                               String responseString,
                               int responseCode) throws IOException {
        exchange.sendResponseHeaders(responseCode, 0);

        if (!responseString.isEmpty()){
            try ( OutputStream os = exchange.getResponseBody()) {
                os.write(responseString.getBytes(DEFAULT_CHARSET));
            }
        }
    }

}
