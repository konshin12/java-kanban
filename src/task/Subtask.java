package task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;

    // Конструктор без параметроу для гсон
    public Subtask() {
    }

    public Subtask(String taskName, String taskDescription, TaskStatus taskStatus, Epic epic) {
        super(taskName, taskDescription, taskStatus);
        if (epic != null) {
            this.epicId = epic.getTaskId();
        }
    }

    public Subtask(String taskName, String taskDescription, TaskStatus taskStatus, Epic epic,
                   Duration duration, LocalDateTime startTime) {
        super(taskName, taskDescription, taskStatus, duration, startTime);
        if (epic != null) {
            this.epicId = epic.getTaskId();
        }
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}