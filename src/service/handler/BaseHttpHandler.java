package service.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.interfaces.TaskManager;
import util.GsonTask;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {

    private static final int INDEX_ID = 2;

    protected static final String INVALID_REQUEST = "Invalid Request";
    protected static final String NOT_FOUND = "Not Found";
    protected static final String SUCCESS = "Success";

    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = GsonTask.getGson();
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
}
