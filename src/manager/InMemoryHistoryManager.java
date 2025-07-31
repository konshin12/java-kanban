package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;
import task.*;

public class InMemoryHistoryManager implements  HistoryManager {
    private static final int OLD_TASK = 0;
    private static final int HISTORY_LIST_CAPACITY = 10;
    private final ArrayList<Task> taskHistoryList = new ArrayList<>(HISTORY_LIST_CAPACITY);


    @Override
    public void add(Task task) {

    }

    @Override
    public List<Task> getHistory() {
        List<Task> result = taskHistoryList;
        return result;
    }

    public boolean isHistoryListFull() {
        return taskHistoryList.size() == HISTORY_LIST_CAPACITY;
    }

//    public void addTaskToHistoryList(int id) {
//        if (isHistoryListFull()) {
//            taskHistoryList.remove(OLD_TASK);
//        }
//        taskHistoryList.add(tasks.get(id));
//    }
//
//    public void addSubTaskToHistoryList(int id) {
//        if (isHistoryListFull()) {
//            taskHistoryList.remove(OLD_TASK);
//        }
//        taskHistoryList.add(subtasks.get(id));
//    }
//
//    public void addEpicToHistoryList(int id) {
//        if (isHistoryListFull()) {
//            taskHistoryList.remove(OLD_TASK);
//        }
//        taskHistoryList.add(epics.get(id));
//    }
}
