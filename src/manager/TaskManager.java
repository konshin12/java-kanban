package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.List;

public interface TaskManager {
    //Методы для Task
    List<Task> getAllTasks();
    void deleteAllTasks();
    Task getTaskById(int id);
    Task createTask(Task task);
    Task updateTask(Task task);
    void deleteTaskById(int id);

    //Методы для Subtask
    List<Subtask> getAllSubtasks();
    void deleteAllSubtasks();
    Subtask getSubtaskById(int id);
    Subtask createSubtask(Subtask subtask);
    Subtask updateSubtask(Subtask subtask);
    void deleteSubtaskById(int id);

    // Методы для Epic
    List<Epic> getAllEpics();
    void deleteAllEpics();
    Epic getEpicById(int id);
    Epic createEpic(Epic epic);
    Epic updateEpic(Epic epic);
    void deleteEpicById(int id);
    List<Subtask> getSubtasksByEpicId(int epicId);
    void updateEpicStatus(int epicId);
    List<Task> getHistory();
}
