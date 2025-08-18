package service.handler;

import com.sun.net.httpserver.HttpExchange;
import model.SubTask;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static model.Task.DEFAULT_ID;

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
                    getSubTaskById(exchange);
                }
            }
            case "POST" -> createOrUpdateSubTask(exchange);
            case "DELETE" -> deleteSubTask(exchange);
        }
    }

    private void getSubTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, "Error", 400);
        }
        Optional<SubTask> subTask = taskManager.getSubTask(id.get());

        if (subTask.isPresent()) {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            sendResponse(exchange, gson.toJson(subTask.get()), 200);
        } else {
            sendResponse(exchange, "Not Found", 404);
        }
    }

    private void getSubTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getSubTasksList());
        sendResponse(exchange, response, 200);
    }

    private void deleteSubTask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, "Error", 400);
        }

        try {
            taskManager.removeSubTask(id.get());
            sendResponse(exchange, "Success", 200);
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, "Not Found", 404);
        }
    }

    private void createOrUpdateSubTask(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        SubTask subTask = gson.fromJson(requestBody, SubTask.class);

        if (subTask.getId() == DEFAULT_ID) {
            taskManager.addSubTask(subTask);
            sendResponse(exchange, "SubTask created", 201);
            return;
        }

        if (taskManager.updateSubTask(subTask)) {
            sendResponse(exchange, "SubTask updated", 201);
        } else {
            sendResponse(exchange, "Not Found", 404);
        }
    }
}
