package http.handlers.tasks;

import com.google.gson.Gson;
import gson.GsonFactory;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import task.Epic;
import http.server.HttpTaskServer;
import task.Subtask;
import task.dataTransferObject.EpicDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerEpicsTest {
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
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Big story");
        String json = gson.toJson(new EpicDto(epic));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "При создании эпика должен вернуться 201");

        List<Epic> epics = manager.getAllEpicTasks();
        assertEquals(1, epics.size(), "Эпик должен сохраниться");
        assertEquals("Epic 1", epics.get(0).getTaskName());
    }

    @Test
    void testUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Big story");
        manager.createEpic(epic);

        // --- обновляем эпик ---
        Epic created = manager.getEpicByID(1);
        created.setDescription("Updated epic description");

        String updatedJson = gson.toJson(new EpicDto(created));

        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, updateResponse.statusCode());

        Epic updated = gson.fromJson(updateResponse.body(), Epic.class);
        assertEquals("Updated epic description", updated.getDescription());
        assertEquals(created.getId(), updated.getId());
    }


    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic saved = manager.createEpic(new Epic("Epic 2", "Desc"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + saved.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Epic result = gson.fromJson(response.body(), Epic.class);
        assertEquals(saved.getId(), result.getId());
        assertEquals("Epic 2", result.getTaskName());
    }

    @Test
    public void testGetEpicSubtask() throws IOException, InterruptedException {
        Epic saved = manager.createEpic(new Epic("Epic 2", "Desc"));
        Subtask subtask = new Subtask("sub", "desc", LocalDateTime.now(), Duration.ZERO, saved);
        Subtask created = manager.createSubTask(subtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + saved.getId() + "/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Subtask[] subtasks = gson.fromJson(response.body(), Subtask[].class);
        assertEquals(1, subtasks.length);
        assertTrue(Arrays.stream(subtasks).anyMatch(st -> st.getTaskName().equals(created.getTaskName())));
        assertTrue(Arrays.stream(subtasks).anyMatch(st -> st.getId() == created.getId()));
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic saved = manager.createEpic(new Epic("Epic 3", "Delete me"));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + saved.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllEpicTasks().isEmpty(), "Эпик должен удалиться");
    }

    @Test
    public void testGetNonexistentEpicReturns404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Для несуществующего эпика должен быть 404");
    }
}
