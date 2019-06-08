package ru.hse.anstkras.myjunit.testClasses;

import ru.hse.anstkras.myjunit.Test;

public class ClassThrowException {
    @Test(exception = IllegalStateException.class)
    void test() {
        throw new IllegalStateException();
    }
}
