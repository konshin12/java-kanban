package test;

import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
class InMemoryTaskManagerTest {

    TaskManager taskManager;
    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Таска адин", "Дескрипсион", TaskStatus.NEW);
        task1.setTaskId(1);
        Task task2 = new Task("Таск два", "Другой тентасьон", TaskStatus.IN_PROGRESS);
        task2.setTaskId(1);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    // Тест 2: Проверка равенства наследников Task по id
    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Epic epic = new Epic("Эпис", "Опись");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Сабтаска", "Описаня", TaskStatus.NEW, epic);
        subtask1.setTaskId(2);
        Subtask subtask2 = new Subtask("Сабтаска", "Описание", TaskStatus.DONE, epic);
        subtask2.setTaskId(2);

        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны");
    }

    // Тест 3: Epic не может быть подзадачей самого себя
    @Test
    void epicRejectsSelfAsSubtask() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.createEpic(epic);

        Subtask potentialSubtask = new Subtask("Подзадача под прикрытием", "описание", TaskStatus.NEW, epic);
        potentialSubtask.setTaskId(epic.getTaskId());

        taskManager.createSubtask(potentialSubtask);

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getTaskId());
        assertTrue(subtasks.isEmpty(), "Подзадача с id эпика не должна была добавиться");
    }

    // Тест 4: Subtask не может быть своим же эпиком
    @Test
    void subtaskCannotBeItsOwnEpic() {
        Epic epic = new Epic("Полный эпик", "Описание");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Полный подтаск", "Описание", TaskStatus.NEW, epic);
        subtask.setTaskId(2);

        subtask.setTaskId(subtask.getTaskId());

        taskManager.createSubtask(subtask);

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(subtask.getTaskId());
        assertTrue(subtasks.isEmpty(),
                "Подзадача не должна быть добавлена как подзадача самой себе");
    }

    @Test
    void shouldAddAndFindDifferentTaskTypes() {
        // Создаем задачи
        Task task = new Task("Таск", "Опис", TaskStatus.NEW);
        Epic epic = new Epic("Эпик", "Опис");
        Subtask subtask = new Subtask("Subtask", "Desc", TaskStatus.NEW, epic);

        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        assertNotNull(taskManager.getTaskById(task.getTaskId()), "Не найдена задача");
        assertNotNull(taskManager.getEpicById(epic.getTaskId()), "Не найден эпик");
        assertNotNull(taskManager.getSubtaskById(subtask.getTaskId()), "Не найдена подзадача");
    }

    @Test
    void tasksWithAssignedAndGeneratedIdsShouldNotConflict() {
        Task taskWithId = new Task("Таскуня 1", "Опись", TaskStatus.NEW);
        taskWithId.setTaskId(10);
        taskManager.createTask(taskWithId);

        Task taskWithoutId = new Task("Таскуня 2", "ОПись", TaskStatus.NEW);
        taskManager.createTask(taskWithoutId);

        assertNotEquals(taskWithId.getTaskId(), taskWithoutId.getTaskId(),
                "Id задач не должны конфликтовать");
    }

    @Test
    void taskShouldRemainUnchangedAfterAddingToManager() {
        Task originalTask = new Task("Оригинал", "Описание оригинала", TaskStatus.NEW);

        Task addedTask = taskManager.createTask(originalTask);

        assertEquals(originalTask.getTaskName(), addedTask.getTaskName(), "Имя задачи изменилось");
        assertEquals(originalTask.getTaskDescription(), addedTask.getTaskDescription(), "Описание изменилось");
        assertEquals(originalTask.getTaskStatus(), addedTask.getTaskStatus(), "Статус изменился");
    }
}