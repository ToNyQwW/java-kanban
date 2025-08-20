package service.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Task;
import service.exceptions.NotFoundTaskException;
import service.interfaces.TaskManager;
import util.GsonTask;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static model.Task.DEFAULT_ID;

public abstract class BaseHttpHandler implements HttpHandler {

    public static final String INVALID_REQUEST = "Invalid Request";
    public static final String NOT_FOUND = "Not Found";
    public static final String SUCCESS = "Success";
    public static final String CREATE = "Create completed";
    public static final String UPDATE = "Update completed";

    private static final int INDEX_ID = 2;

    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = GsonTask.getGson();
    }

    protected void getById(HttpExchange exchange) throws IOException {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, INVALID_REQUEST, 400);
            return;
        }
        Optional<? extends Task> task = getTaskFromManager(id.get());

        if (task.isPresent()) {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            sendResponse(exchange, gson.toJson(task.get()), 200);
        } else {
            sendResponse(exchange, NOT_FOUND, 404);
        }
    }

    protected void deleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> id = parseIdFromRequest(exchange);

        if (id.isEmpty()) {
            sendResponse(exchange, INVALID_REQUEST, 400);
            return;
        }

        try {
            removeById(id.get());
            sendResponse(exchange, SUCCESS, 200);
        } catch (NotFoundTaskException e) {
            sendResponse(exchange, NOT_FOUND, 404);
        }
    }

    protected void createOrUpdateTask(HttpExchange exchange) throws IOException {
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = taskFromGson(requestBody);

        if (task.getId() == DEFAULT_ID) {
            addTask(task);
            sendResponse(exchange, CREATE, 201);
            return;
        }

        if (updateTask(task)) {
            sendResponse(exchange, UPDATE, 201);
        } else {
            sendResponse(exchange, NOT_FOUND, 404);
        }
    }

    protected void sendResponse(HttpExchange exchange, String response, int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, response.length());
            os.write(response.getBytes());
        }
    }

    protected Optional<Integer> parseIdFromRequest(HttpExchange exchange) {
        String idFromUri = exchange.getRequestURI().getPath().split("/")[INDEX_ID];
        try {
            return Optional.of(Integer.parseInt(idFromUri));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    protected Optional<? extends Task> getTaskFromManager(int id) {
        return Optional.empty();
    }

    protected void removeById(int id) {
    }

    protected <T extends Task> void addTask(T task) {
    }

    protected <T extends Task> boolean updateTask(T task) {
        return false;
    }

    protected <T extends Task> T taskFromGson(String requestBody) {
        return null;
    }
}
