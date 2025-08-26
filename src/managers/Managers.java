package managers;

import java.io.File;
import java.io.IOException;

public abstract class Managers {
    private static final HistoryManager HISTORY_MANAGER = new InMemoryHistoryManager();

    public static HistoryManager getDefaultHistoryManager() {
        return HISTORY_MANAGER;
    }

    public static TaskManager getDefaultTaskManager(File file) {
        return new FileBackedTaskManager(file);
    }

}
