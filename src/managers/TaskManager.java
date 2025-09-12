package managers;

import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    void addToPrioritized(Task task);

    void clearTasks();

    void clearEpicTasks();

    void clearSubTasks();

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpicTasks();

    ArrayList<SubTask> getAllSubTasks();

    List<Task> getPrioritizedTasks();

    Task getTaskByID(int idTask);

    Epic getEpicByID(int idTask);

    SubTask getSubTaskByID(int idTask);

    Task createTask(Task task);

    Epic createEpic(Epic task);

    SubTask createSubTask(SubTask task);

    Task updateTask(Task task);

    Epic updateEpic(Epic task);

    SubTask updateSubTask(SubTask task);

    Task removeTaskByID(int idTask);

    Epic removeEpicByID(int idTask);

    SubTask removeSubTaskByID(int idTask);

    void removeFromPrioritized(Task task);

}
