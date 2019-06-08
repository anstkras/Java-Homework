package ru.hse.anstkras.myjunit.testClasses;

import ru.hse.anstkras.myjunit.BeforeClass;
import ru.hse.anstkras.myjunit.Test;

public class BeforeClassNotStatic {
    @BeforeClass
    public void init() {
    }

    @Test
    public void test() {
    }
}
