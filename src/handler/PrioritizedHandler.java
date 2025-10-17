package handler;

import base.TaskManager;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();

            if ("GET".equals(method)) {
                handleGetPrioritized(h);
            } else {
                sendText(h, gson.toJson(new ErrorResponse("Метод не поддерживается")), 405);
            }
        } catch (Exception e) {
            sendInternalServerError(h, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    private void handleGetPrioritized(HttpExchange h) throws IOException {
        List<Task> prioritized = taskManager.getPrioritizedTasks();
        sendText(h, gson.toJson(prioritized), 200);
    }

    private static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }

        public String getError() {
            return error;
        }
    }
}