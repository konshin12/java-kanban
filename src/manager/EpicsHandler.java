package manager;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import task.Epic;
import task.Subtask;

import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange h) throws IOException {
        try {
            String method = h.getRequestMethod();
            String path = h.getRequestURI().getPath();
            String query = h.getRequestURI().getQuery();

            if (path.contains("/subtasks")) {
                handleGetEpicSubtasks(h, query);
            } else {
                switch (method) {
                    case "GET":
                        if (query == null || query.isEmpty()) {
                            handleGetAllEpics(h);
                        } else {
                            handleGetEpicById(h, query);
                        }
                        break;
                    case "POST":
                        handleCreateEpic(h);
                        break;
                    case "DELETE":
                        if (query == null || query.isEmpty()) {
                            handleDeleteAllEpics(h);
                        } else {
                            handleDeleteEpicById(h, query);
                        }
                        break;
                    default:
                        sendText(h, gson.toJson(new ErrorResponse("Метод не поддерживается")), 405);
                }
            }
        } catch (ManagerSaveException e) {
            sendInternalServerError(h, e.getMessage());
        } catch (Exception e) {
            sendInternalServerError(h, "Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    private void handleGetAllEpics(HttpExchange h) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        sendText(h, gson.toJson(epics), 200);
    }

    private void handleGetEpicById(HttpExchange h, String query) throws IOException {
        try {
            int id = Integer.parseInt(query.split("=")[1]);
            Epic epic = taskManager.getEpicById(id);
            if (epic == null) {
                sendNotFound(h, "Эпик с ID " + id + " не найден");
            } else {
                sendText(h, gson.toJson(epic), 200);
            }
        } catch (Exception e) {
            sendText(h, gson.toJson(new ErrorResponse("Некорректный ID")), 400);
        }
    }

    private void handleCreateEpic(HttpExchange h) throws IOException {
        String body = readRequestBody(h);
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            Epic created = taskManager.createEpic(epic);
            sendText(h, gson.toJson(created), 201);
        } catch (Exception e) {
            sendText(h, gson.toJson(new ErrorResponse("Ошибка при создании эпика")), 400);
        }
    }

    private void handleDeleteEpicById(HttpExchange h, String query) throws IOException {
        try {
            int id = Integer.parseInt(query.split("=")[1]);
            taskManager.deleteEpicById(id);
            sendText(h, gson.toJson(new SuccessResponse("Эпик удален")), 200);
        } catch (Exception e) {
            sendText(h, gson.toJson(new ErrorResponse("Некорректный ID")), 400);
        }
    }

    private void handleDeleteAllEpics(HttpExchange h) throws IOException {
        taskManager.deleteAllEpics();
        sendText(h, gson.toJson(new SuccessResponse("Все эпики удалены")), 200);
    }

    private void handleGetEpicSubtasks(HttpExchange h, String query) throws IOException {
        try {
            int epicId = Integer.parseInt(query.split("=")[1]);
            Epic epic = taskManager.getEpicById(epicId);
            if (epic == null) {
                sendNotFound(h, "Эпик с ID " + epicId + " не найден");
            } else {
                List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epicId);
                sendText(h, gson.toJson(subtasks), 200);
            }
        } catch (Exception e) {
            sendText(h, gson.toJson(new ErrorResponse("Некорректный ID")), 400);
        }
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