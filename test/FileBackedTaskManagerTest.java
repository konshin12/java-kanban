package test;

import manager.FileBackedTaskManager;
import manager.ManagerSaveException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldSaveAndLoadEmptyManager() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // Сохраняем пустой менеджер
        manager.save();

        // Загружаем обратно
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    void shouldHandleFileErrors() {
        File file = new File("/invalid/path/test.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        assertThrows(ManagerSaveException.class, manager::save);
    }

    @Test
    void shouldPreserveTaskState() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        // Создаем и изменяем задачу
        Task task = manager.createTask(new Task("Test", "Description", TaskStatus.NEW));
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task);

        // Загружаем обратно
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task loadedTask = loadedManager.getTaskById(task.getTaskId());

        assertEquals(TaskStatus.IN_PROGRESS, loadedTask.getTaskStatus());
    }
}