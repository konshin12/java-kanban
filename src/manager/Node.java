package manager;

import task.Task;

public class Node {
    Task task;
    Node next;
    Node prev;

    Node(Task task, Node next, Node prev) {
        this.task = task;
        this.next = next;
        this.prev = prev;
    }
}