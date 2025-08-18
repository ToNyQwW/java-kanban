package service.handler;

import com.sun.net.httpserver.HttpExchange;
import service.interfaces.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {


    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
            sendResponse(exchange, gson.toJson(taskManager.getHistory()), 200);
        } else {
            sendResponse(exchange, "Error", 400);
        }
    }
}
