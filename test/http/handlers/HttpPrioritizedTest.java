package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import gson.GsonFactory;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.*;
import task.Task;
import task.dataTransferObject.TaskDto;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PrioritizedHandlerTest {
    private static HttpServer server;
    private static HttpClient client;
    private static Gson gson = GsonFactory.createGson();
    private static TaskManager manager;

    @BeforeAll
    static void beforeAll() throws IOException {
        manager = new InMemoryTaskManager();
        server = HttpServer.create(new InetSocketAddress(8082), 0);
        server.createContext("/prioritized", new PrioritizedHandler(manager));
        server.start();

        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void afterAll() {
        server.stop(0);
    }

    @BeforeEach
    void beforeEach() {
        manager.clearTasks();
        manager.clearEpicTasks();
        manager.clearSubTasks();
    }

    @Test
    void testGetPrioritizedWhenEmpty() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        TaskDto[] tasks = gson.fromJson(response.body(), TaskDto[].class);
        assertEquals(0, tasks.length);
    }

    @Test
    void testGetPrioritizedWithTasks() throws IOException, InterruptedException {
        // добавляем 2 задачи с разными startTime
        Task task1 = new Task("Task 1", "Desc 1", LocalDateTime.now().plusHours(1), Duration.ofMinutes(30));
        Task task2 = new Task("Task 2", "Desc 2", LocalDateTime.now(), Duration.ofMinutes(20));

        manager.createTask(task1);
        manager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        TaskDto[] tasks = gson.fromJson(response.body(), TaskDto[].class);

        assertEquals(2, tasks.length);

        assertEquals("Task 2", tasks[0].taskName); // у task2 startTime раньше
        assertEquals("Task 1", tasks[1].taskName);
    }

    @Test
    void testUnknownEndpointReturns404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8082/unknown"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
