package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.TaskManager;

import java.io.IOException;

public class AllTasksHandler extends TaskHandler implements HttpHandler {
    public AllTasksHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
    }
}
