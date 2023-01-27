package tests;

import managers.HistoryManager;
import managers.Managers;
import managers.TaskManager;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

/**
 * Класс описывающий реализацию ТЕСТОВ менеджера HistoryManager.
 */

public class HistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;

    protected EpicTask epicTaskCreate() {
        return new EpicTask("Название большой задачи!", "Описание большой задачи", Instant.now(), 0);
    }

    protected SubTask subTaskCreate(EpicTask epicTask) {
        return new SubTask("Название подзадачи", "Описание подзадачи", Instant.now(), 100 ,epicTask.getId());
    }

    protected Task taskCreate() {
        return new Task("Купить хлеб", "1 батон", Instant.now().plusSeconds(110), 0);
    }

    @BeforeEach
    public void beforeEach(){
        taskManager = Managers.getDefault();
        historyManager = taskManager.getHistoryManager();
    }

    @Test
    public void shouldAddTaskInHistory(){
        Task task = taskCreate();
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        EpicTask epicTask = epicTaskCreate();
        taskManager.addEpicTask(epicTask);
        taskManager.getEpicTaskById(epicTask.getId());
        SubTask subTask = subTaskCreate(epicTask);
        taskManager.addSubTask(subTask, epicTask);
        taskManager.getSubTaskById(subTask.getId());

        Assertions.assertEquals(List.of(task, epicTask, subTask), historyManager.getHistory(), "Задачи не совпадают.");
    }

    @Test
    public void shouldWhenHistoryIsEmpty() {
        Task task = taskCreate();
        taskManager.addTask(task);
        EpicTask epicTask = epicTaskCreate();
        taskManager.addEpicTask(epicTask);

        Assertions.assertEquals(0, historyManager.getHistory().size(), "История задач не пустая");
    }

    @Test
    public void shouldWhenTaskRepeatInHistory() {
        Task task = taskCreate();
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        EpicTask epicTask = epicTaskCreate();
        taskManager.addEpicTask(epicTask);
        taskManager.getEpicTaskById(epicTask.getId());
        SubTask subTask = subTaskCreate(epicTask);
        taskManager.addSubTask(subTask, epicTask);
        taskManager.getSubTaskById(subTask.getId());
        taskManager.getTaskById(task.getId());
        taskManager.getEpicTaskById(epicTask.getId());
        taskManager.getSubTaskById(subTask.getId());

        Assertions.assertEquals(3, historyManager.getHistory().size(), "Дублирующиеся задачи не удалены");
    }

    @Test
    public void shouldRemoveTaskInHistoryStart(){
        Task task = taskCreate();
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        EpicTask epicTask = epicTaskCreate();
        taskManager.addEpicTask(epicTask);
        taskManager.getEpicTaskById(epicTask.getId());
        SubTask subTask = subTaskCreate(epicTask);
        taskManager.addSubTask(subTask, epicTask);
        taskManager.getSubTaskById(subTask.getId());

        historyManager.remove(task.getId());

        Assertions.assertFalse(historyManager.getHistory().contains(task), "Задача не удалена из начала истории.");
    }

    @Test
    public void shouldRemoveTaskInHistoryEnd(){
        Task task = taskCreate();
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        EpicTask epicTask = epicTaskCreate();
        taskManager.addEpicTask(epicTask);
        taskManager.getEpicTaskById(epicTask.getId());
        SubTask subTask = subTaskCreate(epicTask);
        taskManager.addSubTask(subTask, epicTask);
        taskManager.getSubTaskById(subTask.getId());

        historyManager.remove(epicTask.getId());

        Assertions.assertFalse(historyManager.getHistory().contains(epicTask), "Задача не удалена из конца истории.");
    }

    @Test
    public void shouldRemoveTaskInHistoryMiddle(){
        Task task = taskCreate();
        taskManager.addTask(task);
        taskManager.getTaskById(task.getId());
        EpicTask epicTask = epicTaskCreate();
        taskManager.addEpicTask(epicTask);
        taskManager.getEpicTaskById(epicTask.getId());
        SubTask subTask = subTaskCreate(epicTask);
        taskManager.addSubTask(subTask, epicTask);
        taskManager.getSubTaskById(subTask.getId());

        historyManager.remove(subTask.getId());

        Assertions.assertFalse(historyManager.getHistory().contains(subTask), "Задача не удалена из середины истории.");
    }


}
