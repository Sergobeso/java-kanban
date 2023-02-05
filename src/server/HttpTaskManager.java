package server;

import com.google.gson.Gson;
import managers.FileBackedTasksManager;
import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {
    private final Gson gson = new Gson();
    private final KVTaskClient client;



    public HttpTaskManager(String urlServer, int port) throws IOException, InterruptedException {
        client = new KVTaskClient(urlServer, port);

    }

    @Override
    public void save() {
        try {
            client.put("task", gson.toJson(getTaskMap().values()));
            client.put("epicTask", gson.toJson(getEpicTaskMap().values()));
            client.put("subTask", gson.toJson(getSubTaskMap().values()));
            client.put("history", gson.toJson(getHistoryManager().getHistory()));
        } catch (IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
}