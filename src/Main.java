import task.*;
import manager.*;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HistoryManager managerHistory = Managers.getDefaultHistory();

//        Task task1 = new Task("Задача1", "Доделать уже этот тасктрекер))))", TaskStatus.NEW);
//        Task task2 = new Task("Задача2", "Описание задачи2", TaskStatus.NEW);

//        TaskManager manager = new InMemoryTaskManager();

        // Создаем задачи
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        manager.createTask(task1);

        Epic epic1 = new Epic("Epic 1", "Description Epic 1");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Desc 1", TaskStatus.NEW, epic1);
        manager.createSubtask(subtask1);

        // Вызываем методы для проверки истории
        manager.getTaskById(task1.getTaskId());
        manager.getEpicById(epic1.getTaskId());
        manager.getSubtaskById(subtask1.getTaskId());

        // Печатаем все задачи и историю
        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("\nЗадачи:");
        manager.getAllTasks().forEach(System.out::println);

        System.out.println("\nЭпики:");
        manager.getAllEpics().forEach(epic -> {
            System.out.println(epic);
            manager.getSubtasksByEpicId(epic.getTaskId()).forEach(subtask ->
                    System.out.println("--> " + subtask));
        });

        System.out.println("\nПодзадачи:");
        manager.getAllSubtasks().forEach(System.out::println);

        System.out.println("\nИстория:");
        manager.getHistory().forEach(System.out::println);
    }
//
//        manager.createTask(task1);
//        manager.createTask(task2);
//
//        System.out.println(manager.getAllTasks());
//
//        task1.setTaskStatus(TaskStatus.IN_PROGRESS);
//        manager.updateTask(task1);
//        System.out.println(manager.getTaskById(task1.getTaskId()));
//
//        manager.deleteTaskById(task2.getTaskId());
//        System.out.println(manager.getAllTasks());
//
//        Epic epic1 = new Epic("Эпик1", "Описание эпика 1");
//        Epic epic2 = new Epic("Эпик2", "Описание эпика 2");
//
//        manager.createEpic(epic1);
//        manager.createEpic(epic2);
////        System.out.println(manager.getAllEpics());
//
//        Subtask subtask1 = new Subtask("Подзадача1", "Описание подзадачи 1", TaskStatus.NEW, epic1);
//        Subtask subtask2 = new Subtask("Подзадача2", "Описание подзадачи 2", TaskStatus.DONE, epic1);
//        Subtask subtask3 = new Subtask("Подзадача3", "Описание подзадачи 3", TaskStatus.NEW, epic2);
//        Subtask subtask4 = new Subtask("Подзадача4", "Описание подзадачи 4", TaskStatus.NEW, epic2);
//
//        manager.createSubtask(subtask1);
//        manager.createSubtask(subtask2);
//        manager.createSubtask(subtask3);
//        manager.createSubtask(subtask4);
//

//
//        System.out.println(manager.getSubtasksByEpicId(epic1.getTaskId()));
//        System.out.println(epic1.getTaskStatus());
//
//        subtask1.setTaskStatus(TaskStatus.DONE);
//        manager.updateSubtask(subtask1);
//        System.out.println(manager.getSubtaskById(subtask1.getTaskId()));
//        System.out.println(epic1.getTaskStatus());
//
//        manager.deleteSubtaskById(subtask3.getTaskId());
//        System.out.println(manager.getSubtasksByEpicId(epic2.getTaskId()));
//
//        manager.deleteAllTasks();
//        manager.deleteAllSubtasks();
//        manager.deleteAllEpics();
//
//        System.out.println(manager.getAllTasks());
//        System.out.println(manager.getAllSubtasks());
//        System.out.println(manager.getAllEpics());
//
//        Epic testEpic = manager.createEpic(new Epic("Тест", "Тестовый эпик"));
//        System.out.println(manager.getEpicById(testEpic.getTaskId()));
//
//
//        testEpic.setTaskDescription("Новое описание");
//        manager.updateEpic(testEpic);
//        System.out.println(manager.getEpicById(testEpic.getTaskId()));
//
//        manager.deleteEpicById(testEpic.getTaskId());
//        System.out.println(manager.getAllEpics());
}