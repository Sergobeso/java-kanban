package server;

import com.google.gson.*;
import managers.FileBackedTasksManager;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import services.TypeTask;

import java.io.*;
import java.util.ArrayList;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson = new Gson();
    private final KVTaskClient client;

    public HttpTaskManager(String urlServer, int port) throws IOException, InterruptedException {
        client = new KVTaskClient(urlServer, port);

        try {
            JsonElement jsonElement = JsonParser.parseString(client.load("task"));

            if (jsonElement.isJsonObject()){
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Task task = gson.fromJson(jsonObject, Task.class);
                addTask(task);
            } else {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (JsonElement jsonElemen : jsonArray) {
                    JsonObject jsonObject = jsonElemen.getAsJsonObject();
                    Task task =  gson.fromJson(jsonObject, Task.class);
                    addTask(task);
                }
            }

            jsonElement = JsonParser.parseString(client.load("epicTask"));
            if (jsonElement.isJsonObject()){
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                EpicTask task = gson.fromJson(jsonObject, EpicTask.class);
                addEpicTask(task);
            } else {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (JsonElement jsonElemen : jsonArray) {
                    JsonObject jsonObject = jsonElemen.getAsJsonObject();
                    EpicTask task =  gson.fromJson(jsonObject, EpicTask.class);
                    addEpicTask(task);
                }
            }

            jsonElement = JsonParser.parseString(client.load("subTask"));
            if (jsonElement.isJsonObject()){
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                SubTask subTask = gson.fromJson(jsonObject, SubTask.class);
                EpicTask epicTask = getEpicTaskById(subTask.getEpicId());
                addSubTask(subTask, epicTask);
            } else {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                for (JsonElement jsonElemen : jsonArray) {
                    JsonObject jsonObject = jsonElemen.getAsJsonObject();
                    SubTask subTask = gson.fromJson(jsonObject, SubTask.class);
                    EpicTask epicTask = getEpicTaskById(subTask.getEpicId());
                    addSubTask(subTask, epicTask);
                }
            }

            jsonElement = JsonParser.parseString(client.load("history"));
            if (jsonElement.isJsonArray()) {
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
    } } catch (IllegalStateException e){
        }}

    @Override
    public void save() {
        try {
            client.put("task", gson.toJson(getTaskMap().values()));
            client.put( "epicTask", gson.toJson(getEpicTaskMap().values()));
            client.put("subTask", gson.toJson(getSubTaskMap().values()));
            client.put("history", gson.toJson(getHistoryManager().getHistory()));
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}