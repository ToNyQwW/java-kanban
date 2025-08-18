package service.handler;

import com.sun.net.httpserver.HttpExchange;
import model.EpicTask;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static model.Task.DEFAULT_ID;

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
                    getEpicById(exchange);
                }
            }
            case "POST" -> createOrUpdateEpic(exchange);
            case "DELETE" -> deleteEpic(exchange);
        }
    }


    private void getEpicById(HttpExchange exchange) throws IOException {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, "Error", 400);
            return;
        }
        Optional<EpicTask> epicTask = taskManager.getEpicTask(id.get());

        if (epicTask.isPresent()) {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            sendResponse(exchange, gson.toJson(epicTask.get()), 200);
        } else {
            sendResponse(exchange, "Not Found", 404);
        }
    }

    private void getEpicSubTasks(HttpExchange exchange) throws IOException {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, "Error", 400);
            return;
        }
        String response = gson.toJson(taskManager.getSubTasksFromEpicTaskId(id.get()));
        sendResponse(exchange, response, 200);
    }

    private void getEpics(HttpExchange exchange) throws IOException {
        String response = gson.toJson(taskManager.getEpicTasksList());
        sendResponse(exchange, response, 200);
    }

    private void deleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, "Error", 400);
            return;
        }

        try {
            taskManager.removeEpicTask(id.get());
            sendResponse(exchange, "Success", 200);
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, "Not Found", 404);
        }
    }

    private void createOrUpdateEpic(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        EpicTask epicTask = gson.fromJson(requestBody, EpicTask.class);

        if (epicTask.getId() == DEFAULT_ID) {
            taskManager.addEpicTask(epicTask);
            sendResponse(exchange, "EpicTask created", 201);
            return;
        }

        if (taskManager.updateEpicTask(epicTask)) {
            sendResponse(exchange, "EpicTask updated", 201);
        } else {
            sendResponse(exchange, "Not Found", 404);
        }
    }
}
