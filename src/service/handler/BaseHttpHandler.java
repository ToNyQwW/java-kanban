package service.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.GsonTask;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {

    private static final int INDEX_ID = 2;

    private final TaskManager taskManager;
    private final Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        gson = GsonTask.getGson();
    }


    /*
    TODO методы sendResponse
     */


    protected void sendResponse(HttpExchange exchange, String response, int responseCode) {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, response.length());
            os.write(response.getBytes());
        } catch (IOException e) {
            /*
            TODO EXCEPTION
             */
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


    public TaskManager getTaskManager() {
        return taskManager;
    }

    public Gson getGson() {
        return gson;
    }
}
