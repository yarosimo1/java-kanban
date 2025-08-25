package managers;

import org.junit.jupiter.api.Test;
import task.Epic;
import task.SubTask;
import task.Task;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class FileBackedTaskManagerTest {
    File file = File.createTempFile("tasks", ".csv");
    FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

    FileBackedTaskManagerTest() throws IOException {
    }

    @Test
    void LoadTasksFromEmptyFile() throws IOException {
        File file = File.createTempFile("tasks", ".csv");
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        assertNull(fileBackedTaskManager);
        System.out.println("Временный файл: " + file.getAbsolutePath());
    }

    @Test
    void SaveAndloadTasksFromFile() throws IOException {
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

        fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(fileBackedTaskManager);
        assertNotNull(fileBackedTaskManager.createTask(task));
        assertNotNull(fileBackedTaskManager.createTask(task1));
        assertNotNull(fileBackedTaskManager.createEpic(epic));
        assertNotNull(fileBackedTaskManager.createEpic(epic1));
        assertNotNull(fileBackedTaskManager.createSubTask(subTaskForEpic));
        assertNotNull(fileBackedTaskManager.createSubTask(subTaskForEpic1));
        assertNotNull(fileBackedTaskManager.createSubTask(subTaskForEpic2));
        System.out.println("Временный файл: " + file.getAbsolutePath());
    }

    @Test
    void loadTasksFromFile() {
        assertNotNull(fileBackedTaskManager);
        assertNotNull(fileBackedTaskManager.getAllTasks());
        assertNotNull(fileBackedTaskManager.getAllEpicTasks());
        assertNotNull(fileBackedTaskManager.getAllSubTasks());
    }
}