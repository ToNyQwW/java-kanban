package service.handler;

import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static model.Task.DEFAULT_ID;

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
                    getTaskById(exchange);
                }
            }
            case "POST" -> createOrUpdateTask(exchange);
            case "DELETE" -> deleteTask(exchange);
        }
    }

    private void getTaskById(HttpExchange exchange) throws IOException {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, "Error", 400);
            return;
        }
        Optional<Task> task = taskManager.getTask(id.get());

        if (task.isPresent()) {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            sendResponse(exchange, gson.toJson(task.get()), 200);
        } else {
            sendResponse(exchange, "Not Found", 404);
        }
    }

    private void getTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getTasksList());
        sendResponse(exchange, response, 200);
    }

    private void deleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, "Error", 400);
            return;
        }

        try {
            taskManager.removeTask(id.get());
            sendResponse(exchange, "Success", 200);
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, "Not Found", 404);
        }
    }

    private void createOrUpdateTask(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(requestBody, Task.class);

        if (task.getId() == DEFAULT_ID) {
            taskManager.addTask(task);
            sendResponse(exchange, "Task created", 201);
            return;
        }

        if (taskManager.updateTask(task)) {
            sendResponse(exchange, "Task updated", 201);
        } else {
            sendResponse(exchange, "Not Found", 404);
        }
    }
}
