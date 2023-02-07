package managers;

import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import services.Status;

import java.security.KeyStore;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

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
        }     else return o1.getStartTime().compareTo(o2.getStartTime());
    };
    protected Set<Task> prioritizedTasks = new TreeSet<>(comparator);

//    второй вариант сортировки, компаратор в одну строку
//    private final Comparator<Task> comparator = Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).thenComparing(Task :: getId);
//    protected final Set<Task> prioritizedTasks = new TreeSet<>(comparator);


    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    public HashMap<Integer, Task> getTaskMap() {
        return taskMap;
    }

    public HashMap<Integer, EpicTask> getEpicTaskMap() {
        return epicTaskMap;
    }

    public HashMap<Integer, SubTask> getSubTaskMap() {
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
    public ArrayList<Task> getListAllTask(){
        ArrayList<Task> allTasks = new ArrayList<>();
        allTasks.addAll(getListTask());
        allTasks.addAll(getListEpicTask());
        allTasks.addAll(getListSubTask());
        return allTasks;
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
        Task task1 =taskMap.get(task.getId());
        taskMap.put(task.getId(), task);
        prioritizedTasks.remove(task1);
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
    public ArrayList<SubTask> getListSubEpicTask(EpicTask epicTask) {
        ArrayList<SubTask> listSubTask = new ArrayList<>();
        for (Integer key : epicTask.getListSubTaskId()) {
            listSubTask.add(subTaskMap.get(key));
        }
        return listSubTask;
    }

    public List<SubTask> getAllSubTaskEpicId(int id){
        List<SubTask> list = new ArrayList<>();
        if (epicTaskMap.containsKey(id)){
            EpicTask epicTask = epicTaskMap.get(id);
            for (int item: epicTask.getListSubTaskId()) {
                if (subTaskMap.containsKey(item)){
                    list.add(subTaskMap.get(item));
                }
            }
        }
        return list;
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

        List<SubTask> subTaskList = getAllSubTaskEpicId(epicTask.getId());

        if (!subTaskList.isEmpty()) {
            Instant startTime = subTaskList.get(0).getStartTime();
            if (startTime != null){
                long tempDuration = 0;
                for (SubTask subTask : subTaskList) {
                    if (subTask.getStartTime() != null){
                        tempDuration += subTask.getDuration();
                        if (subTask.getStartTime().isBefore(startTime)) {
                            startTime = subTask.getStartTime();
                        }
                    }
                }
                epicTask.setDuration(tempDuration);
                epicTask.setEndTime(startTime.plusSeconds(tempDuration));
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
        boolean isNotIntersection = true;
        for (Task taskItem : list) {
            if (!task.equals(taskItem)) {
                if (task.getStartTime() != null && taskItem.getEndTime() != null) {
                    if (task.getEndTime().isBefore(taskItem.getStartTime())
                            && task.getEndTime().isBefore(taskItem.getStartTime())) {
                        isNotIntersection = true;
                    } else if (task.getStartTime().isAfter(taskItem.getEndTime())
                            && task.getEndTime().isAfter(taskItem.getEndTime())) {
                        isNotIntersection = true;
                    } else {
                        isNotIntersection = false;
                        break;
                    }
                }
            }
        }   return isNotIntersection;
    }
}
