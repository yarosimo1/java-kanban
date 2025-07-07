public class SubTask extends Task {
    private Epic epic;

    public SubTask(String taskName, String description) {
        super(taskName, description);
    }


    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}

