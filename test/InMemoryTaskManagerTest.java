package test;

import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
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

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
        HistoryManager history = Managers.getDefaultHistory();
    }

    @Test
    void deleteEpicShouldRemoveItsSubtasks() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", TaskStatus.NEW, epic);
        taskManager.createSubtask(subtask);

        taskManager.deleteEpicById(epic.getTaskId());

        assertNull(taskManager.getSubtaskById(subtask.getTaskId()));
        assertTrue(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void updateSubtaskShouldUpdateEpicStatus() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", TaskStatus.NEW, epic);
        taskManager.createSubtask(subtask);

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());

        subtask.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);

        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }

    @Test
    void historyShouldNotContainDeletedTasks() {
        Task task = new Task("Task", "Desc", TaskStatus.NEW);
        taskManager.createTask(task);

        taskManager.getTaskById(task.getTaskId());
        taskManager.remove(task.getTaskId());

        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void shouldCalculateEpicTimeFromSubtasks() {
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
    void shouldReturnPrioritizedTasks() {
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

    @Test
    void shouldExcludeTasksWithoutStartTimeFromPrioritizedList() {
        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW);

        Task task2 = new Task("Task2", "Desc2", TaskStatus.NEW);
        task2.setStartTime(LocalDateTime.now().plusHours(1));
        task2.setDuration(Duration.ofHours(1));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();

        assertEquals(1, prioritized.size());
        assertEquals("Task2", prioritized.get(0).getTaskName());
    }

    @Test
    void shouldDetectTimeOverlapBetweenTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);

        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW);
        task1.setStartTime(startTime);
        task1.setDuration(duration);
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", "Desc2", TaskStatus.NEW);
        task2.setStartTime(startTime.plusMinutes(30));
        task2.setDuration(duration);

        assertThrows(Exception.class, () -> taskManager.createTask(task2));
    }

    @Test
    void shouldNotDetectOverlapForNonOverlappingTasks() {
        LocalDateTime startTime = LocalDateTime.now();
        Duration duration = Duration.ofHours(1);

        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW);
        task1.setStartTime(startTime);
        task1.setDuration(duration);
        taskManager.createTask(task1);

        Task task2 = new Task("Task2", "Desc2", TaskStatus.NEW);
        task2.setStartTime(startTime.plusHours(2));
        task2.setDuration(duration);

        assertDoesNotThrow(() -> taskManager.createTask(task2));
    }
}