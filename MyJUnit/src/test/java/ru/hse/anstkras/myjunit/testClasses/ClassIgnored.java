package ru.hse.anstkras.myjunit.testClasses;

import ru.hse.anstkras.myjunit.Test;

public class ClassIgnored {
    @Test(ignored = "because")
    void test() {
    }
}
