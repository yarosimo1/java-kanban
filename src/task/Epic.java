package task;

import enums.TaskStatus;
import enums.TypeTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public void updateEpicStatus() {
        if (subTasks.isEmpty()) {
            this.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subTasks.stream()
                .allMatch(sub -> sub.getTaskStatus() == TaskStatus.NEW);
        boolean allDone = subTasks.stream()
                .allMatch(sub -> sub.getTaskStatus() == TaskStatus.DONE);

        if (allDone) {
            this.setTaskStatus(TaskStatus.DONE);
        } else if (allNew) {
            this.setTaskStatus(TaskStatus.NEW);
        } else {
            this.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    public void updateEpicTime() {
        if (subTasks.isEmpty()) {
            this.setDuration(Duration.ZERO);
            this.setStartTime(null);
            this.setEndTime(null);
            return;
        }

        this.setDuration(findTotalDuration());
        this.setStartTime(findMinStart());
        endTime = findMaxEnd();
    }

    private Duration findTotalDuration() {
        return subTasks.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private LocalDateTime findMinStart() {
        return subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private LocalDateTime findMaxEnd() {
        return subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
}