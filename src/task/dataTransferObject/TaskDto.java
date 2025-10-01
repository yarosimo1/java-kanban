package task.dataTransferObject;

import enums.TaskStatus;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskDto {
    public int id;
    public String taskName;
    public String description;
    public LocalDateTime startTime;
    public Duration duration;
    public TaskStatus taskStatus;

    public TaskDto(Task task) {
        this.id = task.getId();
        this.taskName = task.getTaskName();
        this.description = task.getDescription();
        this.startTime = task.getStartTime();
        this.duration = task.getDuration();
        this.taskStatus = task.getTaskStatus();
    }
}
