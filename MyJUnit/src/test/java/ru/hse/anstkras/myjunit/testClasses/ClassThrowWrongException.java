package ru.hse.anstkras.myjunit.testClasses;

public class ClassThrowWrongException {
    @Test(exception = IllegalStateException.class)
    void test() {
        throw new IllegalArgumentException();
    }
}
