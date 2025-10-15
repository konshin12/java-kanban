package manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    new com.google.gson.JsonSerializer<LocalDateTime>() {
                        @Override
                        public com.google.gson.JsonElement serialize(LocalDateTime src,
                                                                     java.lang.reflect.Type typeOfSrc,
                                                                     com.google.gson.JsonSerializationContext context) {
                            return new com.google.gson.JsonPrimitive(src.toString());
                        }
                    })
            .registerTypeAdapter(LocalDateTime.class,
                    new com.google.gson.JsonDeserializer<LocalDateTime>() {
                        @Override
                        public LocalDateTime deserialize(com.google.gson.JsonElement json,
                                                         java.lang.reflect.Type typeOfT,
                                                         com.google.gson.JsonDeserializationContext context) {
                            return LocalDateTime.parse(json.getAsString());
                        }
                    })
            .registerTypeAdapter(Duration.class,
                    new com.google.gson.JsonSerializer<Duration>() {
                        @Override
                        public com.google.gson.JsonElement serialize(Duration src,
                                                                     java.lang.reflect.Type typeOfSrc,
                                                                     com.google.gson.JsonSerializationContext context) {
                            return new com.google.gson.JsonPrimitive(src.toMinutes());
                        }
                    })
            .registerTypeAdapter(Duration.class,
                    new com.google.gson.JsonDeserializer<Duration>() {
                        @Override
                        public Duration deserialize(com.google.gson.JsonElement json,
                                                    java.lang.reflect.Type typeOfT,
                                                    com.google.gson.JsonDeserializationContext context) {
                            return Duration.ofMinutes(json.getAsLong());
                        }
                    })
            .create();
    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        configureRoutes();
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