package task;

import enums.TaskStatus;
import enums.TypeTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private TypeTasks typeTasks;
    private String taskName;
    private TaskStatus taskStatus;
    private String description;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(String taskName, String description, LocalDateTime startTime, Duration duration) {
        this.taskName = taskName;
        this.taskStatus = TaskStatus.NEW;
        this.description = description;
        this.typeTasks = TypeTasks.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String taskName, String description, TypeTasks typeTasks, LocalDateTime startTime, Duration duration) {
        this.taskName = taskName;
        this.taskStatus = TaskStatus.NEW;
        this.description = description;
        this.typeTasks = typeTasks;
        this.startTime = startTime;
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", typeTasks=" + typeTasks +
                ", taskName='" + taskName + '\'' +
                ", taskStatus=" + taskStatus +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        if (this.taskStatus == taskStatus) return;

        this.taskStatus = taskStatus;

        switch (taskStatus) {
            case IN_PROGRESS -> {
                startTime = Objects.requireNonNullElseGet(startTime, LocalDateTime::now);
            }
            case DONE -> {
                if (startTime != null) {
                    duration = Duration.between(startTime, LocalDateTime.now());
                } else {
                    duration = Duration.ZERO;
                }
            }
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TypeTasks getTypeTasks() {
        return typeTasks;
    }

    public void setTypeTasks() {
        this.typeTasks = typeTasks;
    }

    public LocalDateTime getEndTime() throws NullPointerException {
        LocalDateTime newDateTime = getStartTime();
        if (newDateTime != null && duration != null) {
            return newDateTime.plus(duration);
        }

        return null;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime()  {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}