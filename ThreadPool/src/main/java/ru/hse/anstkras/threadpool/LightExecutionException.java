package ru.hse.anstkras.threadpool;

/**
 * Represents an exception that can occurs while executing a task
 * represented by LightFuture
 */
public class LightExecutionException extends Exception {

    /** Creates a new exception without message */
    public LightExecutionException() {
        super();
    }

    /** Creates a new exception with the given message */
    public LightExecutionException(String message) {
        super(message);
    }

    /** Creates a new exception with the given cause */
    public LightExecutionException(Throwable cause) {
        super(cause);
    }
}
