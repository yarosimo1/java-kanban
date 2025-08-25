package task;

import enums.TaskStatus;
import enums.TypeTasks;

import java.util.Objects;

public class Task {
    private int id;
    private TypeTasks typeTasks;
    private String taskName;
    private TaskStatus taskStatus;
    private String description;

    public Task(String taskName, String description) {
        this.taskName = taskName;
        this.taskStatus = TaskStatus.NEW;
        this.description = description;
        this.typeTasks = TypeTasks.TASK;
    }

    public Task(String taskName, String description, TypeTasks typeTasks) {
        this.taskName = taskName;
        this.taskStatus = TaskStatus.NEW;
        this.description = description;
        this.typeTasks = typeTasks;
    }

    @Override
    public String toString() {
        return "Task{" + "id=" + id + ", typeTasks=" + typeTasks + ", taskName='" + taskName + '\'' + ", taskStatus=" + taskStatus + ", description='" + description + '\'' + '}';
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
        this.taskStatus = taskStatus;
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
}