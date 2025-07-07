import java.util.Objects;

public class Task {
    protected String taskName;
    protected String description;
    protected int ID;
    protected TaskStatus taskStatus;

    public Task(String taskName, String description) {
        this.taskName = taskName;
        this.taskStatus = TaskStatus.NEW;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskName='" + taskName + '\'' +
                ", description='" + description + '\'' +
                ", taskStatus='" + taskStatus + '\'' +
                ", ID=" + ID +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return ID == task.ID &&
                Objects.equals(taskName, task.taskName) &&
                Objects.equals(description, task.description) &&
                taskStatus == task.taskStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskName, description, ID, taskStatus);
    }

    public int getID() {
        return ID;
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

    public void setID(int ID) {
        this.ID = ID;
    }
}
