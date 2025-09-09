package managers;


import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Test
    void shouldLinkSubTaskWithEpic() {
        InMemoryTaskManager manager = createTaskManager();

        Epic epic = new Epic("Epic", "Epic description");
        manager.createEpic(epic);

        SubTask subTask = new SubTask("SubTask", "SubTask description",
                LocalDateTime.now(), Duration.ofMinutes(15));
        subTask.setEpic(epic);
        manager.createSubTask(subTask);

        Epic savedEpic = manager.getEpicByID(epic.getId());
        assertNotNull(savedEpic, "Эпик должен существовать");
        assertEquals(1, savedEpic.getSubTasks().size(), "У эпика должен быть один сабтаск");
        assertEquals(subTask, savedEpic.getSubTasks().get(0), "Сабтаск должен совпадать");
    }

    @Test
    void shouldUpdateTaskCorrectly() {
        InMemoryTaskManager manager = createTaskManager();

        Task task = new Task("Task", "Description",
                LocalDateTime.now(), Duration.ofMinutes(15));
        manager.createTask(task);

        task.setDescription("Updated description");
        manager.updateTask(task);

        Task updated = manager.getTaskByID(task.getId());
        assertEquals("Updated description", updated.getDescription(), "Описание должно обновиться");
    }
}
