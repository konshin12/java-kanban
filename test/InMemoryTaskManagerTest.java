package test;

import inmemory.InMemoryTaskManager;
import manager.Managers;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryTaskManagerTest extends test.TaskManagerTest<InMemoryTaskManager> {

    InMemoryTaskManager taskManager = createTaskManager();

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return (InMemoryTaskManager) Managers.getDefault();
    }

    @Test
    public void epicStatusShouldBeNewWhenAllSubtasksNew() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Sub1", "Desc1", TaskStatus.NEW, epic));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Sub2", "Desc2", TaskStatus.NEW, epic));

        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    public void epicStatusShouldBeDoneWhenAllSubtasksDone() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Sub1", "Desc1", TaskStatus.DONE, epic));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Sub2", "Desc2", TaskStatus.DONE, epic));

        assertEquals(TaskStatus.DONE, epic.getTaskStatus());
    }

    @Test
    public void epicStatusShouldBeInProgressWhenSubtasksMixed() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Sub1", "Desc1", TaskStatus.NEW, epic));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Sub2", "Desc2", TaskStatus.DONE, epic));

        assertEquals(TaskStatus.IN_PROGRESS, epic.getTaskStatus());
    }
}