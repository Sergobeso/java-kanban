package modules;

import services.Status;
import services.TypeTask;

/**
 * Класс описывающий подзадачи, которые входят в главную задачу (EpicTask). Идентификация главной задачи
 * проходит по полю epicID.
 */

public class SubTask extends Task {

    private final int epicId;

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.typeTask = TypeTask.SUBTASK;
    }

    public SubTask(int id, String name, Status status, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.id = id;
        this.status = status;
        this.typeTask = TypeTask.SUBTASK;
    }

    public SubTask(String[] data) {
        super(data);
        this.epicId = Integer.parseInt(data[5]);
        this.typeTask = TypeTask.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format(super.toString() + "%d", epicId);
    }
}