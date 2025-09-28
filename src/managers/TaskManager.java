package managers;

import task.Epic;
import task.Subtask;
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

    ArrayList<Subtask> getAllSubTasks();

    List<Task> getPrioritizedTasks();

    Task getTaskByID(int idTask);

    Epic getEpicByID(int idTask);

    Subtask getSubTaskByID(int idTask);

    Task createTask(Task task);

    Epic createEpic(Epic task);

    Subtask createSubTask(Subtask task);

    Task updateTask(Task task);

    Epic updateEpic(Epic task);

    Subtask updateSubTask(Subtask task);

    Task removeTaskByID(int idTask);

    Epic removeEpicByID(int idTask);

    Subtask removeSubTaskByID(int idTask);

    void removeFromPrioritized(Task task);

}
