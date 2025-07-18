package managers;

import task.Task;

import java.util.LinkedList;

public class InMemoryHistoryManager implements HistoryManager {

    private static LinkedList<Task> historyList = new LinkedList<>();

    @Override
    public LinkedList<Task> getHistory() {
        return historyList;
    }

    @Override
    public void addElementInHistiryList(Task task) {
        byte maxsize = 10;

        if (historyList.size() > maxsize) {
            historyList.removeFirst();
        } else {
            historyList.add(task);
        }
    }

}
