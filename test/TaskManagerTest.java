package test;

import base.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    public void createTaskShouldAddTask() {
        Task task = new Task("Test", "Description", TaskStatus.NEW);
        Task created = taskManager.createTask(task);

        assertNotNull(created.getTaskId());
        assertEquals(1, taskManager.getAllTasks().size());
        assertEquals(task.getTaskName(), created.getTaskName());
    }

    @Test
    public void createTaskWithEmptyList() {
        Task task = new Task("Test", "Description", TaskStatus.NEW);
        Task created = taskManager.createTask(task);

        assertFalse(taskManager.getAllTasks().isEmpty());
        assertEquals(1, taskManager.getAllTasks().size());
    }

    @Test
    public void getAllTasksWithEmptyList() {
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void getTaskByIdWithValidId() {
        Task task = new Task("Test", "Description", TaskStatus.NEW);
        Task created = taskManager.createTask(task);

        Task found = taskManager.getTaskById(created.getTaskId());
        assertNotNull(found);
        assertEquals(created.getTaskId(), found.getTaskId());
    }

    @Test
    public void getTaskByIdWithInvalidId() {
        assertNull(taskManager.getTaskById(-1));
        assertNull(taskManager.getTaskById(999));
    }

    @Test
    public void updateTaskWithValidId() {
        Task task = new Task("Test", "Description", TaskStatus.NEW);
        Task created = taskManager.createTask(task);
        created.setTaskName("Updated");

        taskManager.updateTask(created);
        Task updated = taskManager.getTaskById(created.getTaskId());

        assertEquals("Updated", updated.getTaskName());
    }

    @Test
    public void updateTaskWithInvalidId() {
        Task task = new Task("Test", "Description", TaskStatus.NEW);
        task.setTaskId(999); // Несуществующий ID

        Task result = taskManager.updateTask(task);
        assertEquals(task, result); // Должен вернуть исходную задачу без изменений
    }

    @Test
    public void deleteTaskByIdWithValidId() {
        Task task = new Task("Test", "Description", TaskStatus.NEW);
        Task created = taskManager.createTask(task);

        taskManager.deleteTaskById(created.getTaskId());
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void deleteTaskByIdWithInvalidId() {
        assertDoesNotThrow(() -> taskManager.deleteTaskById(999));
    }

    @Test
    public void deleteAllTasks() {
        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW);
        Task task2 = new Task("Task2", "Desc2", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.deleteAllTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void createEpicShouldAddEpic() {
        Epic epic = new Epic("Epic", "Description");
        Epic created = taskManager.createEpic(epic);

        assertNotNull(created.getTaskId());
        assertEquals(1, taskManager.getAllEpics().size());
    }

    @Test
    public void getEpicByIdWithValidId() {
        Epic epic = new Epic("Epic", "Description");
        Epic created = taskManager.createEpic(epic);

        Epic found = taskManager.getEpicById(created.getTaskId());
        assertNotNull(found);
        assertEquals(created.getTaskId(), found.getTaskId());
    }

    @Test
    public void getEpicByIdWithInvalidId() {
        assertNull(taskManager.getEpicById(-1));
        assertNull(taskManager.getEpicById(999));
    }

    @Test
    public void updateEpicWithValidId() {
        Epic epic = new Epic("Epic", "Description");
        Epic created = taskManager.createEpic(epic);
        created.setTaskName("Updated Epic");

        taskManager.updateEpic(created);
        Epic updated = taskManager.getEpicById(created.getTaskId());

        assertEquals("Updated Epic", updated.getTaskName());
    }

    @Test
    public void deleteEpicByIdWithValidId() {
        Epic epic = new Epic("Epic", "Description");
        Epic created = taskManager.createEpic(epic);

        taskManager.deleteEpicById(created.getTaskId());
        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    public void deleteAllEpics() {
        Epic epic1 = new Epic("Epic1", "Desc1");
        Epic epic2 = new Epic("Epic2", "Desc2");
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        taskManager.deleteAllEpics();
        assertTrue(taskManager.getAllEpics().isEmpty());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void createSubtaskShouldAddSubtask() {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Description", TaskStatus.NEW, createdEpic);
        Subtask created = taskManager.createSubtask(subtask);

        assertNotNull(created.getTaskId());
        assertEquals(1, taskManager.getAllSubtasks().size());
    }

    @Test
    public void createSubtaskWithInvalidEpicId() {
        Subtask subtask = new Subtask("Subtask", "Description", TaskStatus.NEW,
                new Epic("Temp", "Temp"));
        subtask.setEpicId(999); // Несуществующий эпик

        assertThrows(IllegalArgumentException.class, () -> taskManager.createSubtask(subtask));
    }

    @Test
    public void getSubtaskByIdWithValidId() {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", TaskStatus.NEW, createdEpic);
        Subtask created = taskManager.createSubtask(subtask);

        Subtask found = taskManager.getSubtaskById(created.getTaskId());
        assertNotNull(found);
        assertEquals(created.getTaskId(), found.getTaskId());
    }

    @Test
    public void getSubtaskByIdWithInvalidId() {
        assertNull(taskManager.getSubtaskById(-1));
        assertNull(taskManager.getSubtaskById(999));
    }

    @Test
    public void updateSubtaskWithValidId() {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", TaskStatus.NEW, createdEpic);
        Subtask created = taskManager.createSubtask(subtask);
        created.setTaskName("Updated Subtask");

        taskManager.updateSubtask(created);
        Subtask updated = taskManager.getSubtaskById(created.getTaskId());

        assertEquals("Updated Subtask", updated.getTaskName());
    }

    @Test
    public void deleteSubtaskByIdWithValidId() {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", TaskStatus.NEW, createdEpic);
        Subtask created = taskManager.createSubtask(subtask);

        taskManager.deleteSubtaskById(created.getTaskId());
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void deleteAllSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask1 = new Subtask("Subtask1", "Desc1", TaskStatus.NEW, createdEpic);
        Subtask subtask2 = new Subtask("Subtask2", "Desc2", TaskStatus.NEW, createdEpic);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        taskManager.deleteAllSubtasks();
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    public void getSubtasksByEpicIdWithValidId() {
        Epic epic = new Epic("Epic", "Description");
        Epic createdEpic = taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Description", TaskStatus.NEW, createdEpic);
        taskManager.createSubtask(subtask);

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(createdEpic.getTaskId());
        assertFalse(subtasks.isEmpty());
        assertEquals(1, subtasks.size());
    }

    @Test
    public void getSubtasksByEpicIdWithInvalidId() {
        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(999);
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void getHistoryShouldReturnEmptyListInitially() {
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    public void getHistoryShouldReturnTasksAfterAccess() {
        Task task = new Task("Task", "Description", TaskStatus.NEW);
        Task created = taskManager.createTask(task);

        taskManager.getTaskById(created.getTaskId());
        List<Task> history = taskManager.getHistory();

        assertFalse(history.isEmpty());
        assertEquals(1, history.size());
    }

    @Test
    public void shouldCalculateEpicTimeFromSubtasks() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);

        LocalDateTime startTime1 = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime startTime2 = LocalDateTime.of(2024, 1, 1, 12, 0);
        Duration duration1 = Duration.ofHours(1);
        Duration duration2 = Duration.ofHours(2);

        Subtask subtask1 = new Subtask("Subtask1", "Desc1", TaskStatus.NEW, epic);
        subtask1.setStartTime(startTime1);
        subtask1.setDuration(duration1);
        taskManager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Subtask2", "Desc2", TaskStatus.NEW, epic);
        subtask2.setStartTime(startTime2);
        subtask2.setDuration(duration2);
        taskManager.createSubtask(subtask2);

        Epic updatedEpic = taskManager.getEpicById(epic.getTaskId());

        assertEquals(startTime1, updatedEpic.getStartTime());
        assertEquals(Duration.ofHours(3), updatedEpic.getDuration());
        assertEquals(startTime2.plus(duration2), updatedEpic.getEndTime());
    }

    @Test
    public void shouldReturnPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW);
        task1.setStartTime(now.plusHours(2));
        task1.setDuration(Duration.ofHours(1));

        Task task2 = new Task("Task2", "Desc2", TaskStatus.NEW);
        task2.setStartTime(now.plusHours(1));
        task2.setDuration(Duration.ofHours(1));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();

        assertEquals(2, prioritized.size());
        assertEquals("Task2", prioritized.get(0).getTaskName());
        assertEquals("Task1", prioritized.get(1).getTaskName());
    }
}