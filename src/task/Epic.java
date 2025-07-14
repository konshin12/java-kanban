package task;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskStatus.NEW);
        subtasksIds = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtasksId() {
        return subtasksIds;
    }

    public void addSubtaskId(int subtaskId) {
        subtasksIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        if (getSubtasksId().contains(subtaskId)) {
            getSubtasksId().remove(Integer.valueOf(subtaskId));
        }
    }

    public void deleteSubtaskIds() {
        subtasksIds.clear();
    }
}
