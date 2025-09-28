package http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import gson.GsonFactory;
import http.endpoints.Endpoint;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public abstract class BaseHttpHandler {
    protected final Gson gson = GsonFactory.createGson();

    protected void sendOk(HttpExchange exchange, String text) throws IOException {
        writeResponse(exchange, text, 200);
    }

    protected void sendCreated(HttpExchange exchange, String text) throws IOException {
        writeResponse(exchange, text, 201);
    }

    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        writeResponse(exchange, text, 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange, String text) throws IOException {
        writeResponse(exchange, text, 406);
    }

    protected void sendInternalServerError(HttpExchange exchange, String text) throws IOException {
        writeResponse(exchange, text, 500);
    }

    public abstract Endpoint getEndpoint(String requestPath, String requestMethod);

    protected static Optional<Integer> getTaskId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        String[] parts = path.split("/");

        for (String part : parts) {
            if (part.matches("\\d+")) {
                return Optional.of(Integer.parseInt(part));
            }
        }
        return Optional.empty();
    }

    private void writeResponse(HttpExchange exchange, String text, int responseCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(responseCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }
}
