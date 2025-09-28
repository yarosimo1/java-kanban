package task;

import enums.TaskStatus;
import enums.TypeTasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
    private final Map<Integer, Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String taskName, String description) {
        super(taskName, description, TypeTasks.EPIC,null, null);
        subtasks = new HashMap<>();
    }

    public List<Subtask> getSubTasks() {
        return new ArrayList<>(subtasks.values());
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void addSubTask(Subtask subTask) {
        subtasks.put(subTask.getId(), subTask);
        subTask.setEpic(this);
        subTask.setEpicId(this.getId());
    }

    public void removeSubTask(Subtask subTask) {
        subtasks.remove(subTask);
    }

    public void clearSubTasks() {
        subtasks.clear();
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void updateEpicStatus() {
        if (subtasks.isEmpty()) {
            this.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subtasks.values().stream()
                .allMatch(sub -> sub.getTaskStatus() == TaskStatus.NEW);
        boolean allDone = subtasks.values().stream()
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
        if (subtasks.isEmpty()) {
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
        return subtasks.values().stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private LocalDateTime findMinStart() {
        return subtasks.values().stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private LocalDateTime findMaxEnd() {
        return subtasks.values().stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }
}