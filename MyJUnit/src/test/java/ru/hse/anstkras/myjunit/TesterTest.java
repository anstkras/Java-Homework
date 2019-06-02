package ru.hse.anstkras.myjunit;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class TesterTest {
    @Test
    void simpleTest() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Tester tester = new Tester();
        tester.test(SimpleClass.class);
    }
}