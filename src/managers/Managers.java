package managers;

public class Managers {

    private Managers() {}

   public static  HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
   }

   public static  TaskManager  getDefault() {
        return new InMemoryTaskManager();
   }

}
