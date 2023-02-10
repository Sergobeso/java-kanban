package server;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import managers.FileBackedTasksManager;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson = new Gson();
    private final KVTaskClient client;

    public HttpTaskManager(String urlServer) throws IOException, InterruptedException {
        client = new KVTaskClient(urlServer);
        loadManagerServer();
    }

    public void loadManagerServer() {
        try {
            JsonElement jsonElement = JsonParser.parseString(client.load("task"));
            if (!jsonElement.isJsonNull()) {
                List<Task> task = gson.fromJson(jsonElement, new TypeToken<List<Task>>() {
                }.getType());
                task.forEach(this::addTask);
            }
            jsonElement = JsonParser.parseString(client.load("epicTask"));
            if (!jsonElement.isJsonNull()) {
                List<EpicTask> epicTasks = gson.fromJson(jsonElement, new TypeToken<List<EpicTask>>() {
                }.getType());
                epicTasks.forEach(this::addEpicTask);
            }

            jsonElement = JsonParser.parseString(client.load("subTask"));
            if (!jsonElement.isJsonNull()) {
                List<SubTask> subTasks = gson.fromJson(jsonElement, new TypeToken<List<SubTask>>() {
                }.getType());
                subTasks.stream().map(subTask -> {
                    EpicTask epic = getEpicTaskById(subTask.getEpicId());
                    addSubTask(subTask, epic);
                    return null;
                });
            }

            jsonElement = JsonParser.parseString(client.load("history"));
            if (!jsonElement.isJsonNull()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (JsonElement idStr : jsonArray) {
                    int id = Integer.parseInt(idStr.getAsJsonObject().get("id").getAsString());
                    if (getTaskMap().containsKey(id)) {
                        getHistoryManager().add(getTaskById(id));
                    } else if (getEpicTaskMap().containsKey(id)) {
                        getHistoryManager().add(getEpicTaskById(id));
                    } else if (getSubTaskMap().containsKey(id)) {
                        getHistoryManager().add(getSubTaskById(id));
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save() {
        try {
            client.put("task", gson.toJson(getTaskMap().values()));
            client.put("epicTask", gson.toJson(getEpicTaskMap().values()));
            client.put("subTask", gson.toJson(getSubTaskMap().values()));
            client.put("history", gson.toJson(getHistoryManager().getHistory()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}