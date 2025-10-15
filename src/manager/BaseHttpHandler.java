package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected final TaskManager taskManager;
    protected final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void sendText(HttpExchange h, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(statusCode, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h, String message) throws IOException {
        sendText(h, gson.toJson(new ErrorResponse(message)), 404);
    }

    protected void sendHasOverlaps(HttpExchange h, String message) throws IOException {
        sendText(h, gson.toJson(new ErrorResponse(message)), 406);
    }

    protected void sendInternalServerError(HttpExchange h, String message) throws IOException {
        sendText(h, gson.toJson(new ErrorResponse(message)), 500);
    }

    protected String readRequestBody(HttpExchange h) throws IOException {
        InputStream is = h.getRequestBody();
        byte[] bytes = is.readAllBytes();
        return new String(bytes, StandardCharsets.UTF_8);
    }

    protected static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}