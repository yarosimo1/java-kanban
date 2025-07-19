import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import task.Epic;
import task.SubTask;
import task.Task;
import task.TaskStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefaultTaskManager();
        HistoryManager historyManager = Managers.getDefaultHistoryManager();

        //task.Task задачи
        Task task = new Task("Task", "new Task");
        Task task1 = new Task("Task1", "new Task1");
        manager.createTask(task);
        manager.createTask(task1);

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

        //task.SubTask задачи для epic1
        SubTask subTaskForEpic2 = new SubTask("SubTaskForEpic1", "new SubTaskForEpic1");
        manager.createSubTask(subTaskForEpic2);

        //Добавляем в epic подзадачи
        epic.addSubTask(subTaskForEpic);
        epic.addSubTask(subTaskForEpic1);

        //Добавляем в epic1 подзадачи
        epic1.addSubTask(subTaskForEpic2);

        System.out.println("----- Созданные задачи -----");
        System.out.println(manager.getTaskByID(0));
        System.out.println(manager.getTaskByID(1));
        System.out.println(manager.getEpicByID(2));
        System.out.println(manager.getEpicByID(3));
        System.out.println(manager.getSubTaskByID(4));
        System.out.println(manager.getSubTaskByID(5));
        System.out.println(manager.getSubTaskByID(6));

        //Изменяем статусы задач task, task1
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        task1.setTaskStatus(TaskStatus.DONE);
        manager.updateTask(task);
        manager.updateTask(task1);

        //Изменяем статусы подзадач subTaskForEpic, subTaskForEpic1, subTaskForEpic2
        subTaskForEpic.setTaskStatus(TaskStatus.NEW);
        subTaskForEpic1.setTaskStatus(TaskStatus.IN_PROGRESS);
        subTaskForEpic2.setTaskStatus(TaskStatus.DONE);
        manager.updateSubTask(subTaskForEpic);
        manager.updateSubTask(subTaskForEpic1);
        manager.updateSubTask(subTaskForEpic2);

        System.out.println();
        System.out.println("----- Задачи после изменения -----");
        System.out.println(manager.getTaskByID(0));
        System.out.println(manager.getTaskByID(1));
        System.out.println(manager.getEpicByID(2));
        System.out.println(manager.getEpicByID(3));
        System.out.println(manager.getSubTaskByID(4));
        System.out.println(manager.getSubTaskByID(5));
        System.out.println(manager.getSubTaskByID(6));

        //Удаляем task
        manager.removeTaskByID(0);

        //Удаляем subTaskForEpic2
        manager.removeSubTaskByID(5);

        //Удаляем epic
        manager.removeEpicByID(3);

        System.out.println();
        System.out.println("----- Задачи после удаления -----");
        System.out.println(manager.getTaskByID(0));
        System.out.println(manager.getTaskByID(1));
        System.out.println(manager.getEpicByID(2));
        System.out.println(manager.getEpicByID(3));
        System.out.println(manager.getSubTaskByID(4));
        System.out.println(manager.getSubTaskByID(5));
        System.out.println(manager.getSubTaskByID(6));
    }
}
