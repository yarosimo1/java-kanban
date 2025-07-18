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

    void createTask(Task task);

    void createEpic(Epic task);

    void createSubTask(SubTask task);

    void updateTask(Task task);

    void updateEpic(Epic task);

    void updateSubTask(SubTask task);

    void removeTaskByID(int idTask);

    void removeEpicByID(int idTask);

    void removeSubTaskByID(int idTask);
}
