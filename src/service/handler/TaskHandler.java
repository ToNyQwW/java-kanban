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

        switch (exchange.getRequestMethod()) {
            case "GET" -> {
                if (exchange.getRequestURI().getPath().endsWith("tasks")) {
                    getTasks(exchange);
                } else {
                    getById(exchange);
                }
            }
            case "POST" -> createOrUpdateTask(exchange);
            case "DELETE" -> deleteTask(exchange);
        }
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getTasksList());
        sendResponse(exchange, response, 200);
    }

    @Override
    protected Optional<Task> getTaskFromManager(int id) {
        return taskManager.getTask(id);
    }

    @Override
    protected void removeById(int id) {
        taskManager.removeTask(id);
    }

    @Override
    protected Task taskFromGson(String requestBody) {
        return gson.fromJson(requestBody, Task.class);
    }

    @Override
    protected <T extends Task> void addTask(T task) {
        taskManager.addTask(task);
    }

    @Override
    protected <T extends Task> boolean updateTask(T task) {
        return taskManager.updateTask(task);
    }
}
