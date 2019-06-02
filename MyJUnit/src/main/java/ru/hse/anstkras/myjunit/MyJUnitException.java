package ru.hse.anstkras.myjunit;

public class MyJUnitException extends Exception {
    public MyJUnitException(String message) {
        super(message);
    }

    public MyJUnitException(String message, Throwable cause) {
        super(message, cause);
    }
}
