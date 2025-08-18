package service.handler;

import com.google.gson.Gson;
import model.EpicTask;
import model.SubTask;
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
import static service.handler.BaseHttpHandler.INVALID_REQUEST;
import static service.handler.BaseHttpHandler.NOT_FOUND;

class SubTaskHandlerTest {

    private static String uri = "http://localhost:8080/subtasks";

    private static TaskManager taskManager;
    private static HttpServerManager server;
    private static HttpClient client;
    private static Gson gson;

    private static EpicTask epicTask;
    private static SubTask subTask;

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
    }

    @AfterAll
    static void tearDown() {
        server.stop();
    }

    @Test
    void getSubTaskByIdTest() throws IOException, InterruptedException {
        String stringId = String.valueOf(subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/" + stringId))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "subTask должен быть найден (200)");
        SubTask taskFromResponse = gson.fromJson(response.body(), SubTask.class);
        assertEquals(subTask, taskFromResponse, "response должен вернуть subTask");
    }

    @Test
    void getSubTaskByIdShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/-1"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "subTask должен быть не найден (400)");
        assertEquals(NOT_FOUND, response.body());
    }

    @Test
    void getSubTaskByIdShouldReturn400() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/ErrorRequest"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Не правильный запрос (400)");
        assertEquals(INVALID_REQUEST, response.body());
    }


    @Test
    void getSubTasksTest() throws IOException, InterruptedException {
        taskManager.clearSubtasksMap();
        taskManager.addSubTask(subTask);
        taskManager.addSubTask(new SubTask("subTask2", "", TaskStatus.NEW,
                LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(30), epicTask.getId()));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SubTask> tasks = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());
        assertEquals(tasks, taskManager.getSubTasksList(), "Коллекции должны быть равны");
    }

    @Test
    void deleteSubTaskByIdTest() throws IOException, InterruptedException {
        assertFalse(taskManager.getSubTask(subTask.getId()).isEmpty(), "до удаления задача присутствует");
        String stringId = String.valueOf(subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(uri + "/" + stringId))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getSubTask(subTask.getId()).isEmpty(), "Задача должна быть удалена");
    }

    @Test
    void deleteSubTaskByIdShouldReturn404() throws IOException, InterruptedException {
        taskManager.clearSubtasksMap();
        assertTrue(taskManager.getSubTask(subTask.getId()).isEmpty(), "до удаления задача отсутствует");
        String stringId = String.valueOf(subTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(uri + "/" + stringId))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals(NOT_FOUND, response.body());
    }

    @Test
    void deleteSubTaskByIdShouldReturn400() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(uri + "/ErrorRequest"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals(INVALID_REQUEST, response.body());
    }

    @Test
    void createSubTaskTest() throws IOException, InterruptedException {
        taskManager.clearSubtasksMap();
        SubTask subTaskForTest = new SubTask("subTask2", "", TaskStatus.NEW,
                LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(30), epicTask.getId());
        String jsonTask = gson.toJson(subTaskForTest);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("SubTask created", response.body());
        assertEquals(1, taskManager.getSubTasksList().size());
    }

    @Test
    void updateSubTaskTest() throws IOException, InterruptedException {
        String jsonTask = gson.toJson(new SubTask(subTask.getId(), "SubTaskUpdated", "", TaskStatus.NEW,
                LocalDateTime.now().plusMinutes(60), Duration.ofMinutes(30), epicTask.getId()));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("SubTask updated", response.body());
        assertEquals("SubTaskUpdated", taskManager.getSubTask(subTask.getId()).get().getName(),
                "Информация о задача должна обновится");

    }

    @Test
    void updateSubTaskShouldReturn404() throws IOException, InterruptedException {
        taskManager.clearSubtasksMap();
        String jsonTask = gson.toJson(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals(NOT_FOUND, response.body());
    }

}