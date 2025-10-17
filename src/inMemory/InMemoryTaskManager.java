package inMemory;

import base.HistoryManager;
import base.TaskManager;
import manager.ManagerSaveException;
import manager.Managers;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Set<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getTaskId)
    );
    private int id = 1;

    public InMemoryTaskManager() {
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean hasTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task1.getEndTime() == null ||
                task2.getStartTime() == null || task2.getEndTime() == null) {
            return false;
        }

        return task1.getStartTime().isBefore(task2.getEndTime()) &&
                task2.getStartTime().isBefore(task1.getEndTime());
    }

    private boolean hasTimeOverlapWithAny(Task task) {
        if (task.getStartTime() == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(t -> t.getStartTime() != null)
                .anyMatch(t -> !t.equals(task) && hasTimeOverlap(task, t));
    }

    private void addToPrioritizedTasks(Task task) {
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritizedTasks(Task task) {
        prioritizedTasks.remove(task);
    }

    private void updateEpicStartTime(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByEpicId(epic.getTaskId());

        LocalDateTime earliestStart = epicSubtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        epic.setStartTime(earliestStart);
    }

    private void updateEpicEndTime(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByEpicId(epic.getTaskId());

        LocalDateTime latestEnd = epicSubtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setEndTime(latestEnd);
    }

    private void updateEpicDuration(Epic epic) {
        List<Subtask> epicSubtasks = getSubtasksByEpicId(epic.getTaskId());

        Duration totalDuration = epicSubtasks.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setDuration(totalDuration);
    }

    private void updateEpicTime(Epic epic) {
        updateEpicStartTime(epic);
        updateEpicEndTime(epic);
        updateEpicDuration(epic);
    }

    //Методы для Task
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        historyManager.add(task);
        return tasks.get(id);
    }

    @Override
    public Task createTask(Task task) {
        if (hasTimeOverlapWithAny(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей задачей");
        }

        task.setTaskId(id++);
        tasks.put(task.getTaskId(), task);
        addToPrioritizedTasks(task);
        return task;
    }

    @Override
    public Task updateTask(Task task) {
        if (hasTimeOverlapWithAny(task)) {
            throw new ManagerSaveException("Задача пересекается по времени с существующей задачей");
        }

        if (tasks.containsKey(task.getTaskId())) {
            Task oldTask = tasks.get(task.getTaskId());
            removeFromPrioritizedTasks(oldTask);
            tasks.put(task.getTaskId(), task);
            addToPrioritizedTasks(task);
        }
        return task;
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            removeFromPrioritizedTasks(task);
        }
    }

    //Методы для Subtask
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteSubtaskIds();
            updateEpicStatus(epic.getTaskId());
        }
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        historyManager.add(subtask);
        return subtasks.get(id);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (hasTimeOverlapWithAny(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующей задачей");
        }

        if (!epics.containsKey(subtask.getEpicId())) {
            throw new IllegalArgumentException("Эпик не существует");
        }

        subtask.setTaskId(id++);
        subtasks.put(subtask.getTaskId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtaskId(subtask.getTaskId());
            updateEpicStatus(subtask.getEpicId());
            updateEpicTime(epic);
        }
        addToPrioritizedTasks(subtask);
        return subtask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (hasTimeOverlapWithAny(subtask)) {
            throw new ManagerSaveException("Подзадача пересекается по времени с существующей задачей");
        }

        if (subtasks.containsKey(subtask.getTaskId())) {
            Subtask oldSubtask = subtasks.get(subtask.getTaskId());
            removeFromPrioritizedTasks(oldSubtask);
            subtasks.put(subtask.getTaskId(), subtask);
            updateEpicStatus(subtask.getEpicId());
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                updateEpicTime(epic);
            }
            addToPrioritizedTasks(subtask);
        }
        return subtask;
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            removeFromPrioritizedTasks(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskId(id);
                updateEpicStatus(epic.getTaskId());
                updateEpicTime(epic);
            }
        }
    }

    // Методы для Epic
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        historyManager.add(epic);
        return epics.get(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setTaskId(id++);
        epics.put(epic.getTaskId(), epic);
        return epic;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epics.containsKey(epic.getTaskId())) {
            Epic tempEpic = epics.get(epic.getTaskId());
            tempEpic.setTaskName(epic.getTaskName());
            tempEpic.setTaskDescription(epic.getTaskDescription());
        }
        return epic;
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (int subtaskId : epic.getSubtasksId()) {
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask != null) {
                    removeFromPrioritizedTasks(subtask);
                }
            }
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return new ArrayList<>();
        }

        return epic.getSubtasksId().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public void updateEpicStatus(int epicId) {
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

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public void remove(int id) {
        historyManager.remove(id);
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}