package ru.hse.anstkras.myjunit;

/** Represents an exception that may occur during the testing */
public class MyJUnitException extends Exception {
    public MyJUnitException(String message) {
        super(message);
    }

    public MyJUnitException(String message, Throwable cause) {
        super(message, cause);
    }
}
