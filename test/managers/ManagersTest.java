package managers;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagersTest {

    @Test
    void createTask() {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.createTask(task).getID();

        final Task savedTask = taskManager.getTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void createEpic() {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        Epic task = new Epic("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.createTask(task).getID();

        final Task savedTask = taskManager.getTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void createSubTask() {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        SubTask task = new SubTask("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.createTask(task).getID();

        final Task savedTask = taskManager.getTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void managersReturnReadyForWorkManagers() {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        HistoryManager historyManager = Managers.getDefaultHistoryManager();

        assertNotNull(taskManager, "Менеджер задач не возвращается.");
        assertNotNull(historyManager, "Менеджер истории просмотров не возвращается.");
    }

    @Test
    void InMemoryTaskManagerCanFindTasksByID() {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        Epic epicTask = new Epic("Test addNewTask", "Test addNewTask description");
        SubTask subTask = new SubTask("Test addNewTask", "Test addNewTask description");

        final int taskID = taskManager.createTask(task).getID();
        final int epicTaskID = taskManager.createTask(epicTask).getID();
        final int subTaskID = taskManager.createSubTask(subTask).getID();

        LinkedList<Task> tasks = new LinkedList<>();
        tasks.add(taskManager.getTaskByID(taskID));
        tasks.add(taskManager.getEpicByID(epicTaskID));
        tasks.add(taskManager.getSubTaskByID(subTaskID));

        assertEquals(3, tasks.size(), "Неверное количество задач.");
    }

    @Test
    void CehkingEachTaskField() {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);

        assertEquals(task.getID(),
                taskManager.getTaskByID(0).getID(),
                "Поля не совподают");
        assertEquals(task.getTaskName(),
                taskManager.getTaskByID(0).getTaskName(),
                "Поля не совпадают");
        assertEquals(task.getTaskStatus(),
                taskManager.getTaskByID(0).getTaskStatus(),
                "Поля не совпадают");
        assertEquals(task.getDescription(),
                taskManager.getTaskByID(0).getDescription(),
                "Поля не совпадают");
    }

    @Test
    void CehkingTaskBeforeUpdating() {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        HistoryManager historyManager = Managers.getDefaultHistoryManager();

        Task task = new Task("Test addNewTask", "Test addNewTask description");
        historyManager.add(task);

        task = new Task("Task after update", "Test addUpdateTask description");
        taskManager.updateTask(task);
        historyManager.add(task);

        LinkedList<Task> tasks = historyManager.getHistory();

        assertEquals(tasks.get(0), tasks.get(1), "Поля id не совпадают");
        assertEquals(tasks.get(0).getTaskName(), tasks.get(1).getTaskName(), "Поля taskName совпадают");
        assertEquals(tasks.get(0).getDescription(), tasks.get(1).getDescription(), "Поля description совпадают");
    }
}