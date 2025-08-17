import task.*;
import manager.*;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        manager.createTask(task1);

        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", TaskStatus.NEW, epic1);
        manager.createSubtask(subtask1);

        manager.getTaskById(task1.getTaskId());
        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);
        manager.getTaskById(task1.getTaskId());
        System.out.println("История: " + manager.getHistory());
    }
}