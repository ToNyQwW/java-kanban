package service.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

class EpicHandlerTest extends TypeToken<List<EpicTask>> {

    private static String uri = "http://localhost:8080/epics";

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
    void setUpEach(){
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
    void getEpicByIdTest() throws IOException, InterruptedException {
        String stringId = String.valueOf(epicTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/" + stringId))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "epicTask должен быть найден (200)");
        EpicTask taskFromResponse = gson.fromJson(response.body(), EpicTask.class);
        assertEquals(epicTask, taskFromResponse, "response должен вернуть epicTask");
    }

    @Test
    void getEpicByIdShouldReturn404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/-1"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "epicTask должен быть не найден (400)");
        assertEquals(NOT_FOUND, response.body());
    }

    @Test
    void getEpicByIdShouldReturn400() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/ErrorRequest"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode(), "Не правильный запрос (400)");
        assertEquals(INVALID_REQUEST, response.body());
    }


    @Test
    void getEpicsTest() throws IOException, InterruptedException {
        taskManager.clearEpicTasksMap();
        taskManager.addEpicTask(epicTask);
        taskManager.addEpicTask(new EpicTask("epicTask2", ""));
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<EpicTask> epicTasks = gson.fromJson(response.body(), new EpicTaskListTypeToken().getType());
        assertEquals(epicTasks, taskManager.getEpicTasksList(), "Коллекции должны быть равны");
    }

    @Test
    void getEpicSubTasksTest() throws IOException, InterruptedException {
        taskManager.addSubTask(new SubTask("SubTask2", "", TaskStatus.NEW, epicTask.getId()));
        String stringId = String.valueOf(epicTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri + "/" + stringId + "/subtasks"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        List<SubTask> epicTasks = gson.fromJson(response.body(), new SubTaskListTypeToken().getType());
        assertEquals(epicTasks, taskManager.getSubTasksFromEpicTaskId(epicTask.getId()),
                "Коллекции должны быть равны");
    }

    @Test
    void deleteEpicTaskByIdTest() throws IOException, InterruptedException {
        assertFalse(taskManager.getEpicTask(epicTask.getId()).isEmpty(), "до удаления задача присутствует");
        String stringId = String.valueOf(epicTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(uri + "/" + stringId))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(taskManager.getSubTask(epicTask.getId()).isEmpty(), "Задача должна быть удалена");
    }

    @Test
    void deleteEpicTaskByIdShouldReturn404() throws IOException, InterruptedException {
        taskManager.clearEpicTasksMap();
        assertTrue(taskManager.getEpicTask(epicTask.getId()).isEmpty(), "до удаления задача отсутствует");
        String stringId = String.valueOf(epicTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(uri + "/" + stringId))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals(NOT_FOUND, response.body());
    }

    @Test
    void deleteEpicTaskByIdShouldReturn400() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(uri + "/ErrorRequest"))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400, response.statusCode());
        assertEquals(INVALID_REQUEST, response.body());
    }

    @Test
    void createEpicTaskTest() throws IOException, InterruptedException {
        taskManager.clearEpicTasksMap();
        String jsonTask = gson.toJson(new EpicTask("epicTask", ""));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("EpicTask created", response.body());
        assertEquals(1, taskManager.getEpicTasksList().size());
    }

    @Test
    void updateEpicTaskTest() throws IOException, InterruptedException {
        String jsonTask = gson.toJson(new EpicTask(epicTask.getId(), "epicTaskUpdated", ""));

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals("EpicTask updated", response.body());
        assertEquals("epicTaskUpdated", taskManager.getEpicTask(epicTask.getId()).get().getName(),
                "Информация о задача должна обновится");

    }

    @Test
    void updateEpicTaskShouldReturn404() throws IOException, InterruptedException {
        taskManager.clearEpicTasksMap();
        String jsonTask = gson.toJson(epicTask);

        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals(NOT_FOUND, response.body());
    }
}