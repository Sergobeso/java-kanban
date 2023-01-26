package modules;

import services.Status;
import services.TypeTask;

import java.time.Instant;
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
    protected Instant startTime;
    protected long duration = 0;

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

    public Task(String name, String description, Instant startTime, long duration) {
        this.name = name;
        this.description = description;
        this.startTime = startTime;
        this.duration = duration;
        this.status = Status.NEW;
        this.typeTask = TypeTask.TASK;
    }

    public Task(int id, String name, Status status, String description, Instant startTime, long duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.typeTask = TypeTask.TASK;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String[] data) {
        this.name = data[2];
        this.description = data[4];
        this.status = Status.valueOf(data[3]);
        this.id = Integer.parseInt(data[0]);
        this.typeTask = TypeTask.TASK;
        if (data[5] == null){
            this.startTime = Instant.parse(data[5]);
        } else this.startTime = null;
        this.duration = Long.parseLong(data[6]);
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return startTime.plusSeconds(duration * 60);
    }

    @Override
    public String toString() {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm").withZone(ZoneId.systemDefault());
//        String formatDate = formatter.format(startTime);
        return String.format("%d,%s,%s,%s,%s," + startTime  + ",%d", id, typeTask, name, status, description, duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && duration == task.duration && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && typeTask == task.typeTask && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, typeTask, startTime, duration);
    }
}
