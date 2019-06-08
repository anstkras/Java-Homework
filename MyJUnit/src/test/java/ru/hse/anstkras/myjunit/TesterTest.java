package ru.hse.anstkras.myjunit;

import org.junit.jupiter.api.Test;
import ru.hse.anstkras.myjunit.testClasses.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TesterTest {
    @Test
    void testClassWithoutConstructor() {
        assertThrows(MyJUnitException.class, () -> {
            var tester = new Tester(ClassWithoutConstructor.class);
            tester.runTests();
        });
    }

    @Test
    void testClassWithTestMethodParameters() {
        assertThrows(MyJUnitException.class, () -> {
            var tester = new Tester(ClassWithTestMethodParameters.class);
            tester.runTests();
        });
    }

    @Test
    void testClassWithPrivateTestMethod() {
        assertThrows(MyJUnitException.class, () -> {
            var tester = new Tester(ClassWithPrivateTestMethod.class);
            tester.runTests();
        });
    }

    @Test
    void testClassWithNotStaticBeforeClass() {
        assertThrows(MyJUnitException.class, () -> {
            var tester = new Tester(BeforeClassNotStatic.class);
            tester.runTests();
        });
    }

    @Test
    void testClassWithMixedAnnotations() {
        assertThrows(MyJUnitException.class, () -> {
            var tester = new Tester(ClassWithMixedAnnotations.class);
            tester.runTests();
        });
    }

    @Test
    void testMethodDoesNotThrow() throws MyJUnitException {
        var tester = new Tester(ClassDoesNotThrow.class);
        List<TestResult> results = tester.runTests();
        assertEquals(1, results.size());
        assertEquals(TestResult.TestResultState.SUCCESS, results.get(0).getState());
    }

    @Test
    void testThrowException() throws MyJUnitException {
        var tester = new Tester(ClassThrowException.class);
        List<TestResult> results = tester.runTests();
        assertEquals(1, results.size());
        assertEquals(TestResult.TestResultState.SUCCESS, results.get(0).getState());
    }

    @Test
    void testThrowWrongException() throws MyJUnitException {
        var tester = new Tester(ClassThrowWrongException.class);
        List<TestResult> results = tester.runTests();
        assertEquals(1, results.size());
        assertEquals(TestResult.TestResultState.FAIL, results.get(0).getState());
    }

    @Test
    void testIgnored() throws MyJUnitException {
        var tester = new Tester(ClassIgnored.class);
        List<TestResult> results = tester.runTests();
        assertEquals(1, results.size());
        assertEquals(TestResult.TestResultState.IGNORED, results.get(0).getState());
    }

    @Test
    void testInteraction() {
        assertDoesNotThrow(() -> {
            var tester = new Tester(InteractionClass.class);
            tester.runTests();
        });
    }
}