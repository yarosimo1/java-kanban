package Epic;

import enums.TaskStatus;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {

    protected TaskManager taskManager = new InMemoryTaskManager();

    @Test
    public void epicStatusShouldBeNewIfAllSubtasksAreNew() {
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
    public void epicStatusShouldBeDoneIfAllSubtasksAreDone() {
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
    public void epicStatusShouldBeInProgressIfSubtasksAreNewAndDone() {
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
    public void epicStatusShouldBeInProgressIfAnySubtaskInProgress() {
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
