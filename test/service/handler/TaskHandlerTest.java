package service.handler;

import com.google.gson.Gson;
import model.GsonTask;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterAll;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

class TaskHandlerTest {

    private static String uri = "http://localhost:8080/tasks";

    private static TaskManager taskManager;
    private static HttpTaskServerManager server;
    private static HttpClient client;
    private static Gson gson;

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
        task = new Task("Task", "", TaskStatus.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        taskManager.addTask(task);
        System.out.println(task);
    }

    @AfterAll
    static void tearDown() {
        server.stop();
    }


    @Test
    void getTaskByIdTest() throws IOException, InterruptedException {
        String stringId = String.valueOf(task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/" + stringId))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Task должен быть найден (200)");
        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertEquals(task, taskFromResponse, "response должен вернуть task");
    }

    @Test
    void getTaskByIdShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/-1"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Task должен быть не найден (400)");
        assertEquals("Not Found", response.body());
    }
}