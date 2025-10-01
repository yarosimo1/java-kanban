package task;

import enums.TypeTasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private Epic epic;
    private int epicId;

    public Subtask(String taskName, String description, LocalDateTime startTime, Duration duration) {
        super(taskName, description, TypeTasks.SUBTASK, startTime, duration);
    }

    public Subtask(String taskName, String description, LocalDateTime startTime, Duration duration, Epic epic) {
        this(taskName, description, startTime, duration);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}