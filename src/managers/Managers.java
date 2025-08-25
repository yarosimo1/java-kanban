package managers;

public abstract class Managers {
    private static final HistoryManager HISTORY_MANAGER = new InMemoryHistoryManager();
    private static final TaskManager TASK_MANAGER = new InMemoryTaskManager();

    public static HistoryManager getDefaultHistoryManager() {
        return HISTORY_MANAGER;
    }

    public static TaskManager getDefaultTaskManager() {
        return TASK_MANAGER;
    }

}
