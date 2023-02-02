import managers.FileBackedTasksManager;
import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import server.HttpTaskServer;
import services.Status;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

/**
 * В классе проведены некоторые тесты для ознакомления
 */

public class Main {

    public static void main(String[] args) throws IOException {

        FileBackedTasksManager taskManager = new FileBackedTasksManager(new File("./data/history.csv"));
        // TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();

        //Создаем 1 Эпик с 2 подзадачами
        taskManager.addEpicTask(new EpicTask("Имя 1 большой задачи", "Описание  1 большой задачи"));
        taskManager.addSubTask(new SubTask("Имя 1 подзадачи 1бол. задачи", "Описание  1 подзадачи 1бол. задачи", 1), taskManager.getEpicTaskById(1));
        taskManager.addSubTask(new SubTask("Имя 2 подзадачи 1бол. задачи", "Описание  2 подзадачи 1бол. задачи", 1), taskManager.getEpicTaskById(1));

        // Создаем 2й эпик с 1 подзадачей
        taskManager.addEpicTask(new EpicTask("Имя 2 большой задачи", "Описание  2 большой задачи"));
        taskManager.addSubTask(new SubTask("Имя 1 подзадачи 2бол. задачи", "Описание  1 подзадачи 2бол. задачи", Instant.now(), 0, 4), taskManager.getEpicTaskById(4));

        // Создаем 3й эпик без подзадач
        taskManager.addEpicTask(new EpicTask("Имя 3 большой задачи без подзадач", ""));

        // Создаем 4й эпик без подзадач
        taskManager.addEpicTask(new EpicTask("Имя 4 большой задачи", ""));
        taskManager.addSubTask(new SubTask("Имя 1 подзадачи 4бол. задачи", "Описание  1 подзадачи 4бол. задачи", 7), taskManager.getEpicTaskById(7));
        taskManager.addSubTask(new SubTask("Имя 2 подзадачи 4бол. задачи", "Описание  2 подзадачи 4бол. задачи", Instant.now(), 0, 7), taskManager.getEpicTaskById(7));
        taskManager.addSubTask(new SubTask("Имя 3 подзадачи 4бол. задачи", "", Instant.now().plusSeconds(1), 0, 7), taskManager.getEpicTaskById(7));

        taskManager.addTask(new Task("Имя 5 ПРОСТОЙ задачи", ""));
        //Изменяем статусы созданных объектов и обновляем
        taskManager.updateSubTask(new SubTask(2, "Имя 1 подзадачи 1бол. задачи", Status.IN_PROGRESS, "Описание 1 подзадачи 1бол. задачи", Instant.now(), 0, 1));
        taskManager.updateSubTask(new SubTask(5, "Имя 1 подзадачи 2бол. задачи", Status.DONE, "Описание подзадачи с статусом Status.DONE", 4));

        //запросите созданные задачи несколько раз в разном порядке;
        taskManager.getSubTaskById(10);
        taskManager.getSubTaskById(9);
        taskManager.getSubTaskById(10);
        taskManager.getSubTaskById(3);
        taskManager.getSubTaskById(8);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(8);
        taskManager.getSubTaskById(2);
        taskManager.getEpicTaskById(1);
        taskManager.getEpicTaskById(6);
        taskManager.getEpicTaskById(7);
        taskManager.getEpicTaskById(4);
        taskManager.getEpicTaskById(1);
        taskManager.getSubTaskById(2);
        taskManager.getEpicTaskById(1);
        taskManager.getSubTaskById(2);
        taskManager.getTaskById(11);


        System.out.println("Печатаем Историю просмотров: ");
        for (int i = 0; i < taskManager.getHistoryManager().getHistory().size(); i++) {
            System.out.println(i + 1 + ". " + taskManager.getHistoryManager().getHistory().get(i));
        }

        taskManager.removeByIdEpicTask(7);
        System.out.println("Печатаем Историю просмотров после удаления: ");
        for (int i = 0; i < taskManager.getHistoryManager().getHistory().size(); i++) {
            System.out.println(i + 1 + ". " + taskManager.getHistoryManager().getHistory().get(i));
        }
        System.out.println("**************************************************************");
        System.out.println(taskManager.getListSubEpicTask(taskManager.getEpicTaskById(1)));

        //восстанавливаем данные менеджера из файла при запуске программы
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File("./data/history.csv"));
        System.out.println("Печатаем Историю просмотров FileBackedTasksManager после загрузки из файла: ");
        for (int i = 0; i < fileBackedTasksManager.getHistoryManager().getHistory().size(); i++) {
            System.out.println(i + 1 + ". " + fileBackedTasksManager.getHistoryManager().getHistory().get(i));
        }

    }
}

