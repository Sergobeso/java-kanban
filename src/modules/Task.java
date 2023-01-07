package modules;

import services.Status;
import services.TypeTask;

import java.util.Objects;

/**
 * Класс описывающий одиночную задачу
 */

public class Task {
    protected String name;
    protected String description;
    protected int id;
    protected Status status;
    protected TypeTask typeTask;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.typeTask = TypeTask.TASK;
    }

    public Task(int id, String name, Status status, String description) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.typeTask = TypeTask.TASK;
    }

    public Task(String[] data) {
        this.name = data[2];
        this.description = data[4];
        this.status = Status.valueOf(data[3]);
        this.id = Integer.parseInt(data[0]);
        this.typeTask = TypeTask.TASK;
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

    public TypeTask getTypeTask() {
        return typeTask;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,", id, typeTask, name, status, description);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status);
    }
}
