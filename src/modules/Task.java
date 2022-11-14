package modules;

import services.Status;

/**
 * Класс описывающий одиночную задачу
 */

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "Имя задачи='" + name + '\'' +
                ", описание задачи='" + description + '\'' +
                ", id=" + id + ", статус=" + getStatus() + '}';
    }
}
