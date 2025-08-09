import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;

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
        SubTask subTaskForEpic2 = new SubTask("SubTaskForEpic", "new SubTaskForEpic");
        manager.createSubTask(subTaskForEpic);
        manager.createSubTask(subTaskForEpic1);
        manager.createSubTask(subTaskForEpic2);

        //task.Task задачи
        Task task = new Task("newTask", "new Task");
        Task task1 = new Task("newTask1", "new Task1");
        manager.createTask(task);
        manager.createTask(task1);

        //Добавляем в epic подзадачи
        epic.addSubTask(subTaskForEpic);
        epic.addSubTask(subTaskForEpic1);
        epic.addSubTask(subTaskForEpic2);

        System.out.println("----- Просмотр задач -----");
        System.out.println(manager.getEpicByID(0));
        System.out.println(manager.getTaskByID(5));
        System.out.println(manager.getEpicByID(1));
        System.out.println(manager.getSubTaskByID(2));
        System.out.println(manager.getTaskByID(6));
        System.out.println(manager.getSubTaskByID(3));
        System.out.println(manager.getSubTaskByID(4));

        System.out.println();
        System.out.println("----- История просмотров -----");
        System.out.println(historyManager.getHistory());

        System.out.println();
        System.out.println("----- Просмотр задач в другом порядке -----");
        System.out.println(manager.getSubTaskByID(2));
        System.out.println(manager.getEpicByID(1));
        System.out.println(manager.getTaskByID(5));
        System.out.println(manager.getEpicByID(0));
        System.out.println(manager.getSubTaskByID(3));
        System.out.println(manager.getTaskByID(6));
        System.out.println(manager.getSubTaskByID(4));

        System.out.println();
        System.out.println("----- История просмотров в другом порядке-----");
        System.out.println(historyManager.getHistory());

        manager.removeEpicByID(0);

        System.out.println();
        System.out.println("----- История просмотров после удаления эпика -----");
        System.out.println(historyManager.getHistory());
        System.out.println(historyManager.getHistory().size());
    }
}
