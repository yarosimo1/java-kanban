package managers;

import task.*;
import java.util.ArrayList;
import java.util.HashMap;

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
        historyManager.addElementInHistiryList(tasks.get(idTask));
        return tasks.get(idTask);
    }

    @Override
    public Epic getEpicByID(int idTask) {
        historyManager.addElementInHistiryList(epicTasks.get(idTask));
        return epicTasks.get(idTask);
    }

    public SubTask getSubTaskByID(int idTask) {
        historyManager.addElementInHistiryList(subTasks.get(idTask));
        return subTasks.get(idTask);
    }

    @Override
    public void createTask(Task task) {
        task.setID(idTask++);
        tasks.put(task.getID(), task);
    }

    @Override
    public void createEpic(Epic task) {
        task.setID(idTask++);
        epicTasks.put(task.getID(), task);

    }

    @Override
    public void createSubTask(SubTask task) {
        task.setID(idTask++);
        subTasks.put(task.getID(), task);

        Epic epic = task.getEpic();
        if (epic != null) {
            epic.addSubTask(task);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getID(), task);
    }

    @Override
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

    @Override
    public void updateSubTask(SubTask task) {
        subTasks.put(task.getID(), task);
        Epic epic = task.getEpic();
        if (epic != null) {
            updateEpicStatus(epic);
        }
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

    public ArrayList<SubTask> getEpicSubTasksByID(Epic task)  {
        Epic epic = epicTasks.get(task.getID());

        return epic.getSubTasks();
    }



}
