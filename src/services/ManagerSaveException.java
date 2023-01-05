package services;

public class ManagerSaveException extends RuntimeException{
    public ManagerSaveException(final Throwable cause){
        super(cause);
    }

    public ManagerSaveException(final String message){
        super(message);
    }
}
