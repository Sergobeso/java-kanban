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
    }

    public SubTask(int id ,String name, Status status, String description,  int epicId) {
        super(name, description);
        this.epicId = epicId;
        this.id = id;
        this.status = status;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%d", id, TypeTask.SUBTASK, name, status, description, epicId);
//                "SubTask{" +
//                "Имя задачи='" + name + '\'' +
//                ", описание задачи='" + description + '\'' +
//                ", id=" + id + ", статус=" + getStatus() + '}';
    }

}

