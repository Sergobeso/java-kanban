package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import java.io.IOException;
public class SubTaskHandler extends TaskHandlerAbstract {
    public SubTaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
    }
}
