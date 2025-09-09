package manager;

import task.*;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("id,type,name,status,description,epic");

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
        if (task instanceof Epic) {
            return String.format("%d,EPIC,%s,%s,%s,",
                    task.getTaskId(),
                    task.getTaskName(),
                    task.getTaskStatus(),
                    task.getTaskDescription());
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,SUBTASK,%s,%s,%s,%d",
                    subtask.getTaskId(),
                    subtask.getTaskName(),
                    subtask.getTaskStatus(),
                    subtask.getTaskDescription(),
                    subtask.getEpicId());
        } else {
            return String.format("%d,TASK,%s,%s,%s,",
                    task.getTaskId(),
                    task.getTaskName(),
                    task.getTaskStatus(),
                    task.getTaskDescription());
        }
    }

    private Task taskFromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TypeTask type = TypeTask.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                Task task = new Task(name, description, status);
                task.setTaskId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(name, description);
                epic.setTaskId(id);
                epic.setTaskStatus(status);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                // Временный объект - эпик будет установлен позже
                Subtask subtask = new Subtask(name, description, status, null);
                subtask.setTaskId(id);
                subtask.setTaskId(epicId); // Сохраняем ID эпика
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            if (!file.exists()) {
                return manager; // Файл не существует - возвращаем пустой менеджер
            }

            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            if (lines.length <= 1) {
                return manager; // Только заголовок или пустой файл
            }

            List<Task> tasks = new ArrayList<>();
            List<Epic> epics = new ArrayList<>();
            List<Subtask> subtasks = new ArrayList<>();

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;

                Task task = manager.taskFromString(line);
                if (task instanceof Epic) {
                    epics.add((Epic) task);
                } else if (task instanceof Subtask) {
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
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
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