package services;

/**
 * В классе перечислены статусы состояния задач
 */

public enum Status {
    NEW("NEW"), IN_PROGRESS("IN_PROGRESS"), DONE("DONE");

    private String status;

    Status(String name){
        this.status = name;
    }

    public String getStatus() {
        return status;
    }
}
