import modules.EpicTask;
import modules.SubTask;
import services.Manager;
import services.Status;

/**
 * В классе проведены некоторые тесты для ознакомления
 */

public class Main {

    public static void main(String[] args) {

        Manager manager = new Manager();

        // Создаем 1 Эпик с 2 подзадачами
        manager.setEpicTask(new EpicTask("Построить дом", "1й этап"));
        manager.setSubTask(new SubTask("Сделать фундамент", "Залить плиту 300 мм", 1), manager.getEpicTaskById(1));
        manager.setSubTask(new SubTask("Построить стены", "Газобетон 375 мм", 1), manager.getEpicTaskById(1));

        // Создаем 2й эпик с 1 подзадачей
        manager.setEpicTask(new EpicTask("Доделать дом", "2й этап"));
        manager.setSubTask(new SubTask("Заказать окна", "Стеклопакеты", 4), manager.getEpicTaskById(4));

        //Печатаем списки эпиков, задач и подзадач
        System.out.println(manager.getListTask());
        System.out.println(manager.getListEpicTask());
        System.out.println(manager.getListSubTask());

        //Изменяем статусы созданных объектов и обновляем
        manager.updateSubTask((manager.getSubTaskById(2)),  Status.IN_PROGRESS);
        manager.updateSubTask(manager.getSubTaskById(5), Status.DONE);

        // Печатаем списки после изменения статусов
        System.out.println();
        System.out.println(manager.getListEpicTask());
        System.out.println(manager.getListSubTask());
        System.out.println();

        // Удаляем задачу из одного из эпиков
        manager.removeByIdEpicTas(4);
        System.out.println(manager.getListEpicTask());
        System.out.println(manager.getListSubTask());
    }
}
