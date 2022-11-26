package services;

import modules.EpicTask;
import modules.SubTask;
import modules.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager<Tasks extends Task > {

    void setTask(Task task);
    void setEpicTask(EpicTask epicTask);
    void setSubTask(SubTask subTask, EpicTask epicTask);

    ArrayList<Task> getListTask();
    ArrayList<EpicTask> getListEpicTask();
    ArrayList<SubTask> getListSubTask();

    void clearTask();
    void clearEpicTaskMap();
    void clearSubTaskMap();

    Task getTaskById(int id);
    EpicTask getEpicTaskById(int id);
    SubTask getSubTaskById(int id);

    void updateTask(Task task);
    void updateEpicTaskMap(EpicTask epicTask);
    void updateSubTask(SubTask subTask);

    void removeByIdTask(int id);
    void removeByIdEpicTask(int id);
    void removeByIdSubTask(int id);

    ArrayList<SubTask> getListSubTask(EpicTask epicTask);

   List<Tasks> getHistory();
}
