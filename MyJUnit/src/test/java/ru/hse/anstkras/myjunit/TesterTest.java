package ru.hse.anstkras.myjunit;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TesterTest {
    @Test
    void simpleTest() throws MyJUnitException {
        List<TestResult> results = Tester.test(SimpleClass.class);
        assertEquals(1, results.size());
        assertEquals(TestResult.TestResultState.SUCCESS, results.get(0).getState());
    }
}