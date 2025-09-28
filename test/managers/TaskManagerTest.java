package managers;

import exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    protected void setUp() {
        taskManager = createTaskManager();
    }

    private Task makeTask() {
        return new Task("Test Task", "Description",
                LocalDateTime.now(), Duration.ofMinutes(30));
    }

    private Epic makeEpic() {
        return new Epic("Epic Task", "Epic description");
    }

    private Subtask makeSubTask(Epic epic) {
        Subtask subTask = new Subtask("Sub Task", "Sub description",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(45), epic);
        return subTask;

    }
    @Test
    public void shouldCreateAndGetTask() {
        Task task = makeTask();
        taskManager.createTask(task);

        Task saved = taskManager.getTaskByID(task.getId());
        assertNotNull(saved);
        assertEquals(task.getId(), saved.getId());
        assertEquals("Test Task", saved.getTaskName());
    }

    @Test
    public void shouldReturnEmptyListWhenNoTasks() {
        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllEpicTasks().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @Test
    public void shouldReturnNullForUnknownId() {
        NotFoundException taskEx = assertThrows(NotFoundException.class,
                () -> taskManager.getTaskByID(999));
        assertEquals("Task with id=999 not found", taskEx.getMessage());

        NotFoundException epicEx = assertThrows(NotFoundException.class,
                () -> taskManager.getEpicByID(999));
        assertEquals("Epic with id=999 not found", epicEx.getMessage());

        NotFoundException subtaskEx = assertThrows(NotFoundException.class,
                () -> taskManager.getSubTaskByID(999));
        assertEquals("Subtask with id=999 not found", subtaskEx.getMessage());
    }

    @Test
    public void shouldClearTasks() {
        Task task = makeTask();
        taskManager.createTask(task);

        taskManager.clearTasks();
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldClearEpicsAndSubTasks() {
        Epic epic = makeEpic();
        taskManager.createEpic(epic);

        Subtask sub = makeSubTask(epic);
        sub.setEpic(epic);
        taskManager.createSubTask(sub);

        taskManager.clearEpicTasks();

        assertTrue(taskManager.getAllEpicTasks().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @Test
    public void shouldUpdateTask() {
        Task task = makeTask();
        taskManager.createTask(task);

        task.setTaskName("Updated name");
        taskManager.updateTask(task);

        Task updated = taskManager.getTaskByID(task.getId());
        assertEquals("Updated name", updated.getTaskName());
    }

    @Test
    public void shouldRemoveTaskById() {
        Task task = makeTask();
        taskManager.createTask(task);

        Task removed = taskManager.removeTaskByID(task.getId());
        assertNotNull(removed);
        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldRemoveEpicAndItsSubtasks() {
        Epic epic = makeEpic();
        taskManager.createEpic(epic);

        Subtask sub = makeSubTask(epic);
        taskManager.createSubTask(sub);

        Epic removedEpic = taskManager.removeEpicByID(epic.getId());
        assertNotNull(removedEpic);

        assertTrue(taskManager.getAllEpicTasks().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @Test
    public void shouldRemoveSubTaskByIdAndUpdateEpic() {
        Epic epic = taskManager.createEpic(makeEpic());

        Subtask sub = taskManager.createSubTask(makeSubTask(epic));

        Subtask removed = taskManager.removeSubTaskByID(sub.getId());
        assertNotNull(removed);

        assertTrue(taskManager.getAllSubTasks().isEmpty());
        assertTrue(epic.getSubTasks().isEmpty());
    }
}