package manager;

import task.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
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

            if (node.task.equals(copyTask)) {
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
        return copyTask;
    }

    private void removeNode(int id) {
        final Node node = nodeMap.remove(id);

        if (node == null) {
            return;
        }

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

    private void linkLast(Task task) {
        last = new Node(task, null, last);
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    private List<Task> getTasks() {
        final List<Task> taskHistoryList = new ArrayList<>();
        for (Node node : nodeMap.values()) {
            taskHistoryList.add(node.task);
        }
        return taskHistoryList;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
