package modules;

import services.Status;
import services.TypeTask;

import java.util.ArrayList;

/**
 * Класс описывающий задачу, в которую входят подзадачи (SubTask). Хранит в списке ID подзадач, которые в нее входят
 */

public class EpicTask extends Task {
    ArrayList<Integer> listSubTaskId;

    public EpicTask(String name, String description) {
        super(name, description);
        listSubTaskId = new ArrayList<>();
    }

    public EpicTask(int id, String name, Status status, String description) {
        super(id, name, status, description);
        listSubTaskId = new ArrayList<>();
    }

    public void addIdSubTask(int epicId) {
        listSubTaskId.add(epicId);
    }

    public ArrayList<Integer> getListSubTaskId() {
        return listSubTaskId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,", id, TypeTask.EPICTASK, name, status, description);
//                "EpicTask{" +
//                "Имя задачи='" + name + '\'' +
//                ", описание задачи='" + description + '\'' + ", айдишники SubTask, которые входят в задачу" + listSubTaskId +
//                ", id=" + id + ", статус=" + getStatus() + '}';
    }

}
