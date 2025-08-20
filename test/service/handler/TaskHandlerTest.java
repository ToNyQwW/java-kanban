package service.handler;

import com.google.gson.Gson;
import model.Task;
import model.TaskStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.HttpTaskServerManager;
import service.interfaces.HttpServerManager;
import service.interfaces.TaskManager;
import util.GsonTask;
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
import static service.handler.BaseHttpHandler.*;

class TaskHandlerTest {

    private static String uri = "http://localhost:8080/tasks";

    private static TaskManager taskManager;
    private static HttpServerManager server;
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
        assertEquals(NOT_FOUND, response.body());
    }

    @Test
    void getTaskByIdShouldReturn400() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/ErrorRequest"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Не правильный запрос (400)");
        assertEquals(INVALID_REQUEST, response.body());
    }


    @Test
    void getTasksTest() throws IOException, InterruptedException {
        taskManager.clearTasksMap();
        taskManager.addTask(task);
        taskManager.addTask(new Task("Task", "", TaskStatus.NEW,
                LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(30)));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());
        assertEquals(tasks, taskManager.getTasksList(), "Коллекции должны быть равны");
    }

    @Test
    void deleteTaskByIdTest() throws IOException, InterruptedException {
        assertFalse(taskManager.getTask(task.getId()).isEmpty(), "до удаления задача присутствует");
        String stringId = String.valueOf(task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(uri + "/" + stringId))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getTask(task.getId()).isEmpty(), "Задача должна быть удалена");
    }

    @Test
    void deleteTaskByIdShouldReturn404() throws IOException, InterruptedException {
        taskManager.clearTasksMap();
        assertTrue(taskManager.getTask(task.getId()).isEmpty(), "до удаления задача отсутствует");
        String stringId = String.valueOf(task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(uri + "/" + stringId))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals(NOT_FOUND, response.body());
    }

    @Test
    void deleteTaskByIdShouldReturn400() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(uri + "/ErrorRequest"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals(INVALID_REQUEST, response.body());
    }

    @Test
    void createTaskTest() throws IOException, InterruptedException {
        taskManager.clearTasksMap();
        Task taskForTest = new Task("Task", "", TaskStatus.NEW,
                LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(30));
        String jsonTask = gson.toJson(taskForTest);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(CREATE, response.body());
        assertEquals(1, taskManager.getTasksList().size());
    }

    @Test
    void updateTaskTest() throws IOException, InterruptedException {
        String jsonTask = gson.toJson(new Task(task.getId(), "TaskUpdated", "", TaskStatus.NEW,
                LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(30)));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(UPDATE, response.body());
        assertEquals("TaskUpdated", taskManager.getTask(task.getId()).get().getName(),
                "Информация о задача должна обновится");

    }

    @Test
    void updateTaskShouldReturn404() throws IOException, InterruptedException {
        taskManager.clearTasksMap();
        String jsonTask = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals(NOT_FOUND, response.body());
    }
}