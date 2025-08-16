package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> taskHistoryList = new ArrayList<>();

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;


    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        final int taskId = task.getTaskId();
        remove(taskId);
        linkLast(task);
        nodeMap.put(taskId, last);
    }

    private Task copyTask(Task original) {
        if (original == null) {
            throw new IllegalArgumentException("Нельзя скопировать пустую задачу");
        }
        return new Task(original.getTaskName(), original.getTaskDescription(),
                original.getTaskStatus());
    }

    public void removeNode(int id) {
        final Node node = nodeMap.remove(id);
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        }

        if (node == first) {
            first = node.next;
        }
        if (node == last) {
            last = node.prev;
        }
    }

    public void linkLast(Task task) {
        last = new Node(task, null, last);
    }

    public void remove(int id) {
        removeNode(id);
    }

    public List<Task> getTasks() {
        for (Node node : nodeMap.values()) {
            taskHistoryList.add(node.task);
        }
        return taskHistoryList;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "taskHistoryList=" + taskHistoryList +
                ", nodeMap=" + nodeMap +
                ", first=" + first +
                ", last=" + last +
                '}';
    }
}
