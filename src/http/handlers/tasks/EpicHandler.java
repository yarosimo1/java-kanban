package http.handlers.tasks;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.NotFoundException;
import http.endpoints.Endpoint;
import http.handlers.BaseHttpHandler;
import managers.TaskManager;
import task.Epic;
import task.dataTransferObject.EpicDto;
import task.dataTransferObject.SubtaskDto;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            getHandleMethod(getEndpoint(exchange.getRequestURI().getPath(),
                    exchange.getRequestMethod()), exchange);
        } catch (NotFoundException e) {
            sendNotFound(exchange, String.format("{\"error\":\"%s\"}", e.getMessage()));
        } catch (Exception e) {
            sendInternalServerError(exchange, "{\"error\":\"Internal Server Error\"}");
        }
    }

    public void handleGetAllEpics(HttpExchange exchange) throws IOException {
        var epics = manager.getAllEpicTasks().stream().map(EpicDto::new).toList();
        sendOk(exchange, gson.toJson(epics));
    }

    public void handleGetEpicById(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange).orElseThrow(() ->
                new NotFoundException("Epic ID not provided"));
        Epic epic = manager.getEpicByID(id);
        sendOk(exchange, gson.toJson(new EpicDto(epic)));
    }

    public void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange).orElseThrow(() ->
                new NotFoundException("Epic ID not provided"));
        var epicSubtasks = manager.getEpicByID(id).getSubTasks().stream().map(SubtaskDto::new).toList();
        sendOk(exchange, gson.toJson(epicSubtasks));
    }

    public void handlePostEpic(HttpExchange exchange) throws IOException {
        Epic epic = parseEpic(exchange.getRequestBody()).orElse(null);

        Epic saved;

        if (epic.getId() != 0 && manager.getEpicByID(epic.getId()) != null) {
            saved = manager.updateEpic(epic);
            sendOk(exchange, gson.toJson(new EpicDto(saved)));
        } else {
            saved = manager.createEpic(epic);
            sendCreated(exchange, gson.toJson(new EpicDto(saved)));
        }
    }


    public void handleDeleteEpic(HttpExchange exchange) throws IOException {
        int id = getTaskId(exchange).orElseThrow(() ->
                new NotFoundException("Epic ID not provided"));
        Epic deletedEpic = manager.removeEpicByID(id);
        sendOk(exchange, gson.toJson(new EpicDto(deletedEpic)));
    }

    private Optional<Epic> parseEpic(InputStream bodyInputStream) throws IOException {
        String body = new String(bodyInputStream.readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) return Optional.empty();

        try {
            JsonObject jsonObject = JsonParser.parseString(body).getAsJsonObject();
            String taskName = jsonObject.has("taskName") ? jsonObject.get("taskName").getAsString() : null;
            String description = jsonObject.has("description") ? jsonObject.get("description").getAsString() : null;
            int id = jsonObject.has("id") ? jsonObject.get("id").getAsInt() : 0;

            Epic epic;
            if (id != 0) {
                epic = manager.getEpicByID(id);
                epic.setTaskName(taskName);
                epic.setDescription(description);
            } else {
                epic = new Epic(taskName, description);
            }
            return Optional.of(epic);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");
        if (pathParts.length == 2 && pathParts[1].equals("epics") && requestMethod.equals("GET"))
            return Endpoint.GET_TASKS;
        if (pathParts.length == 3 && pathParts[1].equals("epics") && requestMethod.equals("GET"))
            return Endpoint.GET_TASK;
        if (pathParts.length == 2 && pathParts[1].equals("epics") && requestMethod.equals("POST"))
            return Endpoint.POST;
        if (pathParts.length == 3 && pathParts[1].equals("epics") && requestMethod.equals("DELETE"))
            return Endpoint.DELETE;
        if (pathParts.length == 4 && pathParts[1].equals("epics") && pathParts[3].equals("subtasks") && requestMethod.equals("GET"))
            return Endpoint.GET_EPIC_SUB;
        return Endpoint.UNKNOW;
    }

    private void getHandleMethod(Endpoint endpoint, HttpExchange exchange) throws IOException {
        switch (endpoint) {
            case GET_TASKS -> handleGetAllEpics(exchange);
            case GET_TASK -> handleGetEpicById(exchange);
            case POST -> handlePostEpic(exchange);
            case DELETE -> handleDeleteEpic(exchange);
            case GET_EPIC_SUB -> handleGetEpicSubtasks(exchange);
            default -> sendNotFound(exchange, "{\"error\":\"Unknown endpoint\"}");
        }
    }
}