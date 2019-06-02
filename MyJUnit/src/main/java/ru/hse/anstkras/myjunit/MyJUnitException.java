package ru.hse.anstkras.myjunit;

/** Represents an exception that may occur during the testing */
public class MyJUnitException extends Exception {
    MyJUnitException(String message) {
        super(message);
    }

    MyJUnitException(String message, Throwable cause) {
        super(message, cause);
    }
}
