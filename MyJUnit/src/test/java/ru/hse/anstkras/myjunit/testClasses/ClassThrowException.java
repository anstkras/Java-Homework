package ru.hse.anstkras.myjunit.testClasses;

public class ClassThrowException {
    @Test(exception = IllegalStateException.class)
    void test() {
        throw new IllegalStateException();
    }
}
