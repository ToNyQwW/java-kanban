package service.Handler;

import com.sun.net.httpserver.HttpExchange;
import service.interfaces.TaskManager;

import java.io.IOException;

public class SubTaskHandler extends BaseHttpHandler  {

    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

    }
}
