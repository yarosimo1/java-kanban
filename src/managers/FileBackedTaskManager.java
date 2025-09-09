package managers;

import enums.TaskStatus;
import enums.TypeTasks;
import exceptions.ManagerSaveException;
import task.Epic;
import task.SubTask;
import task.Task;
import timeGrid.TimeGrid;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private File file;
    private boolean isLoading = false;
    private final TimeGrid timeGrid = new TimeGrid(LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0));

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

                giveTaskTypeFromFile(manager, fromString(line));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла: " + file.getName(), e);
        } finally {
            manager.isLoading = false;
        }
        return manager;
    }

    private static void giveTaskTypeFromFile(FileBackedTaskManager manager, Task task) {
        TypeTasks typeTasks = task.getTypeTasks();

        switch (typeTasks) {
            case EPIC -> manager.createEpic((Epic) task);
            case SUBTASK -> {
                SubTask subTask = manager.createSubTask((SubTask) task);
                Epic epic = manager.getEpicByID(subTask.getEpicId());
                epic.addSubTask(subTask);
                manager.updateEpic(epic);
            }
            case TASK -> manager.createTask(task);
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + typeTasks);
        }
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

        Duration duration = null;
        if (parts.length > 5 && !parts[5].isEmpty()) {
            duration = Duration.ofMinutes(Long.parseLong(parts[5].trim()));
        }

        LocalDateTime startTime = null;
        if (parts.length > 6 && !parts[6].isEmpty()) {
            startTime = LocalDateTime.parse(parts[6].trim());
        }

        switch (typeTasks) {
            case EPIC -> {
                Epic epic = new Epic(taskName, description);
                epic.setId(id);
                epic.setTaskStatus(taskStatus);
                epic.setDuration(duration);
                return epic;
            }
            case TASK -> {
                Task task = new Task(taskName, description, startTime, duration);
                task.setId(id);
                task.setTaskStatus(taskStatus);
                task.setDuration(duration);
                return task;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[7].trim());
                SubTask subTask = new SubTask(taskName, description, startTime, duration);
                subTask.setId(id);
                subTask.setEpicId(epicId);
                subTask.setTaskStatus(taskStatus);
                subTask.setDuration(duration);
                return subTask;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + typeTasks);
        }
    }

    public static void main(String[] args) {
//        File file1 = new File("task.csv");
//        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file1);
//
//        Task task = new Task("Task", "new Task",
//                LocalDateTime.of(2025, 9, 8, 10, 15));
//        Task task1 = new Task("Task1", "new Task1",
//                LocalDateTime.of(2025, 9, 8, 10, 15));
//
//        Epic epic = new Epic("Epic", "new Epic");
//        Epic epic1 = new Epic("Epic1", "new Epic1");
//
//        SubTask subTaskForEpic = new SubTask("SubTaskForEpic1", "new SubTaskForEpic1",
//                LocalDateTime.of(2025, 9, 8, 10, 45));
//        SubTask subTaskForEpic1 = new SubTask("SubTaskForEpic2", "new SubTaskForEpic2",
//                LocalDateTime.of(2025, 9, 8, 11, 00));
//        SubTask subTaskForEpic2 = new SubTask("SubTaskForEpic3", "new SubTaskForEpic3",
//                LocalDateTime.of(2025, 9, 8, 11, 15));
//
//        subTaskForEpic.setEpic(epic);
//        subTaskForEpic1.setEpic(epic);
//        subTaskForEpic2.setEpic(epic1);
//
//        fileBackedTaskManager.createTask(task);
//        fileBackedTaskManager.createTask(task1);
//        fileBackedTaskManager.createEpic(epic);
//        fileBackedTaskManager.createEpic(epic1);
//        fileBackedTaskManager.createSubTask(subTaskForEpic);
//        fileBackedTaskManager.createSubTask(subTaskForEpic1);
//        fileBackedTaskManager.createSubTask(subTaskForEpic2);
//
//        System.out.println(epic.getSubTasks());
//
////        fileBackedTaskManager = loadFromFile(file1);
////        FileBackedTaskManager fileBackedTaskManager1 = loadFromFile(file1);
//
//        System.out.println("Вывод из 'fileBackedTaskManager'");
//        System.out.println(fileBackedTaskManager.getAllTasks());
//        System.out.println();
//        System.out.println(fileBackedTaskManager.getAllEpicTasks());
//        System.out.println();
//        System.out.println(fileBackedTaskManager.getAllSubTasks());
//        System.out.println("-----------");
//        System.out.println();

//        System.out.println("Вывод из 'fileBackedTaskManager1'");
//        System.out.println(fileBackedTaskManager1.getAllTasks());
//        System.out.println();
//        System.out.println(fileBackedTaskManager1.getAllEpicTasks());
//        System.out.println();
//        System.out.println(fileBackedTaskManager1.getAllSubTasks());
//        System.out.println("-----------");
//        System.out.println();
//
//        System.out.println(fileBackedTaskManager.getPrioritizedTasks());
    }

    private void initFile() {
        try {
            if (!file.exists()) {
                file.createNewFile();

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("id,type,name,status,description,duration,startTime,epic");
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при создании файла: " + file.getName(), e);
        }
    }

    @Override
    public Task createTask(Task task) {
        if (!timeGrid.canSchedule(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой");
        }

        Task created = super.createTask(task);
        timeGrid.schedule(task);
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
        if (!timeGrid.canSchedule(task)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой");
        }

        SubTask created = super.createSubTask(task);
        timeGrid.schedule(task);
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
        save();
        return updeted;
    }

    @Override
    public Task removeTaskByID(int idTask) {
        Task task = super.removeTaskByID(idTask);

        if (task != null) {
            timeGrid.unschedule(task);
        }

        save();
        return task;
    }

    @Override
    public Epic removeEpicByID(int idTask) {
        Epic epic = super.removeEpicByID(idTask);
        save();
        return epic;
    }

    @Override
    public SubTask removeSubTaskByID(int idTask) {
        SubTask subTask = super.removeSubTaskByID(idTask);

        if (subTask != null) {
            timeGrid.unschedule(subTask);
        }

        save();
        return subTask;
    }

    private void save() {
        if (isLoading) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,duration,startTime,epic");
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
        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";
        String startTime = task.getStartTime() != null ? task.getStartTime().toString() : "";

        if (task instanceof SubTask) {
            epicId = String.valueOf(((SubTask) task).getEpicId());
        }
        return String.join(",",
                String.valueOf(task.getId()),
                type,
                task.getTaskName(),
                task.getTaskStatus().toString(),
                task.getDescription(),
                duration,
                startTime,
                epicId);
    }
}