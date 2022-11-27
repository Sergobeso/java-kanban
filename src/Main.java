import modules.EpicTask;
import modules.SubTask;
import services.Managers;
import services.Status;
import services.TaskManager;

/**
 * В классе проведены некоторые тесты для ознакомления
 */

public class Main {

    public static void main(String[] args) {


        TaskManager taskManager = Managers.getDefault();

        //Создаем 1 Эпик с 2 подзадачами
        taskManager.addEpicTask(new EpicTask("Построить дом", "1й этап"));
        taskManager.addSubTask(new SubTask("Сделать фундамент", "Залить плиту 300 мм", 1), taskManager.getEpicTaskById(1));
        taskManager.addSubTask(new SubTask("Построить стены", "Газобетон 375 мм", 1), taskManager.getEpicTaskById(1));

        // Создаем 2й эпик с 1 подзадачей
        taskManager.addEpicTask(new EpicTask("Доделать дом", "2й этап"));
        taskManager.addSubTask(new SubTask("Заказать окна", "Стеклопакеты", 4), taskManager.getEpicTaskById(4));

        System.out.println(Managers.getDefaultHistory().getHistory());

        //Печатаем списки эпиков, задач и подзадач
//        System.out.println(taskManager.getListTask());
//        System.out.println(taskManager.getListEpicTask());
//        System.out.println(taskManager.getListSubTask());

        //Изменяем статусы созданных объектов и обновляем
        taskManager.updateSubTask(new SubTask(2, "Сделать фундамент", "Залить плиту 300 мм", Status.IN_PROGRESS, 1));
        taskManager.updateSubTask(new SubTask(5, "Заказать окна", "Стеклопакеты", Status.DONE, 4));

        System.out.println(Managers.getDefaultHistory().getHistory());

        // Печатаем списки после изменения статусов
//        System.out.println();
//        System.out.println(taskManager.getListEpicTask());
//        System.out.println(taskManager.getListSubTask());
//        System.out.println();


        // Удаляем задачу из одного из эпиков
//        taskManager.removeByIdEpicTask(4);
//        System.out.println(taskManager.getListEpicTask());
//        System.out.println(taskManager.getListSubTask());

        // manager.getTaskById(11);

        System.out.println("Печатаем Историю просмотров: ");
        for (int i = 0; i < Managers.getDefaultHistory().getHistory().size(); i++) {
            System.out.println(Managers.getDefaultHistory().getHistory().get(i));
        }
    }
}
