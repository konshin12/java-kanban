package test;

import manager.*;
import org.junit.jupiter.api.Test;
import task.*;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends test.TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    InMemoryTaskManager taskManager = createTaskManager();

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