package managers;

import modules.EpicTask;
import modules.SubTask;
import modules.Task;
import services.TypeTask;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Класс описывающий реализацию менеджера FileBackedTasksManager.
 * Позволяет записывать задачи в файл и считывать задачи из файла.
 * Имеет статический метод public static FileBackedTasksManager loadFromFile. Метод восстанавливает данные
 * менеджера из файла при запуске программы
 */

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTasksManager() {

    }
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
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file))) {

            fileWriter.write("id,type,name,status,description,startTime,duration,epic");
            fileWriter.newLine();

            for (Map.Entry<Integer, Task> item : getTaskMap().entrySet()) {
                fileWriter.write(item.getValue().toString());
                fileWriter.newLine();
            }
            for (Map.Entry<Integer, EpicTask> item : getEpicTaskMap().entrySet()) {
                fileWriter.write(item.getValue().toString());
                fileWriter.newLine();
            }
            for (Map.Entry<Integer, SubTask> item : getSubTaskMap().entrySet()) {
                fileWriter.write(item.getValue().toString());
                fileWriter.newLine();
            }

            fileWriter.newLine();
            fileWriter.write(historyToString(getHistoryManager()));

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    // метод создания задачи из строки
    public static Task fromString(String value) {
        String[] data = value.split(",", 8);
        TypeTask type = TypeTask.valueOf(data[1]);

        switch (type) {
            case TASK:
                Task task = new Task(data);
                return task;
            case EPICTASK:
                EpicTask epicTask = new EpicTask(data);
                return epicTask;
            case SUBTASK:
                SubTask subTask = new SubTask(data);
                return subTask;
            default:
                return null;
        }
    }

    // сохранение менеджера истории в файл
    public static String historyToString(HistoryManager manager) {
        StringJoiner joiner = new StringJoiner(",");
        for (int i = 0; i < manager.getHistory().size(); i++) {
            joiner.add(String.valueOf(manager.getHistory().get(i).getId()));
        }
        return joiner.toString();
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
        try (BufferedReader br = new BufferedReader(new FileReader(file1))) {

            boolean flagIsEmpty = true;
            br.readLine();
            while (br.ready()) {
                String line = br.readLine();
                if (!line.isBlank()) {

                    flagIsEmpty = false;
                    Task tasks = fromString(line);

                    if (tasks.getTypeTask() == TypeTask.EPICTASK) {
                        fbtm.getEpicTaskMap().put(tasks.getId(), (EpicTask) tasks);
                    } else if (tasks.getTypeTask() == TypeTask.SUBTASK) {
                        fbtm.getSubTaskMap().put(tasks.getId(), (SubTask) tasks);
                        int id = ((SubTask) tasks).getEpicId();
                        fbtm.getEpicTaskById(id).addIdSubTask(tasks.getId());
                    } else fbtm.getTaskMap().put(tasks.getId(), tasks);


                } else {
                    if (flagIsEmpty){
                        return fbtm;
                    }

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
    public ArrayList<SubTask> getListSubEpicTask(EpicTask epicTask) {
        return super.getListSubEpicTask(epicTask);
    }
}
