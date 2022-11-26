package services;

public class Managers<TaskManager> {
    public TaskManager taskManager;

    public Managers (TaskManager taskManager){
        this.taskManager = taskManager;
    }

    public TaskManager getDefault(){
        return taskManager;
    }
}
