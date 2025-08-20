package service.handler;

import com.sun.net.httpserver.HttpExchange;
import model.SubTask;
import model.Task;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class SubTaskHandler extends BaseHttpHandler {

    public SubTaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        switch (exchange.getRequestMethod()) {
            case "GET" -> {
                if (exchange.getRequestURI().getPath().endsWith("subtasks")) {
                    getSubTasks(exchange);
                } else {
                    getById(exchange);
                }
            }
            case "POST" -> createOrUpdateTask(exchange);
            case "DELETE" -> deleteTask(exchange);
        }
    }

    private void getSubTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getSubTasksList());
        sendResponse(exchange, response, 200);
    }

    @Override
    protected Optional<SubTask> getTaskFromManager(int id) {
        return taskManager.getSubTask(id);
    }

    @Override
    protected void removeById(int id) {
        taskManager.removeSubTask(id);
    }

    @Override
    protected SubTask taskFromGson(String requestBody) {
        return gson.fromJson(requestBody, SubTask.class);
    }

    @Override
    protected <T extends Task> void addTask(T task) {
        taskManager.addSubTask((SubTask) task);
    }

    @Override
    protected <T extends Task> boolean updateTask(T task) {
        return taskManager.updateSubTask((SubTask) task);
    }
}
