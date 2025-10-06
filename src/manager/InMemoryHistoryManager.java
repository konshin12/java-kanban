package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements  HistoryManager {
    private static final int OLD_TASK = 0;
    private static final int HISTORY_LIST_CAPACITY = 10;
    private final List<Task> taskHistoryList = new ArrayList<>(HISTORY_LIST_CAPACITY);

public class InMemoryHistoryManager implements HistoryManager {
    private static final int OLD_TASK = 0;
    private static final int HISTORY_LIST_CAPACITY = 10;
    private final List<Task> taskHistoryList = new ArrayList<>(HISTORY_LIST_CAPACITY);
    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        int taskId = task.getTaskId();
        Task copyTask = copyTask(task);

        if (nodeMap.containsKey(taskId)) {
            Node node = nodeMap.get(taskId);

            if (node.getTask().equals(copyTask)) {
                remove(taskId);
            }
        }
        linkLast(copyTask);
        nodeMap.put(taskId, last);
    }

    private Task copyTask(Task original) {
        if (original == null) {
            throw new IllegalArgumentException("Нельзя скопировать пустую задачу");
        }

        Task copyTask = new Task(original.getTaskName(), original.getTaskDescription(),
                original.getTaskStatus());
        copyTask.setTaskId(original.getTaskId());

        copyTask.setStartTime(original.getStartTime());
        copyTask.setDuration(original.getDuration());

        return copyTask;
    }

    private void removeNode(int id) {
        final Node node = nodeMap.remove(id);

        if (node == null) {
            return;
        }

        if (node.getPrev() != null) {
            node.getPrev().setNext(node.getNext());
        }

        if (node.getNext() != null) {
            node.getNext().setPrev(node.getPrev());
        }

        if (node == first) {
            first = node.getNext();
        }

        if (node == last) {
            last = node.getPrev();
        }
    }

    private void linkLast(Task task) {
        final Node newNode = new Node(task, null, last);
        if (last == null) {
            first = newNode;
        } else {
            last.setNext(newNode);
        }
        last = newNode;
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    private List<Task> getTasks() {
        final List<Task> taskHistoryList = new ArrayList<>();
        Node current = first;
        while (current != null) {
            taskHistoryList.add(current.getTask());
            current = current.getNext();
        }
        return taskHistoryList;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}