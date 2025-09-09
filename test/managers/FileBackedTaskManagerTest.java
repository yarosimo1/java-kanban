package managers;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File tempFile;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            tempFile = File.createTempFile("tasks", ".csv");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать временный файл для теста", e);
        }
        return new FileBackedTaskManager(tempFile);
    }

    @Test
    void shouldReturnNullWhenLoadingFromEmptyFile() throws IOException {
        File emptyFile = File.createTempFile("tasks", ".csv");

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(emptyFile);

        assertNull(loaded, "При загрузке из пустого файла менеджер должен быть null");
    }

    @Test
    void shouldSaveAndLoadTasksFromFile() throws IOException {
        FileBackedTaskManager manager = createTaskManager();

        Task task = new Task("Task", "new Task",
                LocalDateTime.now(), Duration.ofMinutes(15));
        Task task1 = new Task("Task1", "new Task1",
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofMinutes(15));

        Epic epic = new Epic("Epic", "new Epic");
        Epic epic1 = new Epic("Epic1", "new Epic1");

        SubTask subTaskForEpic = new SubTask("SubTaskForEpic1", "new SubTaskForEpic1",
                LocalDateTime.of(2025, 1, 1, 11, 0), Duration.ofMinutes(15));
        SubTask subTaskForEpic1 = new SubTask("SubTaskForEpic2", "new SubTaskForEpic2",
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofMinutes(15));
        SubTask subTaskForEpic2 = new SubTask("SubTaskForEpic3", "new SubTaskForEpic3",
                LocalDateTime.of(2025, 1, 1, 13, 0), Duration.ofMinutes(15));

        epic.addSubTask(subTaskForEpic);
        epic.addSubTask(subTaskForEpic1);
        epic1.addSubTask(subTaskForEpic2);

        manager.createTask(task);
        manager.createTask(task1);
        manager.createEpic(epic);
        manager.createEpic(epic1);
        manager.createSubTask(subTaskForEpic);
        manager.createSubTask(subTaskForEpic1);
        manager.createSubTask(subTaskForEpic2);

        // загружаем из файла
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertNotNull(loaded, "После сохранения и загрузки менеджер должен быть создан");
        assertEquals(2, loaded.getAllTasks().size(), "Должны загрузиться все обычные задачи");
        assertEquals(2, loaded.getAllEpicTasks().size(), "Должны загрузиться все эпики");
        assertEquals(3, loaded.getAllSubTasks().size(), "Должны загрузиться все сабтаски");
    }

    @Test
    void shouldLoadEmptyCollectionsFromFile() {
        FileBackedTaskManager manager = createTaskManager();

        assertNotNull(manager.getAllTasks(), "Список задач не должен быть null");
        assertNotNull(manager.getAllEpicTasks(), "Список эпиков не должен быть null");
        assertNotNull(manager.getAllSubTasks(), "Список сабтасков не должен быть null");
    }
    @Test
    public void testSubTaskScheduling_NoOverlap() {
        SubTask newSub = new SubTask("NewSub", "Desc",
                LocalDateTime.of(2025, 9, 10, 14, 0), Duration.ofMinutes(15));

        assertDoesNotThrow(() -> {
            taskManager.createSubTask(newSub);
        });
    }

    @Test
    public void testSubTaskScheduling_Overlap() {
        SubTask overlapping = new SubTask("OverlapSub", "Desc",
                LocalDateTime.of(2025, 9, 10, 11, 0), Duration.ofMinutes(15));
        SubTask overlapping1 = new SubTask("OverlapSub1", "Desc",
                LocalDateTime.of(2025, 9, 10, 11, 0), Duration.ofMinutes(15));

        // первая задача должна создаться нормально
        taskManager.createSubTask(overlapping);

        // вторая должна пересечься и выбросить исключение
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createSubTask(overlapping1);
        });

        assertEquals("Подзадача пересекается по времени с другой", ex.getMessage());
    }

    @Test
    public void testTaskScheduling_NoOverlap() {
        SubTask newSub = new SubTask("NewTask", "Desc",
                LocalDateTime.of(2025, 9, 10, 14, 0), Duration.ofMinutes(15));

        assertDoesNotThrow(() -> {
            taskManager.createSubTask(newSub);
        });
    }

    @Test
    public void testTaskScheduling_Overlap() {
        Task overlapping = new SubTask("OverlapTask", "Desc",
                LocalDateTime.of(2025, 9, 10, 11, 0), Duration.ofMinutes(15));
        Task overlapping1 = new SubTask("OverlapTask", "Desc",
                LocalDateTime.of(2025, 9, 10, 11, 0), Duration.ofMinutes(15));

        // первая задача должна создаться нормально
        taskManager.createTask(overlapping);

        // вторая должна пересечься и выбросить исключение
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.createTask(overlapping1);
        });

        assertEquals("Задача пересекается по времени с другой", ex.getMessage());
    }
}
