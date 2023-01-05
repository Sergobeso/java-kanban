package services;

import modules.EpicTask;
import modules.SubTask;
import modules.Task;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Класс описывающий реализацию менеджера FileBackedTasksManager.
 * Позволяет записывать задачи в файл и считывать задачи из файла.
 * Имеет статический метод public static FileBackedTasksManager loadFromFile. Метод восстанавливает данные
 * менеджера из файла при запуске программы
 */

public class FileBackedTasksManager extends InMemoryTaskManager {

    /* Тестовый вызов возможностей менеджера*/
    public static void main(String[] args) {

        FileBackedTasksManager taskManager = new FileBackedTasksManager(new File("./data/history.csv"));

        //Создаем 1 Эпик с 2 подзадачами
        taskManager.addEpicTask(new EpicTask("Построить дом", "1й этап"));
        taskManager.addSubTask(new SubTask("Сделать фундамент", "Залить плиту 300 мм", 1), taskManager.getEpicTaskById(1));
        taskManager.addSubTask(new SubTask("Построить стены", "Газобетон 375 мм", 1), taskManager.getEpicTaskById(1));

        // Создаем 2й эпик с 1 подзадачей
        taskManager.addEpicTask(new EpicTask("Доделать дом", "2й этап"));
        taskManager.addSubTask(new SubTask("Заказать окна", "Стеклопакеты", 4), taskManager.getEpicTaskById(4));

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
        taskManager.getEpicTaskById(7);
        taskManager.getEpicTaskById(4);
        taskManager.getEpicTaskById(1);
        taskManager.getSubTaskById(2);
        taskManager.getEpicTaskById(1);
        taskManager.getSubTaskById(2);
        taskManager.removeByIdEpicTask(7);
        FileBackedTasksManager fileBackedTasksManager = FileBackedTasksManager.loadFromFile(new File("./data/history1.csv"));
    }
    /*Конец тестового метода*/
    /*******************************************************************************************************/

    private File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
        String fileName = "./data/history.csv";
        file = new File(fileName);

        try {
            if (!Files.exists(Paths.get(fileName))) {
                Path path = Files.createFile(Paths.get(fileName));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка при создании файла");
        }
    }

    // метод записи состояния менеджера задач в файл
    public void save() {
        try (Writer fileWriter = new FileWriter(file);) {

            fileWriter.write("id,type,name,status,description,epic\n");

            for (Map.Entry<Integer, Task> item : getTaskMap().entrySet()) {
                fileWriter.write(item.getValue() + "\n");
            }
            for (Map.Entry<Integer, EpicTask> item : getEpicTaskMap().entrySet()) {
                fileWriter.write(item.getValue() + "\n");
            }
            for (Map.Entry<Integer, SubTask> item : getSubTaskMap().entrySet()) {
                fileWriter.write(item.getValue() + "\n");
            }

            fileWriter.write("\n");
            fileWriter.write(historyToString(getHistoryManager()));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения файла");
        }
    }

    // метод создания задачи из строки
    public static Task fromString(String value) {
        String[] data = value.split(",", 6);
        int id = Integer.parseInt(data[0]);
        TypeTask type = TypeTask.valueOf(data[1]);
        String name = data[2];
        Status status = Status.valueOf(data[3]);
        String description = data[4];

        switch (type) {
            case TASK:
                Task task = new Task(id, name, status, description);
                return task;
            case EPICTASK:
                EpicTask epicTask = new EpicTask(id, name, status, description);
                return epicTask;
            case SUBTASK:
                SubTask subTask = new SubTask(id, name, status, description, Integer.parseInt(data[5]));
                return subTask;
            default:
                return null;
        }
    }

    // сохранение менеджера истории в файл
    public static String historyToString(HistoryManager manager) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < manager.getHistory().size(); i++) {
            str.append(manager.getHistory().get(i).getId());
            if (i != manager.getHistory().size() - 1) {
                str.append(",");
            }
        }
        return str.toString();
    }

    // восстановление менеджера истории из CSV
    public static List<Integer> historyFromString(String value) {
        String[] idLine = value.split(",");
        List<Integer> listId = new ArrayList<>();
        for (String item : idLine) {
            listId.add(Integer.parseInt(item));
        }
        return listId;
    }

    //метод восстанавливает данные менеджера из файла при запуске программы
    public static FileBackedTasksManager loadFromFile(File file1) {
        FileBackedTasksManager fbtm = new FileBackedTasksManager(file1);
        try (FileReader reader = new FileReader(file1);){
            BufferedReader br = new BufferedReader(reader);

            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (!line.isBlank()) {

                    Task tasks = fromString(line);

                    if (tasks instanceof EpicTask) {
                        fbtm.getEpicTaskMap().put(tasks.getId(), (EpicTask) tasks);
                    } else if (tasks instanceof SubTask) {
                        fbtm.getSubTaskMap().put(tasks.getId(), (SubTask) tasks);
                        int id = ((SubTask) tasks).getEpicId();
                        fbtm.getEpicTaskById(id).addIdSubTask(tasks.getId());
                    } else fbtm.getTaskMap().put(tasks.getId(), tasks);


                } else {
                    String lineNext = br.readLine();
                    List<Integer> list = historyFromString(lineNext);

                    for (Integer id : list) {
                        if (fbtm.getTaskMap().containsKey(id)) {
                            fbtm.getHistoryManager().add(fbtm.getTaskById(id));
                        } else if (fbtm.getEpicTaskMap().containsKey(id)) {
                            fbtm.getHistoryManager().add(fbtm.getEpicTaskById(id));
                        } else if (fbtm.getSubTaskMap().containsKey(id)) {
                            fbtm.getHistoryManager().add(fbtm.getSubTaskById(id));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fbtm;
    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpicTask(EpicTask epicTask) {
        super.addEpicTask(epicTask);
        save();
    }

    @Override
    public void addSubTask(SubTask subTask, EpicTask epicTask) {
        super.addSubTask(subTask, epicTask);
        save();
    }

    @Override
    public ArrayList<Task> getListTask() {
        return super.getListTask();
    }

    @Override
    public ArrayList<EpicTask> getListEpicTask() {
        return super.getListEpicTask();
    }

    @Override
    public ArrayList<SubTask> getListSubTask() {
        return super.getListSubTask();
    }

    @Override
    public void clearTask() {
        super.clearTask();
    }

    @Override
    public void clearEpicTaskMap() {
        super.clearEpicTaskMap();
    }

    @Override
    public void clearSubTaskMap() {
        super.clearSubTaskMap();
    }


    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public EpicTask getEpicTaskById(int id) {
        EpicTask epicTask = super.getEpicTaskById(id);
        save();
        return epicTask;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpicTaskMap(EpicTask epicTask) {
        super.updateEpicTaskMap(epicTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeByIdTask(int id) {
        super.removeByIdTask(id);
        save();
    }

    @Override
    public void removeByIdEpicTask(int id) {
        super.removeByIdEpicTask(id);
        save();
    }

    @Override
    public void removeByIdSubTask(int id) {
        super.removeByIdSubTask(id);
        save();
    }

    @Override
    public ArrayList<SubTask> getListSubTask(EpicTask epicTask) {
        return super.getListSubTask();
    }
}
