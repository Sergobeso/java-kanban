package server;

import com.google.gson.*;
import managers.FileBackedTasksManager;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;

import java.io.*;
import java.util.ArrayList;

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
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (JsonElement jsonElemen : jsonArray) {
                    JsonObject jsonObject = jsonElemen.getAsJsonObject();
                    Task task = gson.fromJson(jsonObject, Task.class);
                    addTask(task);
                }
            }

            jsonElement = JsonParser.parseString(client.load("epicTask"));
            if (!jsonElement.isJsonNull()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (JsonElement jsonElemen : jsonArray) {
                    JsonObject jsonObject = jsonElemen.getAsJsonObject();
                    EpicTask task = gson.fromJson(jsonObject, EpicTask.class);
                    addEpicTask(task);
                }
            }

            jsonElement = JsonParser.parseString(client.load("subTask"));
            if (!jsonElement.isJsonNull()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (JsonElement jsonElemen : jsonArray) {
                    JsonObject jsonObject = jsonElemen.getAsJsonObject();
                    SubTask subTask = gson.fromJson(jsonObject, SubTask.class);
                    EpicTask epicTask = getEpicTaskById(subTask.getEpicId());
                    addSubTask(subTask, epicTask);
                }
            }

            jsonElement = JsonParser.parseString(client.load("history"));
            if (!jsonElement.isJsonNull()) {
                ArrayList<String> list = new ArrayList<>();
                for (String idStr : list) {
                    int id = Integer.parseInt(idStr);
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