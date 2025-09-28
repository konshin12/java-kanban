package manager;

import task.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("id,type,name,status,description,epic,duration,startTime");

            for (Task task : getAllTasks()) {
                writer.println(taskToString(task));
            }

            for (Epic epic : getAllEpics()) {
                writer.println(taskToString(epic));
            }

            for (Subtask subtask : getAllSubtasks()) {
                writer.println(taskToString(subtask));
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    private String taskToString(Task task) {
        String durationStr = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";
        String startTimeStr = task.getStartTime() != null ? task.getStartTime().toString() : "";

        if (task.getClass() == Epic.class) {
            Epic epic = (Epic) task;
            return String.format("%d,EPIC,%s,%s,%s,,%s,%s",
                    epic.getTaskId(),
                    escape(epic.getTaskName()),
                    epic.getTaskStatus(),
                    escape(epic.getTaskDescription()),
                    durationStr,
                    startTimeStr);
        } else if (task.getClass() == Subtask.class) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d,%s,%s",
                    subtask.getTaskId(),
                    escape(subtask.getTaskName()),
                    subtask.getTaskStatus(),
                    escape(subtask.getTaskDescription()),
                    subtask.getEpicId(),
                    durationStr,
                    startTimeStr);
        } else {
            return String.format("%d,TASK,%s,%s,%s,,%s,%s",
                    task.getTaskId(),
                    escape(task.getTaskName()),
                    task.getTaskStatus(),
                    escape(task.getTaskDescription()),
                    durationStr,
                    startTimeStr);
        }
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace(",", "\\,");
    }

    private String unescape(String value) {
        if (value == null) return "";
        return value.replace("\\,", ",");
    }

    private Task taskFromString(String value) {
        String[] fields = value.split("(?<!\\\\),");
        for (int i = 0; i < fields.length; i++) {
            fields[i] = unescape(fields[i]);
        }

        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        Duration duration = null;
        LocalDateTime startTime = null;

        if (fields.length > 6 && !fields[6].isEmpty()) {
            duration = Duration.ofMinutes(Long.parseLong(fields[6]));
        }
        if (fields.length > 7 && !fields[7].isEmpty()) {
            startTime = LocalDateTime.parse(fields[7]);
        }

        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status, duration, startTime);
                task.setTaskId(id);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description, duration, startTime);
                epic.setTaskId(id);
                epic.setTaskStatus(status);
                return epic;
            case "SUBTASK":
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(name, description, status,
                        new Epic("temp", "temp"), duration, startTime);
                subtask.setTaskId(id);
                subtask.setEpicId(epicId);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            if (!file.exists()) return manager;

            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            if (lines.length <= 1) return manager;

            List<Task> tasks = new ArrayList<>();
            List<Epic> epics = new ArrayList<>();
            List<Subtask> subtasks = new ArrayList<>();

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                Task task = manager.taskFromString(line);

                if (task.getClass() == Epic.class) {
                    epics.add((Epic) task);
                } else if (task.getClass() == Subtask.class) {
                    subtasks.add((Subtask) task);
                } else {
                    tasks.add(task);
                }

                if (task.getTaskId() >= manager.getId()) {
                    manager.setId(task.getTaskId() + 1);
                }
            }

            for (Epic epic : epics) {
                manager.getEpics().put(epic.getTaskId(), epic);
            }

            for (Task task : tasks) {
                manager.getTasks().put(task.getTaskId(), task);
            }

            for (Subtask subtask : subtasks) {
                Epic epic = manager.getEpics().get(subtask.getEpicId());
                if (epic != null) {
                    Subtask properSubtask = new Subtask(
                            subtask.getTaskName(),
                            subtask.getTaskDescription(),
                            subtask.getTaskStatus(),
                            epic
                    );
                    properSubtask.setTaskId(subtask.getTaskId());
                    manager.getSubtasks().put(properSubtask.getTaskId(), properSubtask);
                    epic.addSubtaskId(properSubtask.getTaskId());
                }
            }

            for (Epic epic : epics) {
                manager.updateEpicStatus(epic.getTaskId());
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки", e);
        }

        return manager;
    }

    @Override
    public Task createTask(Task task) {
        Task result = super.createTask(task);
        save();
        return result;
    }

    @Override
    public Task updateTask(Task task) {
        Task result = super.updateTask(task);
        save();
        return result;
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask result = super.createSubtask(subtask);
        save();
        return result;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask result = super.updateSubtask(subtask);
        save();
        return result;
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic result = super.createEpic(epic);
        save();
        return result;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic result = super.updateEpic(epic);
        save();
        return result;
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }
}