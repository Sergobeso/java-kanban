package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import java.io.IOException;

public class EpicHandler extends TaskHandlerAbstract {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        super.handle(exchange);
    }
}