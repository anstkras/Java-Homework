package ru.hse.anstkras.myjunit;

public class ClassThrowException {
    @Test(exception = IllegalStateException.class)
    void test() {
        throw new IllegalStateException();
    }
}
