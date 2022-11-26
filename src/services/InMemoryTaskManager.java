package services;

import modules.EpicTask;
import modules.SubTask;
import modules.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Класс описывающий взаимодействие с различными задачами.
 * Имеет следующую функциональность:
 * - создает задачи
 * - получает задачи
 * - обновляет задачи
 * - удаляет задачи
 * - обновляет статус EpicTask
 */

public class InMemoryTaskManager<Tasks extends Task> implements TaskManager  {
    private int id;
    private static final int MAX_LENGTH_IN_HISTORY = 10;
    private final HashMap<Integer, Task> taskMap = new HashMap<>();
    private final HashMap<Integer, EpicTask> epicTaskMap = new HashMap<>();
    private final HashMap<Integer, SubTask> subTaskMap = new HashMap<>();
    public List<Tasks> historyIdList = new ArrayList<>();

    // Создание задачи
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

    // Получение списка всех задач.
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

    // Удаление всех задач.
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

    //Получение по идентификатору.
    @Override
    public Task getTaskById(int id) {
        addIdHistory((Tasks) taskMap.get(id));
        return Optional.ofNullable(taskMap.get(id)).orElseThrow(() -> new NullPointerException("ID не найден"));
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        addIdHistory((Tasks) epicTaskMap.get(id));
        return Optional.ofNullable(epicTaskMap.get(id)).orElseThrow(() -> new NullPointerException("ID не найден"));
    }

    @Override
    public SubTask getSubTaskById(int id) {
        addIdHistory((Tasks) subTaskMap.get(id));
        return Optional.ofNullable(subTaskMap.get(id)).orElseThrow(() -> new NullPointerException("ID не найден"));
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
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

    // Удаление по идентификатору.
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

    // Получение списка всех подзадач определённого эпика.
    @Override
    public ArrayList<SubTask> getListSubTask(EpicTask epicTask) {
        ArrayList<SubTask> listSubTask = new ArrayList<>();
        for (Integer key : epicTask.getListSubTaskId()) {
            listSubTask.add(subTaskMap.get(key));
        }
        return listSubTask;
    }

    @Override
    public List<Tasks> getHistory() {
        return historyIdList;
    }

    // метод проверяет список на величину списка и добавляет в историю просмотров
    private void addIdHistory(Tasks t) {
        if (historyIdList.size() == MAX_LENGTH_IN_HISTORY) {
            historyIdList.remove(0);
            historyIdList.add(t);
        } else historyIdList.add(t);
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
