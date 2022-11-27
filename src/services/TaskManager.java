package services;

import modules.EpicTask;
import modules.SubTask;
import modules.Task;

import java.util.ArrayList;

/** Интерфейс описывающий взаимодействие с задачами
* Имеет следующую функциональность:
         * - создает задачи
         * - получает задачи
         * - обновляет задачи
         * - удаляет задачи
*/

public interface TaskManager {

    // Создание задачи
    void addTask(Task task);
    void addEpicTask(EpicTask epicTask);
    void addSubTask(SubTask subTask, EpicTask epicTask);

    // Получение списка всех задач.
    ArrayList<Task> getListTask();
    ArrayList<EpicTask> getListEpicTask();
    ArrayList<SubTask> getListSubTask();

    // Удаление всех задач.
    void clearTask();
    void clearEpicTaskMap();
    void clearSubTaskMap();

    //Получение по идентификатору.
    Task getTaskById(int id);
    EpicTask getEpicTaskById(int id);
    SubTask getSubTaskById(int id);

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
    void updateTask(Task task);
    void updateEpicTaskMap(EpicTask epicTask);
    void updateSubTask(SubTask subTask);

    // Удаление по идентификатору.
    void removeByIdTask(int id);
    void removeByIdEpicTask(int id);
    void removeByIdSubTask(int id);

    // Получение списка всех подзадач определённого эпика.
    ArrayList<SubTask> getListSubTask(EpicTask epicTask);

}
