package service.Handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import service.interfaces.TaskManager;

public abstract class BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;
    private Gson gson;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    /*
    TODO методы sendResponse
     */
}
