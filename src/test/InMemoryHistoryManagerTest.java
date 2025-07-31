package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import manager.*;
import task.*;

class InMemoryHistoryManagerTest {

    public static final int FIRST_ELEMENT = 0;
    public static final int THIRD_ELEMENT = 2;
    TaskManager taskManager = Managers.getDefault();

    @Test
    void historyManagerShouldPreserveTaskState() {
        Task task = new Task("Task", "Desc", TaskStatus.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("LLL", "опись");
        taskManager.createEpic(epic);

        // Первое добавление в историю
        taskManager.getTaskById(task.getTaskId());
        taskManager.getEpicById(epic.getTaskId());
        Task firstVersion = taskManager.getHistory().get(FIRST_ELEMENT);

        // Изменяем задачу
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        epic.setTaskDescription("Юный");
        taskManager.updateTask(task);

        // Второе добавление в историю
        taskManager.getTaskById(task.getTaskId());
        taskManager.getEpicById(epic.getTaskId());
        Task secondVersion = taskManager.getHistory().get(THIRD_ELEMENT);

        assertNotEquals(firstVersion.getTaskStatus(), secondVersion.getTaskStatus(),
                "HistoryManager должен сохранять разные состояния задачи");
    }
}