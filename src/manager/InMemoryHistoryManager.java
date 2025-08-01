package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;
import task.*;

public class InMemoryHistoryManager implements  HistoryManager {
    private static final int OLD_TASK = 0;
    private static final int HISTORY_LIST_CAPACITY = 10;
    private final List<Task> taskHistoryList = new ArrayList<>(HISTORY_LIST_CAPACITY);


    @Override
    public void add(Task task) {
        if (isHistoryListFull()) {
            taskHistoryList.remove(OLD_TASK);
        }
        Task newTask = copyTask(task);
        taskHistoryList.add(newTask);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = taskHistoryList;
        return result;
    }

    public boolean isHistoryListFull() {
        return taskHistoryList.size() == HISTORY_LIST_CAPACITY;
    }

    private Task copyTask(Task original) {
        if (original == null) {
            throw new IllegalArgumentException("Нельзя скопировать пустую задачу");
        }
        return new Task(original.getTaskName(), original.getTaskDescription(),
                original.getTaskStatus());
    }
}
