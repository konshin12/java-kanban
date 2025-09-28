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
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldSaveAndLoadEmptyManager() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        manager.save();

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

        Task task = manager.createTask(new Task("Test", "Description", TaskStatus.NEW));
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task loadedTask = loadedManager.getTaskById(task.getTaskId());

        assertEquals(TaskStatus.IN_PROGRESS, loadedTask.getTaskStatus());
    }

    @Test
    void shouldSaveAndLoadTaskWithTimeFields() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Duration duration = Duration.ofHours(2);

        Task task = new Task("Test", "Description", TaskStatus.NEW);
        task.setStartTime(startTime);
        task.setDuration(duration);
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task loadedTask = loadedManager.getTaskById(task.getTaskId());

        assertEquals(startTime, loadedTask.getStartTime());
        assertEquals(duration, loadedTask.getDuration());
        assertEquals(startTime.plus(duration), loadedTask.getEndTime());
    }

    @Test
    void shouldHandleNullTimeFields() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task = new Task("Test", "Description", TaskStatus.NEW);
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Task loadedTask = loadedManager.getTaskById(task.getTaskId());

        assertNull(loadedTask.getStartTime());
        assertNull(loadedTask.getDuration());
        assertNull(loadedTask.getEndTime());
    }

    @Test
    void shouldCalculateEpicTimeFromSubtasks() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        task.Epic epic = new task.Epic("Epic", "Description");
        manager.createEpic(epic);

        LocalDateTime startTime1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 1, 1, 12, 0);
        Duration duration1 = Duration.ofHours(1);
        Duration duration2 = Duration.ofHours(2);

        task.Subtask subtask1 = new task.Subtask("Subtask1", "Desc1", TaskStatus.NEW, epic);
        subtask1.setStartTime(startTime1);
        subtask1.setDuration(duration1);
        manager.createSubtask(subtask1);

        task.Subtask subtask2 = new task.Subtask("Subtask2", "Desc2", TaskStatus.NEW, epic);
        subtask2.setStartTime(startTime2);
        subtask2.setDuration(duration2);
        manager.createSubtask(subtask2);

        task.Epic loadedEpic = manager.getEpicById(epic.getTaskId());
        assertEquals(startTime1, loadedEpic.getStartTime());
        assertEquals(Duration.ofHours(3), loadedEpic.getDuration());
        assertEquals(startTime2.plus(duration2), loadedEpic.getEndTime());
    }
}