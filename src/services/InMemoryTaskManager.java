package services;

import modules.EpicTask;
import modules.SubTask;
import modules.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

/**
 * Класс описывающий реализацию интерфейса TaskManager.
 * а также хранит в памяти внесенные задачи
 * обновляет статус EpicTask
 */

public class InMemoryTaskManager implements TaskManager {
    private int id;
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();


    @Override
    public void setTask(Task task) {
        id++;
        task.setId(id);
        taskMap.put(task.getId(), task);
    }

    @Override
    public void setEpicTask(EpicTask epicTask) {
        id++;
        epicTask.setId(id);
        epicTaskMap.put(epicTask.getId(), epicTask);
    }

    @Override
    public void setSubTask(SubTask subTask, EpicTask epicTask) {
        id++;
        subTask.setId(id);
        subTaskMap.put(subTask.getId(), subTask);
        epicTask.addIdSubTask(id);
    }


    @Override
    public ArrayList<Task> getListTask() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public ArrayList<EpicTask> getListEpicTask() {
        return new ArrayList<>(epicTaskMap.values());
    }

    @Override
    public ArrayList<SubTask> getListSubTask() {
        return new ArrayList<>(subTaskMap.values());
    }


    @Override
    public void clearTask() {
        taskMap.clear();
    }

    @Override
    public void clearEpicTaskMap() {
        epicTaskMap.clear();
    }

    @Override
    public void clearSubTaskMap() {
        subTaskMap.clear();
    }


    @Override
    public Task getTaskById(int id) {
        inMemoryHistoryManager.add(taskMap.get(id));
        return Optional.ofNullable(taskMap.get(id)).orElseThrow(() -> new NullPointerException("ID не найден"));
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        inMemoryHistoryManager.add(epicTaskMap.get(id));
        return Optional.ofNullable(epicTaskMap.get(id)).orElseThrow(() -> new NullPointerException("ID не найден"));
    }

    @Override
    public SubTask getSubTaskById(int id) {
        inMemoryHistoryManager.add(subTaskMap.get(id));
        return Optional.ofNullable(subTaskMap.get(id)).orElseThrow(() -> new NullPointerException("ID не найден"));
    }


    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);
    }

    @Override
    public void updateEpicTaskMap(EpicTask epicTask) {
        epicTaskMap.put(epicTask.getId(), epicTask);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTaskMap.put(subTask.getId(), subTask);
        updateStatus(subTask.getEpicId());
    }


    @Override
    public void removeByIdTask(int id) {
        if (!taskMap.containsKey(id)) {
            return;
        }
        taskMap.remove(id);
    }

    @Override
    public void removeByIdEpicTask(int id) {
        if (!epicTaskMap.containsKey(id)) {
            return;
        }
        for (Integer val : epicTaskMap.get(id).getListSubTaskId()) {
            removeByIdSubTask(val);
        }
        epicTaskMap.remove(id);
    }

    @Override
    public void removeByIdSubTask(int id) {
        if (!subTaskMap.containsKey(id)) {
            return;
        }
        subTaskMap.remove(id);
    }


    @Override
    public ArrayList<SubTask> getListSubTask(EpicTask epicTask) {
        ArrayList<SubTask> listSubTask = new ArrayList<>();
        for (Integer key : epicTask.getListSubTaskId()) {
            listSubTask.add(subTaskMap.get(key));
        }
        return listSubTask;
    }


    /**
     * если у эпика нет подзадач или все они имеют статус NEW, то статус NEW.
     * если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.
     * во всех остальных случаях статус должен быть IN_PROGRESS.
     */

    private void updateStatus(int epicId) {
        boolean isNew = false;
        boolean isInProgress = false;
        boolean isDone = false;
        int countDone = 0;
        ArrayList<Integer> listSubTaskId;

        try {
            listSubTaskId = epicTaskMap.get(epicId).getListSubTaskId();
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
        } catch (NullPointerException e) {
            System.out.println(("Список задач пуст"));
        }
    }


}
