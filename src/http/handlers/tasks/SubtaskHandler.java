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
import task.Subtask;
import task.dataTransferObject.SubtaskDto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public SubtaskHandler(TaskManager manager) {
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

    public void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        var subtasks = manager.getAllSubTasks().stream().map(SubtaskDto::new).toList();
        sendOk(exchange, gson.toJson(subtasks));
    }

    public void handleGetSubtaskById(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange).orElseThrow(() ->
                new NotFoundException("Subtask ID not provided"));
        Subtask subtask = manager.getSubTaskByID(id);
        sendOk(exchange, gson.toJson(new SubtaskDto(subtask)));
    }

    public void handlePostSubtask(HttpExchange exchange) throws IOException {
        Subtask subtask = parseSubtask(exchange.getRequestBody()).orElse(null);

        Subtask saved;

        if (subtask.getId() != 0 && manager.getSubTaskByID(subtask.getId()) != null) {
            saved = manager.updateSubTask(subtask);
            sendOk(exchange, gson.toJson(new SubtaskDto(saved)));
        } else {
            saved = manager.createSubTask(subtask);
            sendCreated(exchange, gson.toJson(new SubtaskDto(saved)));
        }
    }

    public void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange).orElseThrow(() ->
                new NotFoundException("Subtask ID not provided"));
        Subtask deletedSubtask = manager.removeSubTaskByID(id);
        sendOk(exchange, gson.toJson(new SubtaskDto(deletedSubtask)));
    }

    private Optional<Subtask> parseSubtask(InputStream bodyInputStream) throws IOException {
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
            int epicId = jsonObject.has("epicId") ? jsonObject.get("epicId").getAsInt() : 0;

            Subtask subtask;
            if (id != 0) {
                subtask = manager.getSubTaskByID(id);
                subtask.setTaskName(taskName);
                subtask.setDescription(description);
                subtask.setStartTime(startTime);
                subtask.setDuration(duration);
                subtask.setTaskStatus(taskStatus);
            } else {
                subtask = new Subtask(taskName, description, startTime, duration, manager.getEpicByID(epicId));
            }
            return Optional.of(subtask);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2 && pathParts[1].equals("subtasks") && requestMethod.equals("GET"))
            return Endpoint.GET_TASKS;
        if (pathParts.length == 3 && pathParts[1].equals("subtasks") && requestMethod.equals("GET"))
            return Endpoint.GET_TASK;
        if (pathParts.length == 2 && pathParts[1].equals("subtasks") && requestMethod.equals("POST"))
            return Endpoint.POST;
        if (pathParts.length == 3 && pathParts[1].equals("subtasks") && requestMethod.equals("DELETE"))
            return Endpoint.DELETE;
        return Endpoint.UNKNOW;
    }

    private void getHandleMethod(Endpoint endpoint, HttpExchange exchange) throws IOException {
        switch (endpoint) {
            case GET_TASKS -> handleGetAllSubtasks(exchange);
            case GET_TASK -> handleGetSubtaskById(exchange);
            case POST -> handlePostSubtask(exchange);
            case DELETE -> handleDeleteSubtask(exchange);
            default -> sendNotFound(exchange, "{\"error\":\"Unknown endpoint\"}");
        }
    }
}