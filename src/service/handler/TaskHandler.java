package service.handler;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestMethod = exchange.getRequestMethod();
    }

    private void getTaskById(HttpExchange exchange) {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, "Error", 400);
        }
        Optional<Task> task = getTaskManager().getTask(id.get());

        if (task.isPresent()) {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            sendResponse(exchange, getGson().toJson(task.get()), 200);
        } else {
            sendResponse(exchange, "Not Found", 404);
        }
    }
}
