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
    void shouldNotAllowSubtaskToBeItsOwnEpic() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Desc", TaskStatus.NEW, epic);
        subtask.setTaskId(epic.getTaskId()); // Пытаемся сделать подзадачу своим эпиком

        taskManager.createSubtask(subtask);

        assertTrue(taskManager.getSubtasksByEpicId(epic.getTaskId()).isEmpty());
    }

    @Test
    void historyShouldNotContainDeletedTasks() {
        Task task = new Task("Task", "Desc", TaskStatus.NEW);
        taskManager.createTask(task);

        taskManager.getTaskById(task.getTaskId()); // Добавляем в историю
        taskManager.remove(task.getTaskId());

        assertTrue(taskManager.getHistory().isEmpty());
    }
}