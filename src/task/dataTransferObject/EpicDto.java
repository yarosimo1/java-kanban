package task.dataTransferObject;

import enums.TaskStatus;
import task.Epic;
import task.Subtask;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class EpicDto {
    public int id;
    public String taskName;
    public String description;
    public LocalDateTime startTime;
    public Duration duration;
    public LocalDateTime endTime;
    public TaskStatus taskStatus;
    public List<Integer> subtaskIds;

    public EpicDto(Epic epic) {
        this.id = epic.getId();
        this.taskName = epic.getTaskName();
        this.description = epic.getDescription();
        this.startTime = epic.getStartTime();
        this.duration = epic.getDuration();
        this.endTime = epic.getEndTime();
        this.taskStatus = epic.getTaskStatus();
        this.subtaskIds = epic.getSubTasks()
                    .stream()
                    .map(Subtask::getId)
                    .collect(Collectors.toList());
    }
}
