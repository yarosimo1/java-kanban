package managers;

import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    private int idTask = 0;

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
        Epic epic;
        for (SubTask subTask : subTasks.values()) {
            if (subTask != null) {
                epic = subTask.getEpic();

                if (epic != null) {
                    epic.clearSubTasks();
                    updateEpicStatus(epic);
                }
            }
        }
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

    public SubTask getSubTaskByID(int idTask) {
        historyManager.add(subTasks.get(idTask));
        return subTasks.get(idTask);
    }

    @Override
    public Task createTask(Task task) {
        task.setID(idTask++);
        tasks.put(task.getID(), task);
        return tasks.get(task.getID());
    }

    @Override
    public Epic createEpic(Epic task) {
        task.setID(idTask++);
        epicTasks.put(task.getID(), task);
        return epicTasks.get(task.getID());
    }

    @Override
    public SubTask createSubTask(SubTask task) {
        task.setID(idTask++);
        subTasks.put(task.getID(), task);

        Epic epic = task.getEpic();
        if (epic != null) {
            epic.addSubTask(task);
            updateEpicStatus(epic);
        }
        return subTasks.get(task.getID());
    }

    @Override
    public Task updateTask(Task task) {
        tasks.put(task.getID(), task);
        return tasks.get(task.getID());
    }

    @Override
    public Epic updateEpic(Epic task) {
        epicTasks.put(task.getID(), task);
        return epicTasks.get(task.getID());
    }

    public void updateEpicStatus(Epic epic) {
        if (epic == null) return;

        List<SubTask> subs = epic.getSubTasks();
        if (subs.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (SubTask subTask : subs) {
            TaskStatus status = subTask.getTaskStatus();
            if (status != TaskStatus.NEW) {
                allNew = false;
            }
            if (status != TaskStatus.DONE) {
                allDone = false;
            }
        }

        if (allDone) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (allNew) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        subTasks.put(task.getID(), task);
        Epic epic = task.getEpic();
        if (epic != null) {
            updateEpicStatus(epic);
        }
        return subTasks.get(task.getID());
    }

    @Override
    public void removeTaskByID(int idTask) {
        tasks.remove(idTask);
    }

    @Override
    public void removeEpicByID(int idTask) {
        Epic epic = epicTasks.get(idTask);
        if (epic != null) {
            for (SubTask sub : new ArrayList<>(epic.getSubTasks())) {
                subTasks.remove(sub.getID());
            }
            epic.clearSubTasks();
            epicTasks.remove(idTask);
        }
    }

    @Override
    public void removeSubTaskByID(int idTask) {
        SubTask sub = subTasks.get(idTask);
        if (sub != null) {
            Epic epic = sub.getEpic();
            if (epic != null) {
                epic.removeSubTask(sub);
                updateEpicStatus(epic);
            }
            subTasks.remove(idTask);
        }
    }

    public List<SubTask> getEpicSubTasksByID(Epic task) {
        Epic epic = epicTasks.get(task.getID());

        return epic.getSubTasks();
    }
}
