package task.dataTransferObject;

import enums.TaskStatus;
import task.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubtaskDto {
    public int id;
    public String taskName;
    public String description;
    public LocalDateTime startTime;
    public Duration duration;
    public int epicId;
    public TaskStatus taskStatus;

    public SubtaskDto(Subtask subtask) {
        this.id = subtask.getId();
        this.taskName = subtask.getTaskName();
        this.description = subtask.getDescription();
        this.startTime = subtask.getStartTime();
        this.duration = subtask.getDuration();
        this.epicId = subtask.getEpicId();
        this.taskStatus = subtask.getTaskStatus();
    }
}
