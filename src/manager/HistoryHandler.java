package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();

            if ("GET".equals(method)) {
                handleGetHistory(h);
            } else {
                sendText(h, gson.toJson(new ErrorResponse("Метод не поддерживается")), 405);
            }
        } catch (Exception e) {
            sendInternalServerError(h, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    private void handleGetHistory(HttpExchange h) throws IOException {
        List<Task> history = taskManager.getHistory();
        sendText(h, gson.toJson(history), 200);
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

