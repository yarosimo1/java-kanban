package managers;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    @Test
    public void shouldReturnDefaultTaskManager() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        TaskManager manager = Managers.getDefaultTaskManager(file);

        assertNotNull(manager, "Менеджер задач не должен быть null");
    }

    @Test
    public void shouldReturnDefaultHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistoryManager();

        assertNotNull(historyManager, "Менеджер истории не должен быть null");
    }
}