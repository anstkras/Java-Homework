package ru.hse.anstkras.myjunit;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TesterTest {
    @Test
    void testClassWithoutConstructor() {
        assertThrows(MyJUnitException.class, () -> Tester.test(ClassWithoutConstructor.class));
    }

    @Test
    void testClassWithTestMethodParameters() {
        assertThrows(MyJUnitException.class, () -> Tester.test(ClassWithTestMethodParameters.class));
    }

    @Test
    void testClassWithPrivateTestMethod() {
        assertThrows(MyJUnitException.class, () -> Tester.test(ClassWithPrivateTestMethod.class));
    }

    @Test
    void testMethodDoesNotThrow() throws MyJUnitException {
        List<TestResult> results = Tester.test(ClassDoesNotThrow.class);
        assertEquals(1, results.size());
        assertEquals(TestResult.TestResultState.SUCCESS, results.get(0).getState());
    }

    @Test
    void testThrowException() throws MyJUnitException {
        List<TestResult> results = Tester.test(ClassThrowException.class);
        assertEquals(1, results.size());
        assertEquals(TestResult.TestResultState.SUCCESS, results.get(0).getState());
    }

    @Test
    void testThrowWrongException() throws MyJUnitException {
        List<TestResult> results = Tester.test(ClassThrowWrongException.class);
        assertEquals(1, results.size());
        assertEquals(TestResult.TestResultState.FAIL, results.get(0).getState());
    }

    @Test
    void testIgnored() throws MyJUnitException {
        List<TestResult> results = Tester.test(ClassIgnored.class);
        assertEquals(1, results.size());
        assertEquals(TestResult.TestResultState.IGNORED, results.get(0).getState());
    }

    @Test
    void testInteraction() {
        assertDoesNotThrow(() -> Tester.test(InteractionClass.class));
    }
}