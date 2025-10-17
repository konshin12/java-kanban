package test;

import fileBacked.FileBackedTaskManager;
import manager.ManagerSaveException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends test.TaskManagerTest<FileBackedTaskManager> {

    @TempDir
    protected Path tempDir;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        File file = tempDir.resolve("test.csv").toFile();
        return new FileBackedTaskManager(file);
    }

    @Test
    public void shouldSaveAndLoadEmptyManager() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void shouldSaveAndLoadEpicWithoutSubtasks() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Epic epic = manager.createEpic(new Epic("Epic without subtasks", "Description"));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(0, loadedManager.getAllSubtasks().size());

        Epic loadedEpic = loadedManager.getEpicById(epic.getTaskId());
        assertNotNull(loadedEpic);
        assertEquals("Epic without subtasks", loadedEpic.getTaskName());
        assertEquals(TaskStatus.NEW, loadedEpic.getTaskStatus());
        assertTrue(loadedEpic.getSubtasksId().isEmpty());
    }

    @Test
    public void shouldSaveAndLoadWithEmptyHistory() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task = manager.createTask(new Task("Task", "Description", TaskStatus.NEW));
        Epic epic = manager.createEpic(new Epic("Epic", "Description"));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertTrue(loadedManager.getHistory().isEmpty());
        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
    }

    @Test
    public void shouldSaveAndLoadWithTasksEpicsAndSubtasks() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Task task = manager.createTask(new Task("Task", "Description", TaskStatus.IN_PROGRESS));
        Epic epic = manager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask = manager.createSubtask(new Subtask("Subtask", "Description", TaskStatus.DONE, epic));

        manager.getTaskById(task.getTaskId());
        manager.getEpicById(epic.getTaskId());
        manager.getSubtaskById(subtask.getTaskId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getAllTasks().size());
        assertEquals(1, loadedManager.getAllEpics().size());
        assertEquals(1, loadedManager.getAllSubtasks().size());

        Task loadedTask = loadedManager.getTaskById(task.getTaskId());
        assertNotNull(loadedTask);
        assertEquals("Task", loadedTask.getTaskName());
        assertEquals(TaskStatus.IN_PROGRESS, loadedTask.getTaskStatus());

        Epic loadedEpic = loadedManager.getEpicById(epic.getTaskId());
        assertNotNull(loadedEpic);
        assertEquals("Epic", loadedEpic.getTaskName());
        assertEquals(1, loadedEpic.getSubtasksId().size());

        Subtask loadedSubtask = loadedManager.getSubtaskById(subtask.getTaskId());
        assertNotNull(loadedSubtask);
        assertEquals("Subtask", loadedSubtask.getTaskName());
        assertEquals(TaskStatus.DONE, loadedSubtask.getTaskStatus());
        assertEquals(epic.getTaskId(), loadedSubtask.getEpicId());
    }

    @Test
    public void shouldSaveAndLoadTasksWithTimeFields() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Duration duration = Duration.ofHours(2);

        Task task = new Task("Task with time", "Description", TaskStatus.NEW);
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
    public void shouldHandleCorruptedFile() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("corrupted,data\ninvalid,format\n");
        }

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);
        assertNotNull(manager);
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldHandleNonExistentFile() {
        File file = new File("non_existent_file.csv");

        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);
        assertNotNull(manager);
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldSaveAndLoadEpicWithMultipleSubtasks() throws IOException {
        File file = tempDir.resolve("test.csv").toFile();
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        Epic epic = manager.createEpic(new Epic("Epic with multiple subtasks", "Description"));

        Subtask subtask1 = manager.createSubtask(new Subtask("Subtask 1", "Desc 1", TaskStatus.NEW, epic));
        Subtask subtask2 = manager.createSubtask(new Subtask("Subtask 2", "Desc 2", TaskStatus.IN_PROGRESS, epic));
        Subtask subtask3 = manager.createSubtask(new Subtask("Subtask 3", "Desc 3", TaskStatus.DONE, epic));

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        Epic loadedEpic = loadedManager.getEpicById(epic.getTaskId());
        assertNotNull(loadedEpic);
        assertEquals(3, loadedEpic.getSubtasksId().size());
        assertEquals(TaskStatus.IN_PROGRESS, loadedEpic.getTaskStatus());
    }

    @Test
    public void shouldThrowExceptionWhenSaveFails() {
        File file = new File("/invalid/path/test.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        assertThrows(ManagerSaveException.class, manager::save);
    }
}