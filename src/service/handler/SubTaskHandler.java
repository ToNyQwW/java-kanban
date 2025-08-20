package service.handler;

import com.sun.net.httpserver.HttpExchange;
import model.SubTask;
import service.exceptions.NotFoundTaskException;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static model.Task.DEFAULT_ID;

public class SubTaskHandler extends BaseHttpHandler {

    private static final String CREATE_SUBTASK = "SubTask created";
    private static final String UPDATE_SUBTASK = "SubTask updated";

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
            sendResponse(exchange, INVALID_REQUEST, 400);
            return;
        }
        Optional<SubTask> subTask = taskManager.getSubTask(id.get());

        if (subTask.isPresent()) {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            sendResponse(exchange, gson.toJson(subTask.get()), 200);
        } else {
            sendResponse(exchange, NOT_FOUND, 404);
        }
    }

    private void getSubTasks(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getSubTasksList());
        sendResponse(exchange, response, 200);
    }

    private void deleteSubTask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, INVALID_REQUEST, 400);
            return;
        }

        try {
            taskManager.removeSubTask(id.get());
            sendResponse(exchange, SUCCESS, 200);
        } catch (NotFoundTaskException e) {
            sendResponse(exchange, NOT_FOUND, 404);
        }
    }

    private void createOrUpdateSubTask(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        SubTask subTask = gson.fromJson(requestBody, SubTask.class);

        if (subTask.getId() == DEFAULT_ID) {
            taskManager.addSubTask(subTask);
            sendResponse(exchange, CREATE_SUBTASK, 201);
            return;
        }

        if (taskManager.updateSubTask(subTask)) {
            sendResponse(exchange, UPDATE_SUBTASK, 201);
        } else {
            sendResponse(exchange, NOT_FOUND, 404);
        }
    }
}
