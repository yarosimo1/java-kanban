package managers;

import task.Task;

import java.util.LinkedList;
import java.util.List;

public interface HistoryManager {
    List<Task> getHistory();

    void add(Task task);
}
