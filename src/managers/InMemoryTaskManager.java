package managers;

import task.Epic;
import task.SubTask;
import task.Task;

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

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpicTasks() {
        epicTasks.values().stream().forEach(epic -> {
            epic.getSubTasks().forEach(sub -> {
                subTasks.remove(sub.getId());
            });

            epic.clearSubTasks();
        });

        epicTasks.clear();
    }

    @Override
    public void clearSubTasks() {
        subTasks.values().stream()
                .map(SubTask::getEpic)
                .filter(Objects::nonNull)
                .forEach(epic -> {
                    epic.clearSubTasks();
                    epic.updateEpicStatus();
                    epic.updateEpicTime();
                });
        subTasks.clear();
    }

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
                    epic.updateEpicStatus();
                    epic.updateEpicTime();
                });

        addToPrioritized(task);
        return task;
    }

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
                    epic.updateEpicStatus();
                    epic.updateEpicTime();
                });

        return task;
    }

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
                        epic.updateEpicStatus();
                        epic.updateEpicTime();
                    });
            removeFromPrioritized(sub);
        }
        historyManager.remove(idTask);
        return sub;
    }

    public List<SubTask> getEpicSubTasksByID(Epic task) {
        return Optional.ofNullable(epicTasks.get(task.getId()))
                .map(Epic::getSubTasks)
                .orElse(Collections.emptyList());
    }

    @Override
    public void addToPrioritized(Task task) {
        Optional.ofNullable(task.getStartTime()).ifPresent(t -> prioritizedTasks.add(task));
    }

    @Override
    public void removeFromPrioritized(Task task) {
        Optional.ofNullable(task.getStartTime()).ifPresent(t -> prioritizedTasks.remove(task));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().collect(Collectors.toList());
    }
}
