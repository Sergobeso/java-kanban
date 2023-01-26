package tests;

import managers.FileBackedTasksManager;
import managers.InMemoryTaskManager;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

;

public class FileBackedTasksManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private String fileName = "./data/historyTest.csv";
    private File file = new File(fileName);
    private FileBackedTasksManager fileBackedTasksManager;


    @BeforeEach
    public void beforeEach() {
        taskManager = new InMemoryTaskManager();
        fileBackedTasksManager = new FileBackedTasksManager(file);
    }

    @AfterEach
    public void afterEach() {
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void shouldSaveAndLoadTask() {
        Task task = taskCreate();
        fileBackedTasksManager.addTask(task);
        EpicTask epicTask = epicTaskCreate();
        fileBackedTasksManager.addEpicTask(epicTask);
        SubTask subTask = subTaskCreate(epicTask);
        fileBackedTasksManager.addSubTask(subTask, epicTask);

        FileBackedTasksManager fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(file);

        Assertions.assertTrue(fileBackedTasksManager2.getListTask().contains(task), "Задача task не была добавлена");
        Assertions.assertTrue(fileBackedTasksManager2.getListSubTask().contains(subTask), "Задача subTask не была добавлена");
        Assertions.assertTrue(fileBackedTasksManager2.getListEpicTask().contains(epicTask), "Задача epicTask не была добавлена");
    }

    @Test
    public void shouldIsEmptyListTasks() {
        fileBackedTasksManager.save();
        FileBackedTasksManager fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(file);

        Assertions.assertTrue(fileBackedTasksManager2.getListTask().isEmpty(), "Файл загружен с ошибками, список задач Task должен быть пустрой");
        Assertions.assertTrue(fileBackedTasksManager2.getListEpicTask().isEmpty(), "Файл загружен с ошибками, список задач EpicTask должен быть пустрой");
        Assertions.assertTrue(fileBackedTasksManager2.getListSubTask().isEmpty(), "Файл загружен с ошибками, список задач SubTask должен быть пустрой");
    }

    @Test
    public void shouldIsEmptyListHistory() {
        fileBackedTasksManager.save();
        FileBackedTasksManager fileBackedTasksManager2 = FileBackedTasksManager.loadFromFile(file);
        Assertions.assertTrue(fileBackedTasksManager2.getHistoryManager().getHistory().isEmpty(), "История задач должна быть пустрой");
    }

}
