package modules;

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

    public void addIdSubTask(int epicId) {
        listSubTaskId.add(epicId);
    }

    public ArrayList<Integer> getListSubTaskId() {
        return listSubTaskId;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "Имя задачи='" + name + '\'' +
                ", описание задачи='" + description + '\'' + ", айдишники SubTask, которые входят в задачу" + listSubTaskId +
                ", id=" + id + ", статус=" + getStatus() + '}';
    }

}
