import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import task.Epic;
import task.SubTask;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaultTaskManager();
        HistoryManager historyManager = Managers.getDefaultHistoryManager();

        //task.Epic задачи
        Epic epic = new Epic("Epic", "new Epic");
        Epic epic1 = new Epic("Epic1", "new Epic1");
        manager.createEpic(epic);
        manager.createEpic(epic1);

        //task.SubTask задачи для epic
        SubTask subTaskForEpic = new SubTask("SubTaskForEpic", "new SubTaskForEpic");
        SubTask subTaskForEpic1 = new SubTask("SubTaskForEpic", "new SubTaskForEpic");
        manager.createSubTask(subTaskForEpic);
        manager.createSubTask(subTaskForEpic1);

        //Добавляем в epic подзадачи
        epic.addSubTask(subTaskForEpic);
        epic.addSubTask(subTaskForEpic1);

        System.out.println("----- Созданные задачи -----");
        System.out.println(manager.getEpicByID(0));
        System.out.println(manager.getEpicByID(1));
        System.out.println(manager.getSubTaskByID(2));
        System.out.println(manager.getSubTaskByID(3));

        System.out.println();
        System.out.println(historyManager.getHistory());

        manager.removeEpicByID(0);

        System.out.println();
        System.out.println(historyManager.getHistory());
        System.out.println(historyManager.getHistory().size());
    }
}
