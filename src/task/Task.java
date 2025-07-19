package task;

import java.util.Objects;

public class Task {
    private String taskName;
    private String description;
    private int id;
    private TaskStatus taskStatus;

    public Task(String taskName, String description) {
        this.taskName = taskName;
        this.taskStatus = TaskStatus.NEW;
        this.description = description;
    }

    @Override
    public String toString() {
        return "task.Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", id=" + id +
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

    public int getID() {
        return id;
    }

    public void setID(int ID) {
        this.id = ID;
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
        this.taskStatus = taskStatus;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
