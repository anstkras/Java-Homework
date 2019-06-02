package ru.hse.anstkras.myjunit;

public class ClassThrowWrongException {
    @Test(exception = IllegalStateException.class)
    void test() {
        throw new IllegalArgumentException();
    }
}
