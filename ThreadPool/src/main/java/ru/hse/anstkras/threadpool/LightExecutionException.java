package ru.hse.anstkras.threadpool;

public class LightExecutionException extends Exception {
    public LightExecutionException() {
        super();
    }

    public LightExecutionException(Exception exception) {
        super(exception);
    }
}
