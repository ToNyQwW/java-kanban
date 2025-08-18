package service.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HttpTaskServerManager;
import service.interfaces.TaskManager;
import util.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryHandlerTest {

    private static String uri = "http://localhost:8080/history";

    private static TaskManager taskManager;
    private static HttpTaskServerManager server;
    private static HttpClient client;
    private static Gson gson;

    private static EpicTask epicTask;
    private static SubTask subTask;
    private static Task task;

    @BeforeAll
    static void setUp() throws IOException {
        taskManager = Managers.getDefault(Files.createTempFile("test", ".txt"));
        server = new HttpTaskServerManager(taskManager);
        client = HttpClient.newHttpClient();
        gson = GsonTask.getGson();
        server.start();
    }

    @BeforeEach
    void setUpEach() throws IOException {
        epicTask = new EpicTask("epicTask", "");
        taskManager.addEpicTask(epicTask);
        subTask = new SubTask("subTask", "", TaskStatus.NEW,
                LocalDateTime.now(), Duration.ofMinutes(30), epicTask.getId());
        taskManager.addSubTask(subTask);
        task = new Task("Task", "", TaskStatus.DONE);
        taskManager.addTask(task);
    }

    @Test
    void getHistoryTest() throws IOException, InterruptedException {
        taskManager.getTask(task.getId());
        taskManager.getSubTask(subTask.getId());
        taskManager.getEpicTask(epicTask.getId());

        HttpRequest request = HttpRequest
                .newBuilder(URI.create(uri))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> history = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(3, history.size());
    }
}