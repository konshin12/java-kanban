package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.Subtask;

import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            String query = h.getRequestURI().getQuery();

            switch (method) {
                case "GET":
                    if (query == null || query.isEmpty()) {
                        handleGetAllSubtasks(h);
                    } else {
                        handleGetSubtaskById(h, query);
                    }
                    break;
                case "POST":
                    handleCreateSubtask(h);
                    break;
                case "DELETE":
                    if (query == null || query.isEmpty()) {
                        handleDeleteAllSubtasks(h);
                    } else {
                        handleDeleteSubtaskById(h, query);
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

    private void handleGetAllSubtasks(HttpExchange h) throws IOException {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        sendText(h, gson.toJson(subtasks), 200);
    }

    private void handleGetSubtaskById(HttpExchange h, String query) throws IOException {
        try {
            int id = Integer.parseInt(query.split("=")[1]);
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask == null) {
                sendNotFound(h, "Подзадача с ID " + id + " не найдена");
            } else {
                sendText(h, gson.toJson(subtask), 200);
            }
        } catch (Exception e) {
            sendText(h, gson.toJson(new ErrorResponse("Некорректный ID")), 400);
        }
    }

    private void handleCreateSubtask(HttpExchange h) throws IOException {
        String body = readRequestBody(h);
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (taskManager.getEpicById(subtask.getEpicId()) == null) {
                sendNotFound(h, "Эпик с ID " + subtask.getEpicId() + " не найден");
            } else {
                Subtask created = taskManager.createSubtask(subtask);
                sendText(h, gson.toJson(created), 201);
            }
        } catch (ManagerSaveException e) {
            if (e.getMessage().contains("пересекается")) {
                sendHasOverlaps(h, e.getMessage());
            } else {
                sendInternalServerError(h, e.getMessage());
            }
        } catch (IllegalArgumentException e) {
            sendNotFound(h, e.getMessage());
        } catch (Exception e) {
            sendText(h, gson.toJson(new ErrorResponse("Ошибка при создании подзадачи")), 400);
        }
    }

    private void handleDeleteSubtaskById(HttpExchange h, String query) throws IOException {
        try {
            int id = Integer.parseInt(query.split("=")[1]);
            taskManager.deleteSubtaskById(id);
            sendText(h, gson.toJson(new SuccessResponse("Подзадача удалена")), 200);
        } catch (Exception e) {
            sendText(h, gson.toJson(new ErrorResponse("Некорректный ID")), 400);
        }
    }

    private void handleDeleteAllSubtasks(HttpExchange h) throws IOException {
        taskManager.deleteAllSubtasks();
        sendText(h, gson.toJson(new SuccessResponse("Все подзадачи удалены")), 200);
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