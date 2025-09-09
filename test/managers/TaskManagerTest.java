package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;
import enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;

    // Каждая конкретная реализация должна создавать свой TaskManager
    protected abstract T createTaskManager();

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void shouldCreateAndGetTask() {
        Task task = new Task("Task1", "Description1",
                LocalDateTime.now(), Duration.ofMinutes(15));
        taskManager.createTask(task);

        Task saved = taskManager.getTaskByID(task.getId());
        assertNotNull(saved, "Задача должна быть сохранена");
        assertEquals(task.getTaskName(), saved.getTaskName());
    }

    @Test
    void shouldCreateAndGetEpicWithSubtasks() {
        Epic epic = new Epic("Epic1", "EpicDesc");
        taskManager.createEpic(epic);

        SubTask sub = new SubTask("Sub1", "SubDesc",
                LocalDateTime.now(), Duration.ofMinutes(15));
        sub.setEpic(epic);
        taskManager.createSubTask(sub);

        Epic savedEpic = taskManager.getEpicByID(epic.getId());
        List<SubTask> subs = savedEpic.getSubTasks();

        assertEquals(1, subs.size(), "У эпика должна быть 1 подзадача");
        assertEquals(savedEpic.getId(), sub.getEpicId(), "EpicId у сабтаска должен совпадать");
    }

    @Test
    void shouldUpdateTask() {
        Task task = new Task("Task1", "Desc",
                LocalDateTime.now(), Duration.ofMinutes(15));
        taskManager.createTask(task);

        task.setTaskStatus(TaskStatus.DONE);
        taskManager.updateTask(task);

        Task updated = taskManager.getTaskByID(task.getId());
        assertEquals(TaskStatus.DONE, updated.getTaskStatus(), "Статус должен обновиться");
    }

    @Test
    void shouldRemoveTask() {
        Task task = new Task("Task1", "Desc",
                LocalDateTime.now(), Duration.ofMinutes(15));
        taskManager.createTask(task);

        taskManager.removeTaskByID(task.getId());
        assertNull(taskManager.getTaskByID(task.getId()), "Задача должна быть удалена");
    }

    @Test
    void shouldClearAllTasks() {
        taskManager.createTask(new Task("Task1", "Desc",
                LocalDateTime.now(), Duration.ofMinutes(15)));
        taskManager.createTask(new Task("Task2", "Desc",
                LocalDateTime.now().plusMinutes(35), Duration.ofMinutes(15)));

        taskManager.clearTasks();
        assertTrue(taskManager.getAllTasks().isEmpty(), "Все задачи должны удаляться");
    }

    @Test
    void epicStatusShouldBeNewIfAllSubtasksAreNew() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Test Epic"));
        SubTask sub1 = new SubTask("Sub1", "desc",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(15));
        sub1.setEpic(epic);

        SubTask sub2 = new SubTask("Sub2", "desc",
                LocalDateTime.now().plusHours(2), Duration.ofMinutes(15));
        sub2.setEpic(epic);

        taskManager.createSubTask(sub1);
        taskManager.createSubTask(sub2);

        Epic savedEpic = taskManager.getEpicByID(epic.getId());
        assertEquals(TaskStatus.NEW, savedEpic.getTaskStatus(),
                "Если все подзадачи NEW, то эпик должен быть NEW");
    }

    @Test
    void epicStatusShouldBeDoneIfAllSubtasksAreDone() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Test Epic"));
        SubTask sub1 = new SubTask("Sub1", "desc",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(15));
        sub1.setEpic(epic);

        SubTask sub2 = new SubTask("Sub2", "desc",
                LocalDateTime.now().plusHours(2), Duration.ofMinutes(15));
        sub2.setEpic(epic);

        taskManager.createSubTask(sub1);
        taskManager.createSubTask(sub2);

        sub1.setTaskStatus(TaskStatus.DONE);
        sub2.setTaskStatus(TaskStatus.DONE);

        taskManager.updateSubTask(sub1);
        taskManager.updateSubTask(sub2);

        Epic savedEpic = taskManager.getEpicByID(epic.getId());
        assertEquals(TaskStatus.DONE, savedEpic.getTaskStatus(),
                "Если все подзадачи DONE, то эпик должен быть DONE");
    }

    @Test
    void epicStatusShouldBeInProgressIfSubtasksAreNewAndDone() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Test Epic"));
        SubTask sub1 = new SubTask("Sub1", "desc",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(15));
        sub1.setEpic(epic);

        SubTask sub2 = new SubTask("Sub2", "desc",
                LocalDateTime.now().plusHours(2), Duration.ofMinutes(15));
        sub2.setTaskStatus(TaskStatus.DONE);
        sub2.setEpic(epic);

        taskManager.createSubTask(sub1);
        taskManager.createSubTask(sub2);

        Epic savedEpic = taskManager.getEpicByID(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getTaskStatus(),
                "Если подзадачи NEW и DONE, то эпик должен быть IN_PROGRESS");
    }

    @Test
    void epicStatusShouldBeInProgressIfAnySubtaskInProgress() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Test Epic"));
        SubTask sub1 = new SubTask("Sub1", "desc",
                LocalDateTime.now().plusHours(1), Duration.ofMinutes(15));
        sub1.setTaskStatus(TaskStatus.IN_PROGRESS);
        sub1.setEpic(epic);

        SubTask sub2 = new SubTask("Sub2", "desc",
                LocalDateTime.now().plusHours(2), Duration.ofMinutes(15));
        sub2.setEpic(epic);

        taskManager.createSubTask(sub1);
        taskManager.createSubTask(sub2);

        Epic savedEpic = taskManager.getEpicByID(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getTaskStatus(),
                "Если есть хотя бы одна IN_PROGRESS, то эпик должен быть IN_PROGRESS");
    }
}