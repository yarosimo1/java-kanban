package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<SubTask> subTasks;

    public Epic(String taskName, String description) {
        super(taskName, description);
        subTasks = new ArrayList<>();
    }

    public ArrayList<SubTask> getSubTasks() {
        return (ArrayList<SubTask>) subTasks;
    }

    public void addSubTask(SubTask subTask) {
        subTasks.add(subTask);
        subTask.setEpic(this);
    }

    public void removeSubTask(SubTask subTask) {
        subTasks.remove(subTask);
    }

    public void clearSubTasks() {
        subTasks.clear();
    }

}
