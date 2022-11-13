package services;

import modules.EpicTask;
import modules.SubTask;
import modules.Task;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс описывающий взаимодействие с различными задачами.
 * Имеет следующую функциональность:
 * - создает задачи
 * - получает задачи
 * - обновляет задачи
 * - удаляет задачи
 * - обновляет статус EpicTask
 */

public class Manager {
    private static int id;

    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    HashMap<Integer, SubTask> subTaskMap = new HashMap<>();

    // Создание задачи
    public void setTask(Task task) {
        id++;
        task.setId(id);
        task.setStatus(Status.NEW);
        taskMap.put(task.getId(), task);
    }

    public void setEpicTask(EpicTask epicTask) {
        id++;
        epicTask.setId(id);
        epicTask.setStatus(Status.NEW);
        epicTaskMap.put(epicTask.getId(), epicTask);
    }

    public void setSubTask(SubTask subTask, EpicTask epicTask) {
        id++;
        subTask.setId(id);
        subTask.setStatus(Status.NEW);
        subTaskMap.put(subTask.getId(), subTask);
        epicTask.addIdSubTask(id);
    }

    // Получение списка всех задач.
    public ArrayList<Task> getListTask() {
        return new ArrayList<>(taskMap.values());
    }

    public ArrayList<EpicTask> getListEpicTask() {
        return new ArrayList<>(epicTaskMap.values());
    }

    public ArrayList<SubTask> getListSubTask() {
        return new ArrayList<>(subTaskMap.values());
    }

    // Удаление всех задач.
    public void clearTask() {
        taskMap.clear();
    }

    public void clearEpicTaskMap() {
        epicTaskMap.clear();
    }

    public void clearSubTaskMap() {
        subTaskMap.clear();
    }

    //Получение по идентификатору.
    public Task getTaskById(int id) {
        return taskMap.get(id);
    }

    public EpicTask getEpicTaskById(int id) {
        return epicTaskMap.get(id);
    }

    public SubTask getSubTaskById(int id) {
        return subTaskMap.get(id);
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    public void updateEpicTaskMap(EpicTask epicTask) {
        epicTaskMap.put(epicTask.getId(), epicTask);
    }

    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
    }

    // Удаление по идентификатору.
    public void removeByIdTask(int id) {
        if (!taskMap.containsKey(id)) {
            return;
        }
        taskMap.remove(id);
    }

    public void removeByIdEpicTas(int id) {
        if (!epicTaskMap.containsKey(id)) {
            return;
        }
        epicTaskMap.remove(id);
    }

    public void removeByIdSubTask(int id) {
        if (!subTaskMap.containsKey(id)) {
            return;
        }
        subTaskMap.remove(id);
    }

    // Получение списка всех подзадач определённого эпика.
    public ArrayList<SubTask> getListSubTask(EpicTask epicTask) {
        ArrayList<SubTask> listSubTask = new ArrayList<>();
        int subId = epicTask.getId();
        for (Integer key : subTaskMap.keySet()) {
            SubTask subTask = subTaskMap.get(key);
            if (subTask.getEpicId() == subId) {
                listSubTask.add(subTask);
            }
        }
        return listSubTask;
    }


    /**
     * если у эпика нет подзадач или все они имеют статус NEW, то статус NEW.
     * если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
     * во всех остальных случаях статус должен быть IN_PROGRESS.
     */

    public void updateStatus(int epicId) {
        boolean isNew = false;
        boolean isInProgress = false;
        boolean isDone = false;
        int countDone = 0;
        ArrayList<Integer> listSubTaskId = epicTaskMap.get(epicId).getListSubTaskId();


        if (listSubTaskId.isEmpty()) {
            isNew = true;
        } else {
            for (int id : epicTaskMap.get(epicId).getListSubTaskId()) {
                if (getSubTaskById(id).getStatus() == Status.NEW) {
                    isNew = true;
                } else if (getSubTaskById(id).getStatus() == Status.IN_PROGRESS) {
                    isInProgress = true;
                    break;
                } else if (getSubTaskById(id).getStatus() == Status.DONE) {
                    isDone = true;
                    countDone++;
                }
            }
        }
        if (isNew && !isInProgress && !isDone) {
            epicTaskMap.get(epicId).setStatus(Status.NEW);
        } else if (isDone && (countDone == epicTaskMap.get(epicId).getListSubTaskId().size())) {
            epicTaskMap.get(epicId).setStatus(Status.DONE);
        } else {
            epicTaskMap.get(epicId).setStatus(Status.IN_PROGRESS);
        }
    }

}

