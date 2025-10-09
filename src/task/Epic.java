package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Integer> subtasksIds;
    private LocalDateTime endTime;

    public Epic(String taskName, String taskDescription) {
        super(taskName, taskDescription, TaskStatus.NEW);
        subtasksIds = new ArrayList<>();
    }

    public Epic(String taskName, String taskDescription, Duration duration, LocalDateTime startTime) {
        super(taskName, taskDescription, TaskStatus.NEW, duration, startTime);
        subtasksIds = new ArrayList<>();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
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