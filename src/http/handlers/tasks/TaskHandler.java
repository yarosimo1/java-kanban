package http.handlers.tasks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import enums.TaskStatus;
import exceptions.NotFoundException;
import http.endpoints.Endpoint;
import http.handlers.BaseHttpHandler;
import managers.TaskManager;
import task.Task;
import task.dataTransferObject.TaskDto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            getHandleMethod(getEndpoint(exchange.getRequestURI().getPath(),
                    exchange.getRequestMethod()), exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, String.format("{\"error\":\"%s\"}", e.getMessage()));
        } catch (IllegalArgumentException e) {
            sendHasOverlaps(exchange, String.format("{\"error\":\"%s\"}", e.getMessage()));
        } catch (Exception e) {
            sendInternalServerError(exchange, "{\"error\":\"Internal Server Error\"}");
        }
    }

    public void handleGetAllTasks(HttpExchange exchange) throws IOException {
        var tasks = manager.getAllTasks().stream().map(TaskDto::new).toList();
        sendOk(exchange, gson.toJson(tasks));
    }

    public void handleGetTasksById(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange).orElseThrow(() ->
                new NotFoundException("Task ID not provided"));
        Task task = manager.getTaskByID(id);
        sendOk(exchange, gson.toJson(new TaskDto(task)));
    }

    public void handlePostTask(HttpExchange exchange) throws IOException {
        Task task = parseTask(exchange.getRequestBody()).orElse(null);

        Task saved;

        if (task.getId() != 0 && manager.getTaskByID(task.getId()) != null) {
            saved = manager.updateTask(task);
            sendOk(exchange, gson.toJson(new TaskDto(saved)));
        } else {
            saved = manager.createTask(task);
            sendCreated(exchange, gson.toJson(new TaskDto(saved)));
        }
    }

    public void handleDeleteTask(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange).orElseThrow(() ->
                new NotFoundException("Task ID not provided"));
        Task deletedTask = manager.removeTaskByID(id);
        sendOk(exchange, gson.toJson(new TaskDto(deletedTask)));
    }

    private Optional<Task> parseTask(InputStream bodyInputStream) throws IOException {
        String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) return Optional.empty();

        try {
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            int id = jsonObject.has("id") ? jsonObject.get("id").getAsInt() : 0;
            String taskName = jsonObject.has("taskName") ? jsonObject.get("taskName").getAsString() : null;
            String description = jsonObject.has("description") ? jsonObject.get("description").getAsString() : null;
            LocalDateTime startTime = jsonObject.has("startTime") ? LocalDateTime.parse(jsonObject.get("startTime").getAsString()) : null;
            Duration duration = jsonObject.has("duration") ? Duration.parse(jsonObject.get("duration").getAsString()) : null;
            TaskStatus taskStatus = jsonObject.has("taskStatus") ? TaskStatus.valueOf(jsonObject.get("taskStatus").getAsString()) : TaskStatus.NEW;

            Task task;

            if (id != 0) {
                task = manager.getTaskByID(id);
                task.setTaskName(taskName);
                task.setDescription(description);
                task.setStartTime(startTime);
                task.setDuration(duration);
                task.setTaskStatus(taskStatus);
            } else {
                task = new Task(taskName, description, startTime, duration);
            }
            return Optional.of(task);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2 && pathParts[1].equals("tasks") && requestMethod.equals("GET"))
            return Endpoint.GET_TASKS;
        if (pathParts.length == 3 && pathParts[1].equals("tasks") && requestMethod.equals("GET"))
            return Endpoint.GET_TASK;
        if (pathParts.length == 2 && pathParts[1].equals("tasks") && requestMethod.equals("POST"))
            return Endpoint.POST;
        if (pathParts.length == 3 && pathParts[1].equals("tasks") && requestMethod.equals("DELETE"))
            return Endpoint.DELETE;
        return Endpoint.UNKNOW;
    }

    private void getHandleMethod(Endpoint endpoint, HttpExchange exchange) throws IOException {
        switch (endpoint) {
            case GET_TASKS -> handleGetAllTasks(exchange);
            case GET_TASK -> handleGetTasksById(exchange);
            case POST -> handlePostTask(exchange);
            case DELETE -> handleDeleteTask(exchange);
            default -> sendNotFound(exchange, "{\"error\":\"Unknown endpoint\"}");
        }
    }
}
