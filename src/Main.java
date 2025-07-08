import manager.TaskManager;
import task.*;


public class Main {

    public static void main(String[] args) {
        
        TaskManager taskManager = new TaskManager();

        //task.Task задачи
        Task task = new Task("Task", "new Task");
        Task task1 = new Task("Task1", "new Task1");
        taskManager.createTask(task);
        taskManager.createTask(task1);

        //task.Epic задачи
        Epic epic = new Epic("Epic", "new Epic");
        Epic epic1 = new Epic("Epic1", "new Epic1");
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);

        //task.SubTask задачи для epic
        SubTask subTaskForEpic = new SubTask("SubTaskForEpic", "new SubTaskForEpic");
        SubTask subTaskForEpic1 = new SubTask("SubTaskForEpic", "new SubTaskForEpic");
        taskManager.createSubTask(subTaskForEpic);
        taskManager.createSubTask(subTaskForEpic1);

        //task.SubTask задачи для epic1
        SubTask subTaskForEpic2 = new SubTask("SubTaskForEpic1", "new SubTaskForEpic1");
        taskManager.createSubTask(subTaskForEpic2);

        //Добавляем в epic подзадачи
        epic.addSubTask(subTaskForEpic);
        epic.addSubTask(subTaskForEpic1);

        //Добавляем в epic1 подзадачи
        epic1.addSubTask(subTaskForEpic2);

        System.out.println("----- Созданные задачи -----");
        System.out.println(taskManager.getTaskByID(0));
        System.out.println(taskManager.getTaskByID(1));
        System.out.println(taskManager.getEpicByID(2));
        System.out.println(taskManager.getEpicByID(3));
        System.out.println(taskManager.getSubTaskByID(4));
        System.out.println(taskManager.getSubTaskByID(5));
        System.out.println(taskManager.getSubTaskByID(6));

        //Изменяем статусы задач task, task1
        task.setTaskStatus(TaskStatus.IN_PROGRESS);
        task1.setTaskStatus(TaskStatus.DONE);
        taskManager.updateTask(task);
        taskManager.updateTask(task1);

        //Изменяем статусы подзадач subTaskForEpic, subTaskForEpic1, subTaskForEpic2
        subTaskForEpic.setTaskStatus(TaskStatus.NEW);
        subTaskForEpic1.setTaskStatus(TaskStatus.IN_PROGRESS);
        subTaskForEpic2.setTaskStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTaskForEpic);
        taskManager.updateSubTask(subTaskForEpic1);
        taskManager.updateSubTask(subTaskForEpic2);

        System.out.println();
        System.out.println("----- Задачи после изменения -----");
        System.out.println(taskManager.getTaskByID(0));
        System.out.println(taskManager.getTaskByID(1));
        System.out.println(taskManager.getEpicByID(2));
        System.out.println(taskManager.getEpicByID(3));
        System.out.println(taskManager.getSubTaskByID(4));
        System.out.println(taskManager.getSubTaskByID(5));
        System.out.println(taskManager.getSubTaskByID(6));

        //Удаляем task
        taskManager.removeTaskByID(0);

        //Удаляем subTaskForEpic2
        taskManager.removeSubTaskByID(5);

        //Удаляем epic
        taskManager.removeEpicByID(3);

        System.out.println();
        System.out.println("----- Задачи после удаления -----");
        System.out.println(taskManager.getTaskByID(0));
        System.out.println(taskManager.getTaskByID(1));
        System.out.println(taskManager.getEpicByID(2));
        System.out.println(taskManager.getEpicByID(3));
        System.out.println(taskManager.getSubTaskByID(4));
        System.out.println(taskManager.getSubTaskByID(5));
        System.out.println(taskManager.getSubTaskByID(6));
    }
}
