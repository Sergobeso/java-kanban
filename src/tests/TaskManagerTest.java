package tests;

import managers.TaskManager;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import services.Status;

import java.time.Instant;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected EpicTask epicTaskCreate() {
        return new EpicTask("Название большой задачи!", "Описание большой задачи", Instant.now(), 0);
    }

    protected SubTask subTaskCreate(EpicTask epicTask) {
        return new SubTask("Название подзадачи", "Описание подзадачи", Instant.now(), 200 ,epicTask.getId());
    }

    protected Task taskCreate() {
        return new Task("Название одиночной задачи", "Описание одиночной задачи", Instant.now(), 1000);
    }


    @Test
    public void shouldAddTask() {
        final Task task = taskCreate();
        taskManager.addTask(task);
        final int taskId = task.getId();
        final Task savedTask = taskManager.getTaskById(taskId);

        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> list = taskManager.getListTask();

        Assertions.assertNotNull(list, "Задачи не возвращаются.");
        Assertions.assertEquals(1, list.size(), "Неверное количество задач.");
        Assertions.assertEquals(task, list.get(0), "Задачи не совпадают.");
        Assertions.assertEquals(Status.NEW, task.getStatus(), "Статусы не совпадают.");
    }

    @Test
    public void shouldAddEpicTask() {

        final EpicTask epicTask = epicTaskCreate();
        taskManager.addEpicTask(epicTask);
        final int epicTaskId = epicTask.getId();
        final EpicTask savedTask = taskManager.getEpicTaskById(epicTaskId);

        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(epicTask, savedTask, "Задачи не совпадают.");

        final List<EpicTask> list = taskManager.getListEpicTask();

        Assertions.assertNotNull(list, "Задачи на возвращаются.");
        Assertions.assertEquals(1, list.size(), "Неверное количество задач.");
        Assertions.assertEquals(epicTask, list.get(0), "Задачи не совпадают.");
        Assertions.assertEquals(Status.NEW, epicTask.getStatus(), "Статусы не совпадают.");
    }

    @Test
    public void shouldAddSubTask() {

        final EpicTask epicTask = epicTaskCreate();
        taskManager.addEpicTask(epicTask);
        final SubTask subTask = subTaskCreate(epicTask);
        taskManager.addSubTask(subTask, epicTask);

        final int subTaskId = subTask.getId();
        final SubTask savedTask = taskManager.getSubTaskById(subTaskId);

        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(subTask, savedTask, "Задачи не совпадают.");

        final List<SubTask> list = taskManager.getListSubTask();

        Assertions.assertNotNull(list, "Задачи на возвращаются.");
        Assertions.assertEquals(1, list.size(), "Неверное количество задач.");
        Assertions.assertEquals(subTask, list.get(0), "Задачи не совпадают.");
        Assertions.assertEquals(Status.NEW, epicTask.getStatus(), "Статусы не совпадают.");
        Assertions.assertEquals(epicTask.getId(), subTask.getEpicId(), "ID EPIC не совпадают.");
        Assertions.assertEquals(subTaskId, epicTask.getListSubTaskId().get(0), "ID subTask не совпадают.");
    }

    @Test
    public void shouldStatusEpic() {
        final EpicTask epicTask = epicTaskCreate();

        taskManager.addEpicTask(epicTask);

        // Пустой список подзадач.
        Assertions.assertEquals(Status.NEW, epicTask.getStatus(), "Статусы не совпадают при пустом списке подзадач.");

        final SubTask subTask1 = subTaskCreate(epicTask);
        final SubTask subTask2 = subTaskCreate(epicTask);

        // Все подзадачи со статусом NEW.
        taskManager.addSubTask(subTask1, epicTask);
        taskManager.addSubTask(subTask2, epicTask);

        Assertions.assertEquals(Status.NEW, epicTask.getStatus(), "Статусы не совпадают при подзадачах со статусом NEW.");

        // Подзадачи со статусом IN_PROGRESS.
        subTask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask1);

        Assertions.assertEquals(Status.IN_PROGRESS, epicTask.getStatus(), "Статусы не совпадают при подзадачах со статусом IN_PROGRESS.");

        // Подзадачи со статусами NEW и DONE.
        subTask1.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask1);
        Assertions.assertEquals(Status.IN_PROGRESS, epicTask.getStatus(), "Статусы не совпадают при подзадачах со статусами NEW и DONE");

        // Все подзадачи со статусом DONE
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask2);
        Assertions.assertEquals(Status.DONE, epicTask.getStatus(), "Статусы не совпадают при подзадачах со статусами DONE");
    }

    @Test
    public void shouldWhenRemoveTaskById() {
        final Task task = taskCreate();
        taskManager.addTask(task);
        final int taskId = task.getId();
        taskManager.removeByIdTask(taskId);
        final List<Task> list = taskManager.getListTask();
        Assertions.assertEquals(0, list.size(), "Задача task не удалена");
    }

    @Test
    public void shouldWhenRemoveEpicTaskById() {
        final EpicTask epicTask = epicTaskCreate();
        taskManager.addEpicTask(epicTask);
        final int epicTaskId = epicTask.getId();
        taskManager.removeByIdEpicTask(epicTaskId);
        final List<EpicTask> list = taskManager.getListEpicTask();
        Assertions.assertEquals(0, list.size(), "Задача epicTask не удалена");
    }

    @Test
    public void shouldWhenRemoveSubTaskById() {
        final EpicTask epicTask = epicTaskCreate();
        taskManager.addEpicTask(epicTask);
        final SubTask subTask = subTaskCreate(epicTask);
        taskManager.addSubTask(subTask, epicTask);

        final int subTaskId = subTask.getId();
        taskManager.removeByIdSubTask(subTaskId);
        final List<SubTask> list = taskManager.getListSubTask();
        Assertions.assertEquals(0, list.size(), "Задача subTask не удалена");
    }

    @Test
    public void shouldWhenAddTaskOnTime(){
        final Task task = taskCreate();
        taskManager.addTask(task);
        final EpicTask epicTask = epicTaskCreate();
        taskManager.addEpicTask(epicTask);
        final SubTask subTask = subTaskCreate(epicTask);
        taskManager.addSubTask(subTask, epicTask);
        final SubTask subTask1 = subTaskCreate(epicTask);
        taskManager.addSubTask(subTask1, epicTask);

        Assertions.assertNotNull(task.getStartTime(),"Время не установлено");
        Assertions.assertNotNull(epicTask.getStartTime(),"Время не установлено");
        Assertions.assertNotNull(subTask.getStartTime(), "Время не установлено");

        Assertions.assertEquals(task.getStartTime().plusSeconds(task.getDuration()), task.getEndTime());
        Assertions.assertEquals(subTask.getStartTime().plusSeconds(subTask.getDuration()), subTask.getEndTime());
        Assertions.assertEquals(subTask.getEndTime().plusSeconds(
                subTask1.getDuration()), epicTask.getEndTime());
    }
}
