package ru.hse.anstkras.myjunit;

public class DifficultClass {
    private String string;

    @BeforeClass
    void init() {
        string = "String";
    }

    @Test
    void test() {
        if (string == null) {
            throw new IllegalStateException();
        }
    }
}
