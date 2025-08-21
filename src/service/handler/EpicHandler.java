package service.handler;

import com.sun.net.httpserver.HttpExchange;
import model.EpicTask;
import model.Task;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        switch (exchange.getRequestMethod()) {
            case "GET" -> {
                if (exchange.getRequestURI().getPath().endsWith("epics")) {
                    getEpics(exchange);
                } else if (exchange.getRequestURI().getPath().endsWith("subtasks")) {
                    getEpicSubTasks(exchange);
                } else {
                    getById(exchange);
                }
            }
            case "POST" -> createOrUpdateTask(exchange);
            case "DELETE" -> deleteTask(exchange);
        }
    }

    private void getEpicSubTasks(HttpExchange exchange) throws IOException {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, INVALID_REQUEST, 400);
            return;
        }
        String response = gson.toJson(taskManager.getSubTasksFromEpicTaskId(id.get()));
        sendResponse(exchange, response, 200);
    }

    private void getEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getEpicTasksList());
        sendResponse(exchange, response, 200);
    }

    @Override
    protected Optional<EpicTask> getTaskFromManager(int id) {
        return taskManager.getEpicTask(id);
    }

    @Override
    protected void removeById(int id) {
        taskManager.removeEpicTask(id);
    }

    @Override
    protected EpicTask taskFromGson(String requestBody) {
        return gson.fromJson(requestBody, EpicTask.class);
    }

    @Override
    protected <T extends Task> void addTask(T task) {
        taskManager.addEpicTask((EpicTask) task);
    }

    @Override
    protected <T extends Task> boolean updateTask(T task) {
        return taskManager.updateEpicTask((EpicTask) task);
    }
}
