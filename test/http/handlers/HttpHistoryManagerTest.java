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

public class HttpHistoryManagerTest {
    private static HttpServer server;
    private static HttpClient client;
    private static Gson gson = GsonFactory.createGson();
    private static TaskManager manager;

    @BeforeAll
    static void beforeAll() throws IOException {
        manager = new InMemoryTaskManager();

        server = HttpServer.create(new InetSocketAddress(8081), 0);
        server.createContext("/history", new HistoryHandler());
        server.start();

        client = HttpClient.newHttpClient();
    }

    @AfterAll
    static void afterAll() {
        server.stop(0);
    }

    @BeforeEach
    void beforeEach() {

    }

    @Test
    void testGetHistoryWhenEmpty() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        TaskDto[] tasks = gson.fromJson(response.body(), TaskDto[].class);
        assertEquals(0, tasks.length);
    }

    @Test
    void testGetHistoryWithTasks() throws IOException, InterruptedException {
        Task task = manager.createTask(new Task("History Task", "Desc",
                LocalDateTime.now(), Duration.ofMinutes(15)));
        manager.getTaskByID(task.getId());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        TaskDto[] tasks = gson.fromJson(response.body(), TaskDto[].class);
        assertEquals(1, tasks.length);
        assertEquals("History Task", tasks[0].taskName);
    }

    @Test
    void testUnknownEndpointReturns404() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8081/unknown"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
