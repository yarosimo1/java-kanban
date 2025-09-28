package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.endpoints.Endpoint;
import managers.TaskManager;
import task.dataTransferObject.TaskDto;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        getHandleMethod(getEndpoint(exchange.getRequestURI().getPath(),
                        exchange.getRequestMethod()),
                exchange);
    }

    @Override
    public Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("prioritized") && requestMethod.equals("GET")) {
            return Endpoint.GET_PRIORITIZED;
        }

        return Endpoint.UNKNOW;
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        var historyTasks =  manager.getPrioritizedTasks().stream()
                .map(TaskDto::new)
                .toList();

        String response = gson.toJson(historyTasks);

        sendOk(exchange, response);
    }

    private void getHandleMethod(Endpoint endpoint, HttpExchange exchange) throws IOException {
        switch (endpoint) {
            case GET_PRIORITIZED -> handleGetPrioritizedTasks(exchange);
            default -> sendNotFound(exchange, "{\"error\":\"Unknown endpoint\"}");
        }
    }
}
