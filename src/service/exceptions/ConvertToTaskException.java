package service.exceptions;

public class ConvertToTaskException extends RuntimeException {

    public ConvertToTaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConvertToTaskException(String message) {
        super(message);
    }
}
