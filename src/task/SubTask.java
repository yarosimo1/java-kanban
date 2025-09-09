package task;

import enums.TypeTasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Epic epic;
    private int epicId;

    public SubTask(String taskName, String description, LocalDateTime startTime, Duration duration) {
        super(taskName, description, TypeTasks.SUBTASK, startTime, duration);
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