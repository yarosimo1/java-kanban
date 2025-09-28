package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import gson.GsonFactory;
import http.handlers.HistoryHandler;
import managers.HistoryManager;
import managers.Managers;
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
    private static HistoryManager historyManager;

    @BeforeAll
    static void beforeAll() throws IOException {
        historyManager = Managers.getDefaultHistoryManager();

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
        // добавим в историю задачу
        Task task = new Task("History Task", "Desc",
                LocalDateTime.now(), Duration.ofMinutes(15));
        task.setId(1);
        historyManager.add(task);

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
