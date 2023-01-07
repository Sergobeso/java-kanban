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
        this.typeTask = TypeTask.EPICTASK;
    }

    public EpicTask(int id, String name, Status status, String description) {
        super(id, name, status, description);
        listSubTaskId = new ArrayList<>();
        this.typeTask = TypeTask.EPICTASK;
    }

    public EpicTask(String[] data) {
        super(data);
        listSubTaskId = new ArrayList<>();
        this.typeTask = TypeTask.EPICTASK;
    }

    public void addIdSubTask(int epicId) {
        listSubTaskId.add(epicId);
    }

    public ArrayList<Integer> getListSubTaskId() {
        return listSubTaskId;
    }
}
