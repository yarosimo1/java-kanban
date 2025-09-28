package http.server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import gson.GsonFactory;
import http.handlers.HistoryHandler;
import http.handlers.PrioritizedHandler;
import http.handlers.tasks.EpicHandler;
import http.handlers.tasks.SubtaskHandler;
import http.handlers.tasks.TaskHandler;
import managers.Managers;
import managers.TaskManager;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private final int port;
    private static TaskManager manager;
    private static HttpServer httpServer;
    private static final Gson gson = GsonFactory.createGson();

    public HttpTaskServer (TaskManager manager) {
        this(manager, 8080);
    }

    public HttpTaskServer(TaskManager manager, int port) {
        this.manager = manager;
        this.port = port;
    }

    public void start() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            httpServer.createContext("/tasks", new TaskHandler(manager));
            httpServer.createContext("/epics", new EpicHandler(manager));
            httpServer.createContext("/subtasks", new SubtaskHandler(manager));
            httpServer.createContext("/history", new HistoryHandler());
            httpServer.createContext("/prioritized", new PrioritizedHandler(manager));
            httpServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        httpServer.stop(1);
    }

    public static Gson getGson() {
        return gson;
    }

    public int getPort() {
        return port;
    }

    public static void main(String[] args) {
        File file = new File("task.csv");
        TaskManager manager1 = Managers.uploadTaskManager(file);
        HttpTaskServer server = new HttpTaskServer(manager1);
        server.start();
    }
}
