import java.awt.*;
import java.sql.SQLOutput;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Task task1 = new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1");
        Epic epic2 = new Epic("Эпик2", "Описание эпика2");
        TaskManager taskManager = new TaskManager();
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        Subtask subtaskOfEpic1 = new Subtask("Подзачада 1", "Описание подзадачи1", TaskStatus.NEW, epic1);
        Subtask subtaskOfEpic2 = new Subtask("Подзачада 2", "Описание подзадачи2", TaskStatus.NEW, epic2);
        Subtask subtaskOfEpic21 = new Subtask("Подзачада 3", "Описание подзадачи3", TaskStatus.DONE, epic2);
        Subtask subtaskOfEpic223 = new Subtask("Подзачада 3", "Описание подзадачи3", TaskStatus.DONE, epic2);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createSubtask(subtaskOfEpic1);
        taskManager.createSubtask(subtaskOfEpic2);
        taskManager.createSubtask(subtaskOfEpic21);
        taskManager.createSubtask(subtaskOfEpic223);

        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllTasks());

        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
        subtaskOfEpic2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateTask(task1);
        taskManager.updateSubtask(subtaskOfEpic2);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        taskManager.deleteAllTasks();
        taskManager.deleteEpicById(2);

        System.out.println(taskManager.getAllTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());
    }
}
