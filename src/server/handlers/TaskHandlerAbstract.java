package server.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import services.Endpoint;
import services.TypeTask;

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
        TypeTask typeTask = getTypeTask(exchange.getRequestURI().getPath());

        switch (endpoint) {
            case GET_TASK: {
                handleGet(exchange, typeTask, parameters);
                break;
            }
            case POST_TASK: {
                handlePost(exchange, typeTask, parameters);
                break;
            }
            case DELETE_TASK: {
                handleDelete(exchange, typeTask, parameters);
                break;
            }
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 404);
        }
    }

    public void handleGet(HttpExchange exchange, TypeTask typeTask, String parameters) throws IOException {
        if (parameters != null && !parameters.isBlank()) {
            try {
                switch (typeTask) {
                    case TASK: {
                        Task task = manager.getTaskById(getId(parameters));
                        writeResponse(exchange, gson.toJson(task), 200);
                        break;
                    }
                    case EPICTASK: {
                        EpicTask epicTask = manager.getEpicTaskById(getId(parameters));
                        writeResponse(exchange, gson.toJson(epicTask), 200);
                        break;
                    }
                    case SUBTASK: {
                        SubTask subTask = manager.getSubTaskById(getId(parameters));
                        writeResponse(exchange, gson.toJson(subTask), 200);
                        break;
                    }
                }
            } catch (IOException | NullPointerException | NumberFormatException e) {
                writeResponse(exchange, "Неверный ID", 400);
            }
        } else {
            switch (typeTask){
                case TASK:
                    writeResponse(exchange, gson.toJson(manager.getListTask()), 200);
                case EPICTASK:
                    writeResponse(exchange, gson.toJson(manager.getListEpicTask()), 200);
                case SUBTASK:
                    writeResponse(exchange, gson.toJson(manager.getListSubTask()), 200);
            }
        }
    }

    public void handlePost(HttpExchange exchange, TypeTask typeTask, String parameters) throws IOException {
        try {
            if (parameters == null || parameters.isBlank() || !manager.getTaskMap().containsKey(getId(parameters))) {
                try (InputStream body = exchange.getRequestBody()) {
                    byte[] byteJson = body.readAllBytes();
                    String stringJson = new String(byteJson, StandardCharsets.UTF_8);
                    switch (typeTask) {
                        case TASK: {
                            Task task = gson.fromJson(stringJson, Task.class);
                            manager.addTask(task);
                            writeResponse(exchange, "Задача успешно добавлена", 200);
                        }
                        case EPICTASK: {
                            EpicTask epicTask = gson.fromJson(stringJson, EpicTask.class);
                            manager.addEpicTask(epicTask);
                            writeResponse(exchange, "Задача успешно добавлена", 200);
                        }
                        case SUBTASK: {
                            SubTask subTask = gson.fromJson(stringJson, SubTask.class);
                            int idEpic = subTask.getEpicId();

                            EpicTask epicTask = manager.getEpicTaskById(idEpic);
                            manager.addSubTask(subTask, epicTask);
                            writeResponse(exchange, "Задача успешно добавлена", 200);
                        }
                    }
                } catch (JsonIOException e) {
                    writeResponse(exchange, "Неверный JSON формат", 400);
                }
            } else {
                try (InputStream body = exchange.getRequestBody()) {
                    byte[] byteJson = body.readAllBytes();
                    String stringJson = new String(byteJson, StandardCharsets.UTF_8);

                    switch (typeTask) {
                        case TASK: {
                            Task task = gson.fromJson(stringJson, Task.class);
                            manager.updateTask(task);
                            writeResponse(exchange, "Задача успешно обновлена", 200);
                        }
                        case EPICTASK: {
                            EpicTask epicTask = gson.fromJson(stringJson, EpicTask.class);
                            manager.updateEpicTaskMap(epicTask);
                            writeResponse(exchange, "Задача успешно обновлена", 200);
                        }
                        case SUBTASK: {
                            SubTask subTask = gson.fromJson(stringJson, SubTask.class);
                            manager.updateSubTask(subTask);
                            writeResponse(exchange, "Задача успешно обновлена", 200);
                        }
                    }
                } catch (JsonIOException e) {
                    writeResponse(exchange, "Неверный JSON формат", 400);
                }
            }
        } catch (NumberFormatException e) {
            writeResponse(exchange, "Неверный формат ID", 400);
        }
    }

    public void handleDelete(HttpExchange exchange, TypeTask typeTask, String parameters) throws IOException {
        if (parameters == null || parameters.isBlank()) {
            switch (typeTask) {
                case TASK: {
                    manager.clearTask();
                    writeResponse(exchange, "Все задачи Task удалены", 200);
                }
                case EPICTASK: {
                    manager.clearEpicTaskMap();
                    writeResponse(exchange, "Все задачи EpicTask удалены", 200);
                }
                case SUBTASK: {
                    manager.clearSubTaskMap();
                    writeResponse(exchange, "Все задачи EpicTask удалены", 200);
                }
            }
        } else {
            try {
                switch (typeTask) {
                    case TASK: {
                        Task task = manager.getTaskById(getId(parameters));
                        manager.removeByIdTask(task.getId());
                        writeResponse(exchange, "Задача с ID: " + getId(parameters) + " удалена!", 200);
                    }
                    case EPICTASK: {
                        EpicTask epicTask = manager.getEpicTaskById(getId(parameters));
                        manager.removeByIdEpicTask(epicTask.getId());
                        writeResponse(exchange, "Задача с ID: " + getId(parameters) + " удалена!", 200);
                    }
                    case SUBTASK: {
                        SubTask subTask = manager.getSubTaskById(getId(parameters));
                        manager.removeByIdSubTask(subTask.getId());
                        writeResponse(exchange, "Задача с ID: " + getId(parameters) + " удалена!", 200);
                    }
                }
            } catch (IOException | NullPointerException | NumberFormatException e) {
                writeResponse(exchange, "Неверный ID", 400);
            }
        }
    }

    public TypeTask getTypeTask(String url) {
        String typeTask = url.split("/")[2];
        switch (typeTask) {
            case "task":
                return TypeTask.TASK;
            case "epic":
                return TypeTask.EPICTASK;
            default:
                return TypeTask.SUBTASK;
        }
    }
}
