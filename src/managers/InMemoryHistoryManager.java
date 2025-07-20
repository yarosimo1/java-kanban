package managers;

import task.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final byte MAX_SIZE_HISTORY_LIST = 10;
    private static List<Task> historyList = new LinkedList<>();

    @Override
    public List<Task> getHistory() {
        return historyList;
    }

    @Override
    public void add(Task task) {
        if (historyList.size() > MAX_SIZE_HISTORY_LIST) {
            historyList.removeFirst();
        } else {
            historyList.add(task);
        }
    }

}
