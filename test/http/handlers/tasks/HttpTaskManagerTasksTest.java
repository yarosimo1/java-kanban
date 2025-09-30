package http.handlers.tasks;

import com.google.gson.Gson;
import gson.GsonFactory;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import task.Task;
import http.server.HttpTaskServer;
import task.dataTransferObject.TaskDto;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {
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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description",
                LocalDateTime.now(), Duration.ofMinutes(15));
        String json = gson.toJson(new TaskDto(task));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Должен вернуться статус 201 Created");
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size(), "Задача должна сохраниться в менеджере");
        assertEquals("Test Task", tasks.get(0).getTaskName(), "Имя должно совпадать");
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description",
                LocalDateTime.now(), Duration.ofMinutes(15));
        String json = gson.toJson(new TaskDto(task));

        // --- создаём задачу ---
        HttpRequest createRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> createResponse = client.send(createRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, createResponse.statusCode());

        Task created = gson.fromJson(createResponse.body(), Task.class);
        assertNotEquals(0, created.getId());

        // --- обновляем задачу ---
        created.setDescription("Updated description");
        String updatedJson = gson.toJson(new TaskDto(created));

        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updatedJson))
                .build();

        HttpResponse<String> updateResponse = client.send(updateRequest, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, updateResponse.statusCode());
        Task updated = gson.fromJson(updateResponse.body(), Task.class);

        assertEquals("Updated description", updated.getDescription());
        assertEquals(created.getId(), updated.getId()); // ID не должен измениться
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task saved = manager.createTask(
                new Task("Task 2", "desc",
                        LocalDateTime.now(), Duration.ofMinutes(5))
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + saved.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        Task result = gson.fromJson(response.body(), Task.class);
        assertEquals(saved.getId(), result.getId());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task saved = manager.createTask(
                new Task("Task 3", "desc", LocalDateTime.now(), Duration.ofMinutes(5))
        );

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + saved.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(manager.getAllTasks().isEmpty(), "Задача должна удалиться");
    }

    @Test
    public void testGetNonexistentTaskReturns404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Для несуществующей задачи должен вернуться 404");
    }
}
