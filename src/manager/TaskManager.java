package manager;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import task.*;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int id = 1;

    public TaskManager() {
    }

    //Методы для Task
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Task createTask(Task task) {
        task.setTaskId(id++);
        tasks.put(task.getTaskId(), task);
        return task;
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getTaskId())) {
            tasks.put(task.getTaskId(), task);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }

    //Методы для Subtask
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteSubtaskIds();
            updateEpicStatus(epic.getTaskId());
        }
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.setTaskId(id++);
        subtasks.put(subtask.getTaskId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getTaskId());
            updateEpicStatus(subtask.getEpicId());
        }
        return subtask;
    }

    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getTaskId())) {
            subtasks.put(subtask.getTaskId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getTaskId());
            }
        }
    }

    // Методы для Epic
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Epic createEpic(Epic epic) {
        epic.setTaskId(id++);
        epics.put(epic.getTaskId(), epic);
        return epic;
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getTaskId())) {
            Epic tempEpic = epics.get(epic.getTaskId());
            tempEpic.setTaskName(epic.getTaskName());
            tempEpic.setTaskDescription(epic.getTaskDescription());
        }
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksId()) {
                subtasks.remove(subtaskId);
            }
        }
    }

    public List<Subtask> getSubtasksByEpicId(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksId()) {
                Subtask subtask = subtasks.get(subtaskId);
                if (subtask != null) {
                    result.add(subtask);
                }
            }
        }
        return result;
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<Subtask> epicSubtasks = getSubtasksByEpicId(epicId);
        if (epicSubtasks.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean isNew = true;
        boolean inProgress = false;
        boolean done = true;

        for (Subtask subtask : epicSubtasks) {
            if (subtask.getTaskStatus() != TaskStatus.NEW) {
                isNew = false;
                inProgress = true;
            }
            if (subtask.getTaskStatus() != TaskStatus.DONE) {
                done = false;
                inProgress = true;
            }
        }

        if (isNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else if (done) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (inProgress) {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }
}