package manager;

import task.*;
import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epicTasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int idTask = 0;

    public void deleteAllTasks() {
        tasks.clear();
        epicTasks.clear();
        subTasks.clear();
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getAllEpicTasks() {
        return new ArrayList<>(epicTasks.values());
    }

    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    public Task getTaskByID(int idTask) {
        return tasks.get(idTask);
    }

    public Epic getEpicByID(int idTask) {
        return epicTasks.get(idTask);
    }

    public SubTask getSubTaskByID(int idTask) {
        return subTasks.get(idTask);
    }

    public void createTask(Task task) {
        task.setID(idTask++);
        tasks.put(task.getID(), task);
    }

    public void createEpic(Epic task) {
        task.setID(idTask++);
        epicTasks.put(task.getID(), task);

    }

    public void createSubTask(SubTask task) {
        task.setID(idTask++);
        subTasks.put(task.getID(), task);

        Epic epic = task.getEpic();
        if (epic != null) {
            epic.addSubTask(task);
            updateEpicStatus(epic);
        }
    }

    public void updateTask(Task task) {
        tasks.put(task.getID(), task);
    }

    public void updateEpic(Epic task) {
        epicTasks.put(task.getID(), task);
    }

    public void updateEpicStatus(Epic epic) {
        if (epic == null) return;

        ArrayList<SubTask> subs = epic.getSubTasks();
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

    public void updateSubTask(SubTask task) {
        subTasks.put(task.getID(), task);
        Epic epic = task.getEpic();
        if (epic != null) {
            updateEpicStatus(epic);
        }
    }

    public void removeTaskByID(int idTask) {
        tasks.remove(idTask);
    }

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

    public ArrayList<SubTask> getEpicSubTasksByID(Epic task)  {
        Epic epic = epicTasks.get(task.getID());

        return epic.getSubTasks();
    }



}
