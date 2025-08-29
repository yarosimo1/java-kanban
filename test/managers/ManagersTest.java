package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
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
    private static File file;
    private static TaskManager taskManager;
    private static HistoryManager historyManager;

    private Task task;
    private Epic epic;
    private Epic epic1;
    private SubTask subTaskForEpic;
    private  SubTask subTaskForEpic1;

    @BeforeAll
    public static void satrterCreate() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        taskManager = Managers.getDefaultTaskManager(file);
        historyManager = Managers.getDefaultHistoryManager();
    }

    @BeforeEach
    public void create() {
        task = new Task("Test addNewTask", "Test addNewTask description");
        epic = new Epic("Epic", "new Epic");
        epic1 = new Epic("Epic1", "new Epic1");
        subTaskForEpic = new SubTask("SubTaskForEpic", "new SubTaskForEpic");
        subTaskForEpic1 = new SubTask("SubTaskForEpic", "new SubTaskForEpic");
        taskManager.createTask(task);
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);
        taskManager.createSubTask(subTaskForEpic);
        taskManager.createSubTask(subTaskForEpic1);
    }


    @AfterEach
    public void clearLists() {
        taskManager.removeTaskByID(task.getId());
        taskManager.removeEpicByID(epic.getId());
        taskManager.removeEpicByID(epic1.getId());
        taskManager.removeSubTaskByID(subTaskForEpic.getId());
        taskManager.removeSubTaskByID(subTaskForEpic1.getId());
    }

    @Test
    public void createTask() {
        final Task savedTask = taskManager.getTaskByID(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
        taskManager.removeTaskByID(task.getId());
        taskManager.removeSubTaskByID(savedTask.getId());
    }

    @Test
    public void createEpic() {
        final Epic savedTask = taskManager.getEpicByID(epic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epic, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
        taskManager.removeEpicByID(epic.getId());
        taskManager.removeEpicByID(savedTask.getId());
    }

    @Test
    public void createSubTask() {
        final SubTask savedTask = taskManager.getSubTaskByID(subTaskForEpic.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subTaskForEpic, savedTask, "Задачи не совпадают.");

        final ArrayList<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
        taskManager.removeSubTaskByID(savedTask.getId());
    }

    @Test
    void managersReturnReadyForWorkManagers() {
        assertNotNull(taskManager, "Менеджер задач не возвращается.");
        assertNotNull(historyManager, "Менеджер истории просмотров не возвращается.");
    }

    @Test
    public void InMemoryTaskManagerCanFindTasksByID() {
        final int taskID = task.getId();
        final int epicTaskID = epic.getId();
        final int subTaskID = subTaskForEpic.getId();

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
        assertEquals(task.getId(), taskManager.getTaskByID(task.getId()).getId(), "Поля не совподают");
        assertEquals(task.getTaskName(), taskManager.getTaskByID(task.getId()).getTaskName(), "Поля не совпадают");
        assertEquals(task.getTaskStatus(), taskManager.getTaskByID(task.getId()).getTaskStatus(), "Поля не совпадают");
        assertEquals(task.getDescription(), taskManager.getTaskByID(task.getId()).getDescription(), "Поля не совпадают");

        taskManager.removeTaskByID(task.getId());
    }

    @Test
    void cehkingTaskBeforeUpdating() {
        taskManager.getTaskByID(task.getId());

        task.setDescription("new Description");

        taskManager.updateTask(task);
        taskManager.getTaskByID(task.getId());

        assertEquals(1, historyManager.getHistory().size(), "количество задач изменилось");
        taskManager.removeTaskByID(task.getId());
    }

    @Test
    public void chekListHistoryField() {
        taskManager.getEpicByID(epic.getId());

        assertNotNull(historyManager.getHistory(), "список истории не заполняется");
        assertEquals(1, historyManager.getHistory().size(), "Неверное количество задач.");

        taskManager.removeEpicByID(epic.getId());
    }

    @Test
    public void DeleteTaskFromhistoryManager() {
        taskManager.getEpicByID(epic.getId());
        taskManager.getEpicByID(epic1.getId());

        assertEquals(2, historyManager.getHistory().size(), "Неверное количество задач.");
        taskManager.removeEpicByID(epic.getId());

        assertEquals(1, historyManager.getHistory().size(), "Неверное количество задач.");
        taskManager.removeEpicByID(epic1.getId());
    }

    @Test
    public void DeleteTaskForChekSizeHistoryList() {
        //Добавляем в epic подзадачи
        epic.addSubTask(subTaskForEpic);
        epic.addSubTask(subTaskForEpic1);

        taskManager.getEpicByID(epic.getId());
        taskManager.getEpicByID(epic1.getId());
        taskManager.getSubTaskByID(subTaskForEpic.getId());
        taskManager.getSubTaskByID(subTaskForEpic1.getId());

        assertEquals(4, historyManager.getHistory().size(), "Неверное количество задач.");
        taskManager.removeEpicByID(epic.getId());

        assertEquals(1, historyManager.getHistory().size(), "Неверное количество задач.");
        taskManager.removeEpicByID(epic1.getId());
    }
}