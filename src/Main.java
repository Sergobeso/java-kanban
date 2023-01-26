import managers.FileBackedTasksManager;
import modules.EpicTask;
import modules.SubTask;
import services.Status;

import java.io.File;
import java.time.Instant;

/**
 * В классе проведены некоторые тесты для ознакомления
 */**********************

public class Main {

    public static void main(String[] args) {

        FileBackedTasksManager taskManager = new FileBackedTasksManager(new File("./data/history.csv"));
         //TaskManager taskManager = Managers.getDefault();

        //Создаем 1 Эпик с 2 подзадачами
        taskManager.addEpicTask(new EpicTask("Построить дом", "1й этап"));
        taskManager.addSubTask(new SubTask("Сделать фундамент", "Залить плиту 300 мм", 1), taskManager.getEpicTaskById(1));
        taskManager.addSubTask(new SubTask("Построить стены", "Газобетон 375 мм", 1), taskManager.getEpicTaskById(1));

        // Создаем 2й эпик с 1 подзадачей
        taskManager.addEpicTask(new EpicTask("Доделать дом", "2й этап"));
        taskManager.addSubTask(new SubTask("Заказать окна", "Стеклопакеты", Instant.now(), 0, 4), taskManager.getEpicTaskById(4));

        // Создаем 3й эпик без подзадач
        taskManager.addEpicTask(new EpicTask("Купить квартиру", ""));

        // Создаем 4й эпик без подзадач
        taskManager.addEpicTask(new EpicTask("Купить автомобиль", ""));
        taskManager.addSubTask(new SubTask("Продать старый автомобиль", "За Много денег", 7), taskManager.getEpicTaskById(7));
        taskManager.addSubTask(new SubTask("Взять деньги в банке", "тыщ 300 должно хватить", 7), taskManager.getEpicTaskById(7));
        taskManager.addSubTask(new SubTask("Ехать в салон за авто", "", 7), taskManager.getEpicTaskById(7));

        //Изменяем статусы созданных объектов и обновляем
        taskManager.updateSubTask(new SubTask(2, "Сделать фундамент", Status.IN_PROGRESS, "Залить плиту 300 мм", 1));
        taskManager.updateSubTask(new SubTask(5, "Заказать окна", Status.DONE, "Стеклопакеты", 4));

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


        System.out.println("Печатаем Историю просмотров: ");
        for (int i = 0; i < taskManager.getHistoryManager().getHistory().size(); i++) {
            System.out.println(i + 1 + ". " + taskManager.getHistoryManager().getHistory().get(i));
        }

        taskManager.removeByIdEpicTask(7);
        System.out.println("Печатаем Историю просмотров после удаления: ");
        for (int i = 0; i < taskManager.getHistoryManager().getHistory().size(); i++) {
            System.out.println(i + 1 + ". " + taskManager.getHistoryManager().getHistory().get(i));
        }

        //восстанавливаем данные менеджера из файла при запуске программы
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File("./data/history.csv"));
        System.out.println("Печатаем Историю просмотров FileBackedTasksManager после загрузки из файла: ");
        for (int i = 0; i < fileBackedTasksManager.getHistoryManager().getHistory().size(); i++) {
            System.out.println(i + 1 + ". " + fileBackedTasksManager.getHistoryManager().getHistory().get(i));
        }
    }
}

