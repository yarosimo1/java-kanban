package task;

import enums.TypeTasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<SubTask> subTasks;
    private LocalDateTime endTime;

    public Epic(String taskName, String description) {
        super(taskName, description, TypeTasks.EPIC,null, null);
        subTasks = new ArrayList<>();
    }

    public List<SubTask> getSubTasks() {
        return subTasks;
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
        subTask.setEpic(this);
        subTask.setEpicId(this.getId());
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    public void clearSubTasks() {
        subTasks.clear();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}