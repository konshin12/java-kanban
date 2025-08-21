import task.*;
import manager.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        System.out.println("Создание задач");

        // Создаем две задачи
        Task task1 = taskManager.createTask(new Task("Задача 1", "Описание задачи 1", TaskStatus.NEW));
        Task task2 = taskManager.createTask(new Task("Задача 2", "Описание задачи 2", TaskStatus.NEW));

        // Создаем эпик с тремя подзадачами
        Epic epic1 = taskManager.createEpic(new Epic("Эпик 1", "Описание эпика 1"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Подзадача 1", "Описание подзадачи 1", TaskStatus.NEW, epic1));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Подзадача 2", "Описание подзадачи 2", TaskStatus.IN_PROGRESS, epic1));
        Subtask subtask3 = taskManager.createSubtask(new Subtask("Подзадача 3", "Описание подзадачи 3", TaskStatus.DONE, epic1));

        // Создаем эпик без подзадач
        Epic epic2 = taskManager.createEpic(new Epic("Эпик 2", "Описание эпика 2"));

        System.out.println("Созданы задачи:");
        System.out.println("Task1: " + task1);
        System.out.println("Task2: " + task2);
        System.out.println("Epic1: " + epic1);
        System.out.println("Subtask1: " + subtask1);
        System.out.println("Subtask2: " + subtask2);
        System.out.println("Subtask3: " + subtask3);
        System.out.println("Epic2: " + epic2);
        System.out.println();
        separator();
        System.out.println("Запросы задач в разном порядке");

        // Первая последовательность запросов
        System.out.println("Запрос 1: Task1, Epic1, Subtask2");
        taskManager.getTaskById(task1.getTaskId());
        taskManager.getEpicById(epic1.getTaskId());
        taskManager.getSubtaskById(subtask2.getTaskId());

        printHistory("История после первого запроса:", taskManager.getHistory());

        // Вторая последовательность запросов (повтор + новый)
        System.out.println("Запрос 2: Task1, Task2, Epic1");
        taskManager.getTaskById(task1.getTaskId()); // повтор
        taskManager.getTaskById(task2.getTaskId()); // новый
        taskManager.getEpicById(epic1.getTaskId()); // повтор

        printHistory("История после второго запроса:", taskManager.getHistory());

        // Третья последовательность запросов (все повторные)
        System.out.println("Запрос 3: Task2, Epic1, Task1");
        taskManager.getTaskById(task2.getTaskId());
        taskManager.getEpicById(epic1.getTaskId());
        taskManager.getTaskById(task1.getTaskId());

        printHistory("История после третьего запроса:", taskManager.getHistory());

        // Четвертая последовательность (добавляем эпик без подзадач)
        System.out.println("Запрос 4: Epic2, Subtask3");
        taskManager.getEpicById(epic2.getTaskId());
        taskManager.getSubtaskById(subtask3.getTaskId());

        printHistory("История после четвертого запроса:", taskManager.getHistory());

        separator();

        System.out.println("Удаление задачи из истории");
        System.out.println("Удаляем Task2 (ID: " + task2.getTaskId() + ")");
        taskManager.deleteTaskById(task2.getTaskId());

        printHistory("История после удаления Task2:", taskManager.getHistory());
        separator();
        System.out.println("Удаление эпика с подзадачами");
        System.out.println("Удаляем Epic1 (ID: " + epic1.getTaskId() + ") с подзадачами");
        taskManager.deleteEpicById(epic1.getTaskId());

        printHistory("История после удаления Epic1:", taskManager.getHistory());

        // Проверяем, что подзадачи тоже удалены из менеджера
        System.out.println("Подзадачи Epic1 в менеджере:");
        List<Subtask> remainingSubtasks = taskManager.getAllSubtasks();
        if (remainingSubtasks.isEmpty()) {
            System.out.println("Все подзадачи удалены ✓");
        } else {
            System.out.println("Остались подзадачи: " + remainingSubtasks);
        }
    }

    private static void printHistory(String message, List<Task> history) {
        System.out.println(message);
        if (history.isEmpty()) {
            System.out.println("История пуста");
        } else {
            Task task;
            for (int i = 0; i < history.size(); i++) {
                task = history.get(i);
                System.out.println((i + 1) + ". " + task.getClass().getSimpleName() +
                        " [ID: " + task.getTaskId() +
                        ", Status: " + task.getTaskStatus() +
                        ", Name: " + task.getTaskName() + "]");
            }
        }
        System.out.println();
    }

    private static void separator() {
        System.out.println("-".repeat(88));
    }
}