package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    protected void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    public void historyShouldBeEmptyInitially() {
        assertTrue(historyManager.getHistory().isEmpty(),
                "История должна быть пустой при создании");
    }

    @Test
    public void shouldAddTaskToHistory() {
        Task task = new Task("Task", "Description",
                LocalDateTime.now(), Duration.ofMinutes(15));
        task.setId(1);

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории должна быть одна задача");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать");
    }

    @Test
    public void shouldNotAddNullTask() {
        historyManager.add(null);

        assertTrue(historyManager.getHistory().isEmpty(),
                "В историю не должна добавляться null-задача");
    }

    @Test
    public void shouldRemoveDuplicatesWhenAddingTaskAgain() {
        Task task = new Task("Task", "Description",
                LocalDateTime.now(), Duration.ofMinutes(15));
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task); // добавляем ту же самую задачу ещё раз

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Дубликаты не должны оставаться в истории");
    }

    @Test
    public void shouldRemoveTaskFromBeginning() {
        Task task1 = new Task("Task1", "Desc1",
                LocalDateTime.now(), Duration.ofMinutes(15));
        task1.setId(1);
        Task task2 = new Task("Task2", "Desc2",
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(15));
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории должна остаться одна задача");
        assertEquals(task2, history.get(0), "Задача 2 должна остаться в истории");
    }

    @Test
    public void shouldRemoveTaskFromMiddle() {
        Task task1 = new Task("Task1", "Desc1",
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(15));
        task1.setId(1);
        Task task2 = new Task("Task2", "Desc2",
                LocalDateTime.now().plusMinutes(30), Duration.ofMinutes(15));
        task2.setId(2);
        Task task3 = new Task("Task3", "Desc3",
                LocalDateTime.now().plusMinutes(45), Duration.ofMinutes(15));
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "В истории должны остаться две задачи");
        assertEquals(List.of(task1, task3), history, "История должна содержать задачи 1 и 3");
    }

    @Test
    public void shouldRemoveTaskFromEnd() {
        Task task1 = new Task("Task1", "Desc1",
                LocalDateTime.now(), Duration.ofMinutes(15));
        task1.setId(1);
        Task task2 = new Task("Task2", "Desc2",
                LocalDateTime.now().plusMinutes(15), Duration.ofMinutes(15));
        task2.setId(2);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "В истории должна остаться одна задача");
        assertEquals(task1, history.get(0), "Задача 1 должна остаться в истории");
    }
}
