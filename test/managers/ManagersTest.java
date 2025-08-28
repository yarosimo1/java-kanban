package managers;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    private File file;
    private TaskManager taskManager;
    private HistoryManager historyManager;

    public ManagersTest() throws IOException {
        this.file = File.createTempFile("tasks", ".csv");
        this.taskManager = Managers.getDefaultTaskManager(file);
        this.historyManager = Managers.getDefaultHistoryManager();

    }

    @Test
    public void createTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.createTask(task).getId();

        final Task savedTask = taskManager.getTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
        taskManager.removeTaskByID(taskId);
    }

    @Test
    public void createEpic() {
        Epic task = new Epic("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.createTask(task).getId();

        final Task savedTask = taskManager.getTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
        taskManager.removeEpicByID(taskId);
    }

    @Test
    public void createSubTask() {
        SubTask task = new SubTask("Test addNewTask", "Test addNewTask description");
        final int taskId = taskManager.createTask(task).getId();

        final Task savedTask = taskManager.getTaskByID(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
        taskManager.removeSubTaskByID(taskId);
    }

    @Test
    void managersReturnReadyForWorkManagers() {

        assertNotNull(taskManager, "Менеджер задач не возвращается.");
        assertNotNull(historyManager, "Менеджер истории просмотров не возвращается.");
    }

    @Test
    public void InMemoryTaskManagerCanFindTasksByID() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        Epic epicTask = new Epic("Test addNewTask", "Test addNewTask description");
        SubTask subTask = new SubTask("Test addNewTask", "Test addNewTask description");

        final int taskID = taskManager.createTask(task).getId();
        final int epicTaskID = taskManager.createTask(epicTask).getId();
        final int subTaskID = taskManager.createSubTask(subTask).getId();

        LinkedList<Task> tasks = new LinkedList<>();
        tasks.add(taskManager.getTaskByID(taskID));
        tasks.add(taskManager.getEpicByID(epicTaskID));
        tasks.add(taskManager.getSubTaskByID(subTaskID));

        assertEquals(3, tasks.size(), "Неверное количество задач.");
        taskManager.removeTaskByID(taskID);
        taskManager.removeEpicByID(epicTaskID);
        taskManager.removeSubTaskByID(subTaskID);

    }

    @Test
    public void cehkingEachTaskField() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);

        assertEquals(task.getId(), taskManager.getTaskByID(task.getId()).getId(), "Поля не совподают");
        assertEquals(task.getTaskName(), taskManager.getTaskByID(task.getId()).getTaskName(), "Поля не совпадают");
        assertEquals(task.getTaskStatus(), taskManager.getTaskByID(task.getId()).getTaskStatus(), "Поля не совпадают");
        assertEquals(task.getDescription(), taskManager.getTaskByID(task.getId()).getDescription(), "Поля не совпадают");

        taskManager.removeTaskByID(task.getId());
    }

    @Test
    void cehkingTaskBeforeUpdating() {

        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.getTaskByID(task.getId());

        task = new Task("Task after update", "Test addUpdateTask description");
        taskManager.updateTask(task);
        taskManager.getTaskByID(task.getId());


        assertEquals(1, historyManager.getHistory().size(), "количество задач изменилось");
        taskManager.removeTaskByID(0);
    }

    @Test
    public void chekListHistoryField() {

        //task.Epic задачи
        Epic epic = new Epic("Epic", "new Epic");
        taskManager.createEpic(epic);

        taskManager.getEpicByID(epic.getId());

        assertNotNull(historyManager.getHistory(), "список истории не заполняется");
        assertEquals(1, historyManager.getHistory().size(), "Неверное количество задач.");

        taskManager.removeEpicByID(epic.getId());
    }

    @Test
    public void DeleteTaskFromhistoryManager() {

        //task.Epic задачи
        Epic epic = new Epic("Epic", "new Epic");
        Epic epic1 = new Epic("Epic1", "new Epic1");

        taskManager.getEpicByID(taskManager.createEpic(epic).getId());
        taskManager.getEpicByID(taskManager.createEpic(epic1).getId());

        assertEquals(2, historyManager.getHistory().size(), "Неверное количество задач.");

        taskManager.removeEpicByID(epic.getId());

        assertEquals(1, historyManager.getHistory().size(), "Неверное количество задач.");

        taskManager.removeEpicByID(epic1.getId());
    }

    @Test
    public void DeleteTaskForChekSizeHistoryList() {

        //task.Epic задачи
        Epic epic = new Epic("Epic", "new Epic");
        Epic epic1 = new Epic("Epic1", "new Epic1");

        //task.SubTask задачи для epic
        SubTask subTaskForEpic = new SubTask("SubTaskForEpic", "new SubTaskForEpic");
        SubTask subTaskForEpic1 = new SubTask("SubTaskForEpic", "new SubTaskForEpic");

        //Добавляем в epic подзадачи
        epic.addSubTask(subTaskForEpic);
        epic.addSubTask(subTaskForEpic1);

        taskManager.getEpicByID(taskManager.createEpic(epic).getId());
        taskManager.getEpicByID(taskManager.createEpic(epic1).getId());
        taskManager.getSubTaskByID(taskManager.createSubTask(subTaskForEpic).getId());
        taskManager.getSubTaskByID(taskManager.createSubTask(subTaskForEpic1).getId());

        assertEquals(4, historyManager.getHistory().size(), "Неверное количество задач.");

        taskManager.removeEpicByID(epic.getId());

        assertEquals(1, historyManager.getHistory().size(), "Неверное количество задач.");

        taskManager.removeEpicByID(epic1.getId());
    }
}