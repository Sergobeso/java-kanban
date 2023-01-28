package tests;

import managers.ManagerSaveException;
import managers.TaskManager;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.Status;

import java.time.Instant;
import java.util.List;

/**
 * Класс описывающий реализацию ТЕСТОВ менеджера InMemoryTaskManager и FileBackedTasksManager.
 */
abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected EpicTask epicTask;
    protected SubTask subTask;
    protected Task task;

    @BeforeEach
    protected void taskCreated() {
        epicTask = new EpicTask(1, "Название большой задачи!", Status.NEW, "Описание большой задачи", Instant.now(), 0);
        subTask = new SubTask(2, "Название подзадачи", Status.NEW, "Описание подзадачи", Instant.now(), 180, 1);
        task = new Task(3, "Название одиночной задачи", Status.NEW, "Описание одиночной задачи", Instant.now().plusSeconds(500), 1000);
    }

    @Test
    public void shouldAddTask() {
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

        taskManager.addEpicTask(epicTask);
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
        taskManager.addEpicTask(epicTask);

        // Пустой список подзадач.
        Assertions.assertEquals(Status.NEW, epicTask.getStatus(), "Статусы не совпадают при пустом списке подзадач.");

        final SubTask subTask2 = new SubTask("Название подзадачи", "Описание подзадачи", Instant.now().plusSeconds(200), 180, epicTask.getId());

        // Все подзадачи со статусом NEW.
        taskManager.addSubTask(subTask, epicTask);
        taskManager.addSubTask(subTask2, epicTask);

        Assertions.assertEquals(Status.NEW, epicTask.getStatus(), "Статусы не совпадают при подзадачах со статусом NEW.");

        // Подзадачи со статусом IN_PROGRESS.
        subTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubTask(subTask);

        Assertions.assertEquals(Status.IN_PROGRESS, epicTask.getStatus(), "Статусы не совпадают при подзадачах со статусом IN_PROGRESS.");

        // Подзадачи со статусами NEW и DONE.
        subTask.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask);
        Assertions.assertEquals(Status.IN_PROGRESS, epicTask.getStatus(), "Статусы не совпадают при подзадачах со статусами NEW и DONE");

        // Все подзадачи со статусом DONE
        subTask2.setStatus(Status.DONE);
        taskManager.updateSubTask(subTask2);
        Assertions.assertEquals(Status.DONE, epicTask.getStatus(), "Статусы не совпадают при подзадачах со статусами DONE");
    }

    @Test
    public void shouldWhenRemoveTaskById() {
        taskManager.addTask(task);
        final int taskId = task.getId();
        taskManager.removeByIdTask(taskId);
        final List<Task> list = taskManager.getListTask();
        Assertions.assertEquals(0, list.size(), "Задача task не удалена");
    }

    @Test
    public void shouldWhenRemoveEpicTaskById() {
        taskManager.addEpicTask(epicTask);
        final int epicTaskId = epicTask.getId();
        taskManager.removeByIdEpicTask(epicTaskId);
        final List<EpicTask> list = taskManager.getListEpicTask();
        Assertions.assertEquals(0, list.size(), "Задача epicTask не удалена");
    }

    @Test
    public void shouldWhenRemoveSubTaskById() {
        taskManager.addEpicTask(epicTask);
        taskManager.addSubTask(subTask, epicTask);

        final int subTaskId = subTask.getId();
        taskManager.removeByIdSubTask(subTaskId);
        final List<SubTask> list = taskManager.getListSubTask();
        Assertions.assertEquals(0, list.size(), "Задача subTask не удалена");
    }

    @Test
    public void shouldWhenAddTaskOnTime() {
        taskManager.addTask(task);
        taskManager.addEpicTask(epicTask);
        taskManager.addSubTask(subTask, epicTask);
        final SubTask subTask1 = new SubTask("Название подзадачи", "Описание подзадачи", Instant.now().plusSeconds(200), 180, epicTask.getId());

        taskManager.addSubTask(subTask1, epicTask);

        Assertions.assertNotNull(task.getStartTime(), "Время не установлено");
        Assertions.assertNotNull(epicTask.getStartTime(), "Время не установлено");
        Assertions.assertNotNull(subTask.getStartTime(), "Время не установлено");

        Assertions.assertEquals(task.getStartTime().plusSeconds(task.getDuration()), task.getEndTime());
        Assertions.assertEquals(subTask.getStartTime().plusSeconds(subTask.getDuration()), subTask.getEndTime());
        Assertions.assertEquals(subTask.getEndTime().plusSeconds(
                        subTask1.getDuration()), epicTask.getEndTime(),
                "Неправильно посчитана продолжительность времени Epic");
    }

    @Test
    public void shouldTasksInPrioritizedList() {
        final Task task = new Task("Название одиночной задачи", "Описание одиночной задачи", Instant.now(), 0);
        taskManager.addTask(task);
        taskManager.addEpicTask(epicTask);
        final SubTask subTask = new SubTask("Название подзадачи 1", "Описание подзадачи 1", Instant.now().plusSeconds(200), 0, epicTask.getId());
        taskManager.addSubTask(subTask, epicTask);
        final SubTask subTask1 = new SubTask("Название подзадачи 2", "Описание подзадачи 2", Instant.now().plusSeconds(400), 0, epicTask.getId());
        taskManager.addSubTask(subTask1, epicTask);

        Assertions.assertEquals(taskManager.getListTask().size() + taskManager.getListSubTask().size(),
                taskManager.getPrioritizedTasks().size(), "Задача не попадает в отсортированный список");

    }

    @Test
    public void shouldTasksInPrioritizedListWhenStartTimeNull() {
        final Task task = new Task("Название одиночной задачи", "Описание одиночной задачи", null, 0);
        taskManager.addTask(task);
        taskManager.addEpicTask(epicTask);
        final SubTask subTask = new SubTask("Название подзадачи 1", "Описание подзадачи 1", null, 0, epicTask.getId());
        taskManager.addSubTask(subTask, epicTask);
        final SubTask subTask1 = new SubTask("Название подзадачи 2", "Описание подзадачи 2", null, 0, epicTask.getId());
        taskManager.addSubTask(subTask1, epicTask);

        Assertions.assertEquals(taskManager.getListTask().size() + taskManager.getListSubTask().size(),
                taskManager.getPrioritizedTasks().size(), "Задача не попадает в отсортированный список");

    }

    @Test
    public void shouldTasksInPrioritizedListWhenStartTimeNullAndNotNull() {
        final Task task = new Task("Название одиночной задачи", "Описание одиночной задачи", null, 0);
        taskManager.addTask(task);
        taskManager.addEpicTask(epicTask);
        final SubTask subTask = new SubTask("Название подзадачи 1", "Описание подзадачи 1", Instant.now(), 0, epicTask.getId());
        taskManager.addSubTask(subTask, epicTask);
        final SubTask subTask1 = new SubTask("Название подзадачи 2", "Описание подзадачи 2", null, 0, epicTask.getId());
        taskManager.addSubTask(subTask1, epicTask);

        Assertions.assertEquals(taskManager.getListTask().size() + taskManager.getListSubTask().size(),
                taskManager.getPrioritizedTasks().size(), "Задача не попадает в отсортированный список");
    }

    @Test
    public void shouldWhenIntersectionTasks() {
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            final Task task = new Task("Название одиночной задачи", "Описание одиночной задачи", Instant.now(), 3000);
            taskManager.addTask(task);
            taskManager.addEpicTask(epicTask);
            final SubTask subTask = new SubTask("Название подзадачи 1", "Описание подзадачи 1", Instant.now(), 0, epicTask.getId());
            taskManager.addSubTask(subTask, epicTask);
            final SubTask subTask1 = new SubTask("Название подзадачи 2", "Описание подзадачи 2", Instant.now(), 0, epicTask.getId());
            taskManager.addSubTask(subTask1, epicTask);
        });
    }

    @Test
    public void shouldWhenIntersectionTasksEndList() {
        Assertions.assertThrows(ManagerSaveException.class, () -> {
            final Task task = new Task("Название одиночной задачи", "Описание одиночной задачи", Instant.now().plusSeconds(180), 0);
            taskManager.addTask(task);
            final Task task2 = new Task("Название одиночной задачи", "Описание одиночной задачи", Instant.now(), 100);
            taskManager.addTask(task2);
            final Task task3 = new Task("Название одиночной задачи", "Описание одиночной задачи", Instant.now(), 0);
            taskManager.addTask(task3);
        });
    }
}
