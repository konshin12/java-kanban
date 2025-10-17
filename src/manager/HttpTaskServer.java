package manager;

import base.TaskManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.sun.net.httpserver.HttpServer;
import handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Gson gson = createGson();
    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        configureRoutes();
    }

    private static Gson createGson() {
        JsonSerializer<LocalDateTime> localDateTimeSerializer =
                (src, typeOfSrc, context) -> context.serialize(src.toString());

        JsonDeserializer<LocalDateTime> localDateTimeDeserializer =
                (json, typeOfT, context) -> LocalDateTime.parse(json.getAsString());

        JsonSerializer<Duration> durationSerializer =
                (src, typeOfSrc, context) -> context.serialize(src.toMinutes());

        JsonDeserializer<Duration> durationDeserializer =
                (json, typeOfT, context) -> Duration.ofMinutes(json.getAsLong());

        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, localDateTimeSerializer)
                .registerTypeAdapter(LocalDateTime.class, localDateTimeDeserializer)
                .registerTypeAdapter(Duration.class, durationSerializer)
                .registerTypeAdapter(Duration.class, durationDeserializer)
                .create();
    }

    public static Gson getGson() {
        return gson;
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }

    private void configureRoutes() {
        server.createContext("/tasks", new TasksHandler(taskManager, gson));
        server.createContext("/subtasks", new SubtasksHandler(taskManager, gson));
        server.createContext("/epics", new EpicsHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public void start() {
        server.start();
        System.out.println("HttpTaskServer запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("HttpTaskServer остановлен");
    }
}