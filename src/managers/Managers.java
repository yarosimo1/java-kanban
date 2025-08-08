package managers;

public abstract class Managers {

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefaultTaskManager() {
        return new InMemoryTaskManager();
    }

}
