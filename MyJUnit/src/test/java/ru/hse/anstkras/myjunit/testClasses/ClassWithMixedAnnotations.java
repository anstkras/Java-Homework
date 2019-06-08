package ru.hse.anstkras.myjunit.testClasses;

import ru.hse.anstkras.myjunit.AfterClass;
import ru.hse.anstkras.myjunit.Test;

public class ClassWithMixedAnnotations {
    @AfterClass
    @Test
    public void test() {
    }
}
