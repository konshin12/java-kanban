import manager.FileBackedTaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = Files.createTempFile("tasks", ".csv").toFile();
        System.out.println("Файл для сохранения: " + file.getAbsolutePath());

        System.out.println("=== СОЗДАЕМ ПЕРВЫЙ МЕНЕДЖЕР ===");
        FileBackedTaskManager manager1 = new FileBackedTaskManager(file);

        Task task1 = manager1.createTask(new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW));
        Task task2 = manager1.createTask(new Task("Задача 2", "Описание задачи 2", TaskStatus.IN_PROGRESS));

        Epic epic1 = manager1.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Subtask subtask1 = manager1.createSubtask(
                new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1));
        Subtask subtask2 = manager1.createSubtask(
                new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.DONE, epic1));

        System.out.println("Задачи в первом менеджере:");
        printManagerState(manager1);

        System.out.println("\n=== ЗАГРУЖАЕМ МЕНЕДЖЕР ИЗ ФАЙЛА ===");
        FileBackedTaskManager manager2 = FileBackedTaskManager.loadFromFile(file);

        System.out.println("Задачи во втором менеджере:");
        printManagerState(manager2);

        System.out.println("\n=== ПРОВЕРКА СОВПАДЕНИЯ ===");
        checkEquality(manager1, manager2);

        System.out.println("\n=== СОДЕРЖИМОЕ ФАЙЛА ===");
        System.out.println(Files.readString(file.toPath()));

        file.delete();
    }

    private static void printManagerState(FileBackedTaskManager manager) {
        System.out.println("Задачи: " + manager.getAllTasks().size());
        System.out.println("Эпики: " + manager.getAllEpics().size());
        System.out.println("Подзадачи: " + manager.getAllSubtasks().size());
        System.out.println("История: " + manager.getHistory().size());
    }

    private static void checkEquality(FileBackedTaskManager manager1, FileBackedTaskManager manager2) {
        boolean tasksEqual = manager1.getAllTasks().size() == manager2.getAllTasks().size();
        boolean epicsEqual = manager1.getAllEpics().size() == manager2.getAllEpics().size();
        boolean subtasksEqual = manager1.getAllSubtasks().size() == manager2.getAllSubtasks().size();

        System.out.println("Задачи совпадают: " + (tasksEqual ? "✓" : "✗"));
        System.out.println("Эпики совпадают: " + (epicsEqual ? "✓" : "✗"));
        System.out.println("Подзадачи совпадают: " + (subtasksEqual ? "✓" : "✗"));
        System.out.println("Все данные восстановлены: " +
                (tasksEqual && epicsEqual && subtasksEqual ? "✓" : "✗"));
    }
}