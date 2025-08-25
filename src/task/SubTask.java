package task;

import enums.TypeTasks;

public class SubTask extends Task {
    private Epic epic;
    private int epicId;

    public SubTask(String taskName, String description) {
        super(taskName, description, TypeTasks.SUBTASK);
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}

