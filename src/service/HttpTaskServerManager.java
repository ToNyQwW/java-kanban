package service;


import com.sun.net.httpserver.HttpServer;
import service.handler.*;
import service.interfaces.HttpServerManager;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServerManager implements HttpServerManager {

    public static final int PORT = 8080;

    private static final String TASKS_CONTEXT = "/tasks";
    private static final String SUBTASKS_CONTEXT = "/subtasks";
    private static final String EPICS_CONTEXT = "/epics";
    private static final String HISTORY_CONTEXT = "/history";
    private static final String PRIORITY_CONTEXT = "/priority";

    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServerManager(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    @Override
    public void start() {
        server.createContext(TASKS_CONTEXT, new TaskHandler(taskManager));
        server.createContext(SUBTASKS_CONTEXT, new SubTaskHandler(taskManager));
        server.createContext(EPICS_CONTEXT, new EpicHandler(taskManager));
        server.createContext(HISTORY_CONTEXT, new HistoryHandler(taskManager));
        server.createContext(PRIORITY_CONTEXT, new PriorityHandler(taskManager));
        server.start();
    }

    @Override
    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }
}
