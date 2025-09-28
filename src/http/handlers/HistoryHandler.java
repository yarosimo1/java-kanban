package http.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.endpoints.Endpoint;
import managers.HistoryManager;
import managers.Managers;
import task.dataTransferObject.TaskDto;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    HistoryManager manager =  Managers.getDefaultHistoryManager();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        getHandleMethod(getEndpoint(exchange.getRequestURI().getPath(),
                        exchange.getRequestMethod()),
                exchange);
    }

    @Override
    public Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (pathParts.length == 2 && pathParts[1].equals("history") && requestMethod.equals("GET")) {
            return Endpoint.GET_HISTORY;
        }

        return Endpoint.UNKNOW;
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
       var historyTasks =  manager.getHistory().stream()
               .map(TaskDto::new)
               .toList();

        String response = gson.toJson(historyTasks);

        sendOk(exchange, response);
    }

    private void getHandleMethod(Endpoint endpoint, HttpExchange exchange) throws IOException {
        switch (endpoint) {
            case GET_HISTORY -> handleGetHistory(exchange);
            default -> sendNotFound(exchange, "{\"error\":\"Unknown endpoint\"}");
        }
    }
}