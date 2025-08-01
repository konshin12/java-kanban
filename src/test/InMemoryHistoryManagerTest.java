package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import manager.*;
import task.*;

class InMemoryHistoryManagerTest {

    TaskManager taskManager = Managers.getDefault();

    @Test
    void shouldReturnTrueWhenTasksAreDifferent() {
        Task task = new Task("Оригинальная задача", "Описание оригинальной задачи", TaskStatus.NEW);
        taskManager.createTask(task); // создаем задачу

        taskManager.getTaskById(task.getTaskId()); // получаем задачу по id (должна быть просмотрена и добавлена в историю)
        Task firstVersionOfTask = taskManager.getHistory().get(0); // достаем из истории просмотров первую задачу

        task.setTaskStatus(TaskStatus.IN_PROGRESS);// меняем статус задачи
        task.setTaskDescription("Описание измененной задачи"); // меняем описание задачи
        taskManager.updateTask(task); // обновляем задачу (статус и описание сохраненной в истории задачи не должен измениться)

        taskManager.getTaskById(task.getTaskId()); // получаем обновленную задачу по id (должна быть просмотрена и добавлена в историю)
        Task updatedVersionOfTask = taskManager.getHistory().get(1); // достаем из истории просмотров вторую задачу

        // Сравниваем оригинальную и обновленную версии
        // При отработке теста - ожидаем true
        assertNotEquals(firstVersionOfTask.getTaskStatus(), updatedVersionOfTask.getTaskStatus(),
                "HistoryManager должен сохранять разные состояния задачи");
    }
}