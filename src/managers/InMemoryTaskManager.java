package managers;

import enums.TaskStatus;
import task.Epic;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epicTasks = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                            Comparator.nullsLast(Comparator.naturalOrder()))
                    .thenComparing(Task::getId)
    );
    private int idTask = 0;

    // Очистка
    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpicTasks() {
        epicTasks.clear();
    }

    @Override
    public void clearSubTasks() {
        subTasks.values().stream()
                .map(SubTask::getEpic)
                .filter(Objects::nonNull)
                .forEach(epic -> {
                    epic.clearSubTasks();
                    updateEpicStatus(epic);
                    updateEpicTime(epic);
                });
        subTasks.clear();
    }

    // Получение списков
    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    // Получение по ID
    @Override
    public Task getTaskByID(int idTask) {
        historyManager.add(tasks.get(idTask));
        return tasks.get(idTask);
    }

    @Override
    public Epic getEpicByID(int idTask) {
        historyManager.add(epicTasks.get(idTask));
        return epicTasks.get(idTask);
    }

    @Override
    public SubTask getSubTaskByID(int idTask) {
        historyManager.add(subTasks.get(idTask));
        return subTasks.get(idTask);
    }

    // Создание
    @Override
    public Task createTask(Task task) {
        task.setId(++idTask);
        tasks.put(task.getId(), task);
        addToPrioritized(task);
        return task;
    }

    @Override
    public Epic createEpic(Epic task) {
        task.setId(++idTask);
        epicTasks.put(task.getId(), task);
        addToPrioritized(task);
        return task;
    }

    @Override
    public SubTask createSubTask(SubTask task) {
        task.setId(++idTask);
        subTasks.put(task.getId(), task);

        Optional.ofNullable(task.getEpic())
                .ifPresent(epic -> {
                    epic.addSubTask(task);
                    updateEpicStatus(epic);
                    updateEpicTime(epic);
                });

        addToPrioritized(task);
        return task;
    }

    // Обновление
    @Override
    public Task updateTask(Task task) {
        removeFromPrioritized(task);
        tasks.put(task.getId(), task);
        addToPrioritized(task);
        return task;
    }

    @Override
    public Epic updateEpic(Epic task) {
        removeFromPrioritized(task);
        epicTasks.put(task.getId(), task);
        addToPrioritized(task);
        return task;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        removeFromPrioritized(task);
        subTasks.put(task.getId(), task);
        addToPrioritized(task);

        Optional.ofNullable(task.getEpic())
                .ifPresent(epic -> {
                    updateEpicStatus(epic);
                    updateEpicTime(epic);
                });

        return task;
    }

    // Удаление
    @Override
    public Task removeTaskByID(int idTask) {
        Task task = tasks.remove(idTask);
        Optional.ofNullable(task).ifPresent(this::removeFromPrioritized);
        historyManager.remove(idTask);
        return task;
    }

    @Override
    public Epic removeEpicByID(int idTask) {
        Epic epic = epicTasks.remove(idTask);

        if (epic != null) {
            epic.getSubTasks().forEach(sub -> {
                subTasks.remove(sub.getId());
                historyManager.remove(sub.getId());
            });
            epic.clearSubTasks();
            removeFromPrioritized(epic);
            historyManager.remove(idTask);
        }
        return epic;
    }

    @Override
    public SubTask removeSubTaskByID(int idTask) {
        SubTask sub = subTasks.remove(idTask);

        if (sub != null) {
            Optional.ofNullable(sub.getEpic())
                    .ifPresent(epic -> {
                        epic.removeSubTask(sub);
                        updateEpicStatus(epic);
                        updateEpicTime(epic);
                    });
            removeFromPrioritized(sub);
        }
        historyManager.remove(idTask);
        return sub;
    }

    // Вспомогательные методы
    public List<SubTask> getEpicSubTasksByID(Epic task) {
        return Optional.ofNullable(epicTasks.get(task.getId()))
                .map(Epic::getSubTasks)
                .orElse(Collections.emptyList());
    }

    private void updateEpicStatus(Epic epic) {
        if (epic == null) return;

        List<SubTask> subs = epic.getSubTasks();

        if (subs.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = subs.stream()
                .allMatch(sub -> sub.getTaskStatus() == TaskStatus.NEW);
        boolean allDone = subs.stream()
                .allMatch(sub -> sub.getTaskStatus() == TaskStatus.DONE);

        if (allDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    private void updateEpicTime(Epic epic) {
        if (epic == null) return;

        List<SubTask> subs = epic.getSubTasks();

        if (subs.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        Duration totalDuration = subs.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        LocalDateTime minStart = subs.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime maxEnd = subs.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setDuration(totalDuration);
        epic.setStartTime(minStart);
        epic.setEndTime(maxEnd);
    }

    // Приоритет
    private void addToPrioritized(Task task) {
        Optional.ofNullable(task.getStartTime()).ifPresent(t -> prioritizedTasks.add(task));
    }

    private void removeFromPrioritized(Task task) {
        Optional.ofNullable(task.getStartTime()).ifPresent(t -> prioritizedTasks.remove(task));
    }

    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().collect(Collectors.toList());
    }
}
