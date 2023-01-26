package managers;

import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import services.Status;

import java.time.Instant;
import java.util.*;

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
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    private final Comparator<Task> comparator = (o1, o2) -> {
        if ( o1.equals(o2))   {
            return 0;
        } else if (o1.getStartTime() == null){
            return 1;
        } else if (o2.getStartTime() == null) {
            return -1;
        }     else return (int) (o1.getStartTime().toEpochMilli() - o2.getStartTime().toEpochMilli());
    };
    protected Set<Task> prioritizedTasks = new TreeSet<>(comparator);
    //protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));


    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    protected HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    protected HashMap<Integer, EpicTask> getEpicTaskMap() {
        return epicTaskMap;
    }

    protected HashMap<Integer, SubTask> getSubTaskMap() {
        return subTaskMap;
    }

    @Override
    public void addTask(Task task) {
        id++;
        task.setId(id);
        taskMap.put(task.getId(), task);
        addTaskToPrioritizedTasks(task);
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        id++;
        epicTask.setId(id);
        epicTaskMap.put(epicTask.getId(), epicTask);
    }

    @Override
    public void addSubTask(SubTask subTask, EpicTask epicTask) {
        id++;
        subTask.setId(id);
        subTaskMap.put(subTask.getId(), subTask);
        addTaskToPrioritizedTasks(subTask);
        epicTask.addIdSubTask(id);
        updateEpicTime(epicTask);
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
        historyManager.add(taskMap.get(id));
        return Optional.ofNullable(taskMap.get(id)).orElseThrow(() -> new NullPointerException("ID не найден"));
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        historyManager.add(epicTaskMap.get(id));
        return Optional.ofNullable(epicTaskMap.get(id)).orElseThrow(() -> new NullPointerException("ID не найден"));
    }

    @Override
    public SubTask getSubTaskById(int id) {
        historyManager.add(subTaskMap.get(id));
        return Optional.ofNullable(subTaskMap.get(id)).orElseThrow(() -> new NullPointerException("ID не найден"));
    }


    @Override
    public void updateTask(Task task) {

        taskMap.put(task.getId(), task);
        prioritizedTasks.remove(task);
        addTaskToPrioritizedTasks(task);
    }

    @Override
    public void updateEpicTaskMap(EpicTask epicTask) {
        epicTaskMap.put(epicTask.getId(), epicTask);
        updateEpicTime(epicTask);
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask subTask1 =subTaskMap.get(subTask.getId());
        subTaskMap.put(subTask.getId(), subTask);
        updateStatus(subTask.getEpicId());
        prioritizedTasks.remove(subTask1);
        addTaskToPrioritizedTasks(subTask);

        updateEpicTime(getEpicTaskById(subTask.getEpicId()));
    }

    //        Set<Task> prioritizedTasksTemp = new TreeSet<>(comparator.reversed());
//
//        for (Task task : prioritizedTasks) {
//            if (subTask.getId() != task.getId()){
//               prioritizedTasksTemp.add(task);
//            }
//        }
//        prioritizedTasks = prioritizedTasksTemp;
    @Override
    public void removeByIdTask(int id) {
        if (!taskMap.containsKey(id)) {
            return;
        }
        prioritizedTasks.remove(getTaskById(id));
        taskMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeByIdEpicTask(int id) {
        if (!epicTaskMap.containsKey(id)) {
            return;
        }
        for (Integer val : epicTaskMap.get(id).getListSubTaskId()) {
            removeByIdSubTask(val);
        }
        prioritizedTasks.remove(getEpicTaskById(id));
        epicTaskMap.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void removeByIdSubTask(int id) {
        if (!subTaskMap.containsKey(id)) {
            return;
        }
        prioritizedTasks.remove(getSubTaskById(id));
        updateEpicTime(getEpicTaskById(getSubTaskById(id).getEpicId()));
        subTaskMap.remove(id);
        historyManager.remove(id);
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

    public void updateEpicTime(EpicTask epicTask){
        List<SubTask> subTaskList = getListSubTask(epicTask);
        if (!subTaskList.isEmpty()) {

            Instant startTime = subTaskList.get(0).getStartTime();
            if (startTime != null){
            Instant endTime = subTaskList.get(0).getEndTime();

            for (SubTask subTask : subTaskList) {
                if (subTask.getStartTime() != null){

                if (subTask.getStartTime().isBefore(startTime)) {
                    startTime = subTask.getStartTime();
                }
                if (subTask.getEndTime().isAfter(endTime)) {
                    endTime = subTask.getEndTime();
                }
                }
            }

            long duration = (endTime.toEpochMilli() - startTime.toEpochMilli())/1000;
            epicTask.setDuration(duration);
            epicTask.setEndTime(endTime);
            epicTask.setStartTime(startTime);
        }
        }
    }

    private void addTaskToPrioritizedTasks(Task task){
        if (validateTasks(task)) {
                prioritizedTasks.add(task);
            } else throw new ManagerSaveException("Задача пересекается с другой задачей");
    }

    public List<Task> getPrioritizedTasks(){
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean validateTasks(Task task){
        List<Task> list = getPrioritizedTasks();
        for (int i = 1; i < list.size(); i++){
            Task taskSave = list.get(i);

            if (task.getStartTime() != null && taskSave.getStartTime() != null){
                if (task.getEndTime().isBefore(taskSave.getStartTime())
                        || task.getStartTime().isAfter(taskSave.getStartTime())){
                   return true;
                } else return false;
            } else return true;
        }
        return true;
    }
}
