package test;

import inMemory.InMemoryHistoryManager;
import manager.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InMemoryHistoryManagerTest {
    private InMemoryHistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        historyManager = (InMemoryHistoryManager) Managers.getDefaultHistory();
    }

    @Test
    public void addShouldStoreTaskInHistory() {
        Task task = new Task("Test", "Description", TaskStatus.NEW);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void addShouldNotStoreDuplicates() {
        Task task = new Task("Test", "Description", TaskStatus.NEW);
        task.setTaskId(1);

        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    public void removeShouldDeleteTaskFromHistory() {
        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW);
        task1.setTaskId(1);
        Task task2 = new Task("Task2", "Desc2", TaskStatus.NEW);
        task2.setTaskId(2);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.remove(1);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    public void getHistoryShouldReturnTasksInOrderOfAddition() {
        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW);
        task1.setTaskId(1);
        Task task2 = new Task("Task2", "Desc2", TaskStatus.NEW);
        task2.setTaskId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    public void removeFromMiddleShouldKeepOrder() {
        Task task1 = new Task("Task1", "Desc1", TaskStatus.NEW);
        task1.setTaskId(1);
        Task task2 = new Task("Task2", "Desc2", TaskStatus.NEW);
        task2.setTaskId(2);
        Task task3 = new Task("Task3", "Desc3", TaskStatus.NEW);
        task3.setTaskId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    public void shouldStoreTasksWithTimeFieldsInHistory() {
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Duration duration = Duration.ofHours(2);

        Task task = new Task("Test", "Description", TaskStatus.NEW);
        task.setStartTime(startTime);
        task.setDuration(duration);
        task.setTaskId(1);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        Task historyTask = history.get(0);

        assertEquals(startTime, historyTask.getStartTime());
        assertEquals(duration, historyTask.getDuration());
        assertEquals(startTime.plus(duration), historyTask.getEndTime());
    }

    @Test
    public void shouldHandleTasksWithNullTimeFieldsInHistory() {
        Task task = new Task("Test", "Description", TaskStatus.NEW);
        task.setTaskId(1);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        Task historyTask = history.get(0);

        assertNull(historyTask.getStartTime());
        assertNull(historyTask.getDuration());
        assertNull(historyTask.getEndTime());
    }
}