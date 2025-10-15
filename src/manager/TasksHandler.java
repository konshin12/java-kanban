package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.Task;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    public TasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath();
            String query = h.getRequestURI().getQuery();

            switch (method) {
                case "GET":
                    if (query == null || query.isEmpty()) {
                        handleGetAllTasks(h);
                    } else {
                        handleGetTaskById(h, query);
                    }
                    break;
                case "POST":
                    handleCreateTask(h);
                    break;
                case "DELETE":
                    if (query == null || query.isEmpty()) {
                        handleDeleteAllTasks(h);
                    } else {
                        handleDeleteTaskById(h, query);
                    }
                    break;
                default:
                    sendText(h, gson.toJson(new ErrorResponse("Метод не поддерживается")), 405);
            }
        } catch (ManagerSaveException e) {
            sendInternalServerError(h, e.getMessage());
        } catch (Exception e) {
            sendInternalServerError(h, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    private void handleGetAllTasks(HttpExchange h) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        sendText(h, gson.toJson(tasks), 200);
    }

    private void handleGetTaskById(HttpExchange h, String query) throws IOException {
        try {
            int id = Integer.parseInt(query.split("=")[1]);
            Task task = taskManager.getTaskById(id);
            if (task == null) {
                sendNotFound(h, "Задача с ID " + id + " не найдена");
            } else {
                sendText(h, gson.toJson(task), 200);
            }
        } catch (Exception e) {
            sendText(h, gson.toJson(new ErrorResponse("Некорректный ID")), 400);
        }
    }

    private void handleCreateTask(HttpExchange h) throws IOException {
        String body = readRequestBody(h);
        try {
            Task task = gson.fromJson(body, Task.class);
            Task created = taskManager.createTask(task);
            sendText(h, gson.toJson(created), 201);
        } catch (ManagerSaveException e) {
            if (e.getMessage().contains("пересекается")) {
                sendHasOverlaps(h, e.getMessage());
            } else {
                sendInternalServerError(h, e.getMessage());
            }
        } catch (Exception e) {
            sendText(h, gson.toJson(new ErrorResponse("Ошибка при создании задачи")), 400);
        }
    }

    private void handleDeleteTaskById(HttpExchange h, String query) throws IOException {
        try {
            int id = Integer.parseInt(query.split("=")[1]);
            taskManager.deleteTaskById(id);
            sendText(h, gson.toJson(new SuccessResponse("Задача удалена")), 200);
        } catch (Exception e) {
            sendText(h, gson.toJson(new ErrorResponse("Некорректный ID")), 400);
        }
    }

    private void handleDeleteAllTasks(HttpExchange h) throws IOException {
        taskManager.deleteAllTasks();
        sendText(h, gson.toJson(new SuccessResponse("Все задачи удалены")), 200);
    }

    private static class SuccessResponse {
        private String message;

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }
}