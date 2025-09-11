package task;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String taskName, String taskDescription, TaskStatus taskStatus, Epic epic) {
        super(taskName, taskDescription, taskStatus);
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