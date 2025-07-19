package managers;

import task.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {
    private static final byte MAXSIZE = 10;
    private static LinkedList<Task> historyList = new LinkedList<>();

    @Override
    public LinkedList<Task> getHistory() {
        return historyList;
    }

    @Override
    public void add(Task task) {
        if (historyList.size() > MAXSIZE) {
            historyList.removeFirst();
        } else {
            historyList.add(task);
        }
    }

}
