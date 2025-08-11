package managers;

public abstract class Managers {
    private static final HistoryManager HISTORY_MANAGER = new InMemoryHistoryManager();

    public static HistoryManager getDefaultHistoryManager() {
        return HISTORY_MANAGER;
    }

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

}
