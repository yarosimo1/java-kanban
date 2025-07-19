package managers;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;

public interface TaskManager {
    void clearTasks();

    void clearEpicTasks();

    void clearSubTasks();

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpicTasks();

    ArrayList<SubTask> getAllSubTasks();

    Task getTaskByID(int idTask);

    Epic getEpicByID(int idTask);

    SubTask getSubTaskByID(int idTask);

    Task createTask(Task task);

    Epic createEpic(Epic task);

    SubTask createSubTask(SubTask task);

    Task updateTask(Task task);

    Epic updateEpic(Epic task);

    SubTask updateSubTask(SubTask task);

    void removeTaskByID(int idTask);

    void removeEpicByID(int idTask);

    void removeSubTaskByID(int idTask);
}
