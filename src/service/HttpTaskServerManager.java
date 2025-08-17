package service;


import com.sun.net.httpserver.HttpServer;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServerManager {

    public static final int PORT = 8080;

    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServerManager(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
    }

    public void start() {
        /*
        TODO Handlers
         */
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
