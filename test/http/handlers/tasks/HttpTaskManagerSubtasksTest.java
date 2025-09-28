package http.handlers.tasks;

import com.google.gson.Gson;
import gson.GsonFactory;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import task.Epic;
import task.Subtask;
import http.server.HttpTaskServer;
import task.dataTransferObject.EpicDto;
import task.dataTransferObject.SubtaskDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerSubtasksTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private HttpClient client;
    private Gson gson = GsonFactory.createGson();

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Epic for Sub", "Epic desc"));
        Subtask sub = new Subtask("Sub 1", "Desc sub",
                LocalDateTime.now(), Duration.ofMinutes(30), epic);
        sub.setEpicId(epic.getId());
        String json = gson.toJson(new SubtaskDto(sub));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "При создании сабтаска должен вернуться 201");

        List<Subtask> subtasks = manager.getAllSubTasks();
        assertEquals(1, subtasks.size(), "Сабтаск должен сохраниться");
        assertEquals("Sub 1", subtasks.get(0).getTaskName());
        assertEquals(epic.getId(), subtasks.get(0).getEpicId(), "Сабтаск должен быть привязан к эпику");
    }

    @Test
    void testUpdateSubtask() throws IOException, InterruptedException {
        // --- создаём эпик для подзадачи ---
        Epic epic = new Epic("Epic for subtask", "Epic desc");
        String epicJson = gson.toJson(new EpicDto(epic));

        HttpRequest epicCreateRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> epicCreateResponse = client.send(epicCreateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, epicCreateResponse.statusCode());

        Epic createdEpic = gson.fromJson(epicCreateResponse.body(), Epic.class);
        assertNotEquals(0, createdEpic.getId());

        // --- создаём подзадачу ---
        Subtask subtask = new Subtask("Subtask title", "Subtask desc",
                LocalDateTime.now(), Duration.ofMinutes(30), createdEpic);
        subtask.setEpicId(createdEpic.getId());

        String subtaskJson = gson.toJson(new SubtaskDto(subtask));

        HttpRequest subtaskCreateRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();

        HttpResponse<String> subtaskCreateResponse = client.send(subtaskCreateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, subtaskCreateResponse.statusCode());

        Subtask createdSubtask = gson.fromJson(subtaskCreateResponse.body(), Subtask.class);
        assertNotEquals(0, createdSubtask.getId());

        // --- обновляем подзадачу ---
        createdSubtask.setDescription("Updated subtask description");
        String updatedSubtaskJson = gson.toJson(new SubtaskDto(createdSubtask));

        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updatedSubtaskJson))
                .build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, updateResponse.statusCode());

        Subtask updated = gson.fromJson(updateResponse.body(), Subtask.class);
        assertEquals("Updated subtask description", updated.getDescription());
        assertEquals(createdSubtask.getId(), updated.getId());
        assertEquals(createdEpic.getId(), updated.getEpicId()); // связь с эпиком сохранилась
    }


    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Epic 2", "Epic desc"));
        Subtask saved = manager.createSubTask(
                new Subtask("Sub 2", "Desc", LocalDateTime.now(), Duration.ofMinutes(10), epic));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + saved.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Subtask result = gson.fromJson(response.body(), Subtask.class);
        assertEquals(saved.getId(), result.getId());
        assertEquals(saved.getEpicId(), result.getEpicId());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = manager.createEpic(new Epic("Epic 3", "Epic desc"));
        Subtask saved = manager.createSubTask(
                new Subtask("Sub 3", "Delete me", LocalDateTime.now(), Duration.ofMinutes(20), epic));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + saved.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllSubTasks().isEmpty(), "Сабтаск должен удалиться");
    }

    @Test
    public void testGetNonexistentSubtaskReturns404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Для несуществующего сабтаска должен быть 404");
    }
}
