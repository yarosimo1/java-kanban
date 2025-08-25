package managers;

import Exceptions.ManagerSaveException;
import enums.TaskStatus;
import enums.TypeTasks;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.*;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private boolean isLoading = false;

    public FileBackedTaskManager(File file) {
        this.file = file;
        initFile();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.isLoading = true;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();

            if (header == null || header.isEmpty()) {
                System.out.println("Файл пуст!");
                return null;
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.createEpic((Epic) task);
                } else if (task instanceof SubTask) {
                    SubTask subTask = manager.createSubTask((SubTask) task);
                    Epic epic = manager.getEpicByID(subTask.getEpicId());
                    epic.addSubTask(subTask);
                    manager.updateEpic(epic);
                } else {
                    manager.createTask(task);
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла: " + file.getName(), e);
        } finally {
            manager.isLoading = false; // не забываем вернуть обратно
        }

        return manager;
    }

    private static Task fromString(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Строка пуста или null");
        }

        String[] parts = value.split(",");

        int id = Integer.parseInt(parts[0].trim());
        TypeTasks typeTasks = TypeTasks.valueOf(parts[1].trim());
        String taskName = parts[2].trim();
        TaskStatus taskStatus = TaskStatus.valueOf(parts[3].trim());
        String description = parts[4].trim();
        int epicId = -1;

        if (parts.length > 5 && !parts[5].trim().isEmpty()) {
            epicId = Integer.parseInt(parts[5].trim());
        }

        switch (typeTasks) {
            case EPIC -> {
                Epic epic = new Epic(taskName, description);
                epic.setId(id);
                epic.setTaskStatus(taskStatus);
                return epic;
            }
            case TASK -> {
                Task task = new Task(taskName, description);
                task.setId(id);
                task.setTaskStatus(taskStatus);
                return task;
            }
            case SUBTASK -> {
                SubTask subTask = new SubTask(taskName, description);
                subTask.setId(id);
                subTask.setEpicId(epicId);
                subTask.setTaskStatus(taskStatus);
                return subTask;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + typeTasks);
        }
    }

    public static void main(String[] args) {
        File file1 = new File("task.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file1);

        Task task = new Task("Task", "new Task");
        Task task1 = new Task("Task1", "new Task1");

        Epic epic = new Epic("Epic", "new Epic");
        Epic epic1 = new Epic("Epic1", "new Epic1");

        SubTask subTaskForEpic = new SubTask("SubTaskForEpic1", "new SubTaskForEpic1");
        SubTask subTaskForEpic1 = new SubTask("SubTaskForEpic2", "new SubTaskForEpic2");
        SubTask subTaskForEpic2 = new SubTask("SubTaskForEpic3", "new SubTaskForEpic3");

        epic.addSubTask(subTaskForEpic);
        epic.addSubTask(subTaskForEpic1);
        epic1.addSubTask(subTaskForEpic2);

        fileBackedTaskManager.createTask(task);
        fileBackedTaskManager.createTask(task1);
        fileBackedTaskManager.createEpic(epic);
        fileBackedTaskManager.createEpic(epic1);
        fileBackedTaskManager.createSubTask(subTaskForEpic);
        fileBackedTaskManager.createSubTask(subTaskForEpic1);
        fileBackedTaskManager.createSubTask(subTaskForEpic2);

        fileBackedTaskManager = loadFromFile(file1);
        FileBackedTaskManager fileBackedTaskManager1 = loadFromFile(file1);

        System.out.println("Вывод из 'fileBackedTaskManager'");
        System.out.println(fileBackedTaskManager.getAllTasks());
        System.out.println();
        System.out.println(fileBackedTaskManager.getAllEpicTasks());
        System.out.println();
        System.out.println(fileBackedTaskManager.getAllSubTasks());
        System.out.println("-----------");
        System.out.println();

        System.out.println("Вывод из 'fileBackedTaskManager1'");
        System.out.println(fileBackedTaskManager1.getAllTasks());
        System.out.println();
        System.out.println(fileBackedTaskManager1.getAllEpicTasks());
        System.out.println();
        System.out.println(fileBackedTaskManager1.getAllSubTasks());
        System.out.println("-----------");
        System.out.println();

    }

    private void initFile() {
        try {
            if (!file.exists()) {
                file.createNewFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("id,type,name,status,description,epic");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании файла: " + file.getName(), e);
        }
    }

    @Override
    public Task createTask(Task task) {
        Task created = super.createTask(task);
        save();
        return created;
    }

    @Override
    public Epic createEpic(Epic task) {
        Epic created = super.createEpic(task);
        save();
        return created;
    }

    @Override
    public SubTask createSubTask(SubTask task) {
        SubTask created = super.createSubTask(task);
        save();
        return created;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public ArrayList<Epic> getAllEpicTasks() {
        return super.getAllEpicTasks();
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public Task updateTask(Task task) {
        Task updeted = super.updateTask(task);
        save();
        return updeted;
    }

    @Override
    public Epic updateEpic(Epic task) {
        Epic updeted = super.updateEpic(task);
        save();
        return updeted;
    }

    @Override
    public SubTask updateSubTask(SubTask task) {
        SubTask updeted = super.updateSubTask(task);
        return updeted;
    }

    @Override
    public void removeTaskByID(int idTask) {
        super.removeTaskByID(idTask);
        save();
    }

    @Override
    public void removeEpicByID(int idTask) {
        super.removeEpicByID(idTask);
        save();
    }

    @Override
    public void removeSubTaskByID(int idTask) {
        super.removeSubTaskByID(idTask);
        save();
    }

    private void save() {
        if (isLoading) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }

            for (Epic epic : getAllEpicTasks()) {
                writer.write(toString(epic));
                writer.newLine();
            }

            for (SubTask subTask : getAllSubTasks()) {
                writer.write(toString(subTask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения данных в файл: " + file, e);
        }
    }

    private String toString(Task task) {
        String type = task.getClass().getSimpleName().toUpperCase();
        String epicId = "";
        if (task instanceof SubTask) {
            epicId = String.valueOf(((SubTask) task).getEpicId());
        }

        return String.join(",", String.valueOf(task.getId()), type, task.getTaskName(), task.getTaskStatus().toString(), task.getDescription(), epicId);
    }
}
