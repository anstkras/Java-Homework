package ru.hse.anstkras.myjunit;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/** Runs tests in the given class using annotations */
public class Tester {
    private final @NotNull Object instance;
    private final @NotNull Class<?> clazz;
    private final @NotNull List<Method> beforeClassMethods = new ArrayList<>();
    private final @NotNull List<Method> afterClassMethods = new ArrayList<>();
    private final @NotNull List<Method> beforeMethods = new ArrayList<>();
    private final @NotNull List<Method> afterMethods = new ArrayList<>();
    private final @NotNull List<Method> testMethods = new ArrayList<>();

    /**
     * Creates a tester for a given class by instantiating
     *
     * @param clazz class to create a tester for
     * @throws MyJUnitException if the class does not have accessible constructor with no parameters
     */
    public Tester(@NotNull Class<?> clazz) throws MyJUnitException {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            instance = constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException exception) {
            throw new MyJUnitException("No accessible constructor with no parameters", exception);
        }

        this.clazz = clazz;
        initMethods();
    }

    private static boolean methodHasMJUnitAnnotation(@NotNull Method method) {
        return method.getAnnotation(Test.class) != null || method.getAnnotation(BeforeClass.class) != null
                || method.getAnnotation(Before.class) != null || method.getAnnotation(AfterClass.class) != null
                || method.getAnnotation(After.class) != null;
    }

    /**
     * Runs annotated methods in the class
     *
     * @return list of {@code TestResult} for each {@code Test} annotated method
     * @throws MyJUnitException if an exception occurs in {@code Before, After, BeforeClass, AfterClass}
     *                          annotated methods or some method was not accessible
     */
    @NotNull
    public List<TestResult> runTests() throws MyJUnitException {
        List<TestResult> results = new ArrayList<>();
        try {
            for (Method method : beforeClassMethods) {
                method.invoke(instance);
            }
            for (Method method : testMethods) {
                Test testAnnotation = method.getAnnotation(Test.class);
                Class<?> annotationException = testAnnotation.exception();
                String annotationIgnoredMessage = testAnnotation.ignored();

                if (!annotationIgnoredMessage.equals(Test.DEFAULT_IGNORED)) {
                    results.add(new IgnoredTestResult(method.getName(), annotationIgnoredMessage));
                } else {
                    results.add(invokeTestMethod(method, annotationException));
                }
            }
            for (Method method : afterClassMethods) {
                method.invoke(instance);
            }
        } catch (InvocationTargetException | IllegalAccessException exception) {
            throw new MyJUnitException("A problem occur while invoking one of the annotated methods", exception);
        }
        return results;
    }

    @NotNull
    private TestResult invokeTestMethod(@NotNull Method method, @NotNull Class<?> annotationException) throws InvocationTargetException, IllegalAccessException {
        for (Method beforeMethod : beforeMethods) {
            beforeMethod.invoke(instance);
        }

        InvocationTargetException exception = null;
        long millisElapsed = 0;
        try {
            millisElapsed = System.currentTimeMillis();
            method.invoke(instance);
            millisElapsed = System.currentTimeMillis() - millisElapsed;
        } catch (InvocationTargetException invocationException) {
            millisElapsed = System.currentTimeMillis() - millisElapsed;
            exception = invocationException;
        }

        TestResult testResult;
        if ((exception == null && annotationException.equals(Test.DefaultException.class))
                || (exception != null && exception.getTargetException().getClass().equals(annotationException))) {
            testResult = new RunTestResult(TestResult.TestResultState.SUCCESS, method.getName(), millisElapsed);
        } else {
            testResult = new RunTestResult(TestResult.TestResultState.FAIL, method.getName(), millisElapsed);
        }
        for (Method afterMethod : afterMethods) {
            afterMethod.invoke(instance);
        }
        return testResult;
    }

    private void initMethods() throws MyJUnitException {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (methodHasMJUnitAnnotation(method) && method.getParameterCount() != 0) {
                throw new MyJUnitException("method " + method.getName() + " should have no parameters");
            }
            if (method.getAnnotation(Test.class) != null) {
                testMethods.add(method);
            }
            if (method.getAnnotation(BeforeClass.class) != null) {
                beforeClassMethods.add(method);
            }
            if (method.getAnnotation(AfterClass.class) != null) {
                afterClassMethods.add(method);
            }
            if (method.getAnnotation(Before.class) != null) {
                beforeMethods.add(method);
            }
            if (method.getAnnotation(After.class) != null) {
                afterMethods.add(method);
            }
        }
    }

    private static class IgnoredTestResult implements TestResult {
        private static final TestResultState STATE = TestResultState.IGNORED;
        private final String methodName;
        private final String reason;

        private IgnoredTestResult(@NotNull String methodName, @NotNull String reason) {
            this.methodName = methodName;
            this.reason = reason;
        }

        @Override
        @NotNull
        public TestResultState getState() {
            return STATE;
        }

        @Override
        @NotNull
        public String getMessage() {
            return "Test " + methodName + " disabled. The reason is " + reason + '.';
        }
    }

    private static class RunTestResult implements TestResult {
        private final TestResultState state;
        private final String methodName;
        private final long millisElapsed;

        private RunTestResult(@NotNull TestResultState state, @NotNull String methodName, long millisElapsed) {
            if (state != TestResultState.SUCCESS && state != TestResultState.FAIL) {
                throw new IllegalArgumentException("State must be equal to SUCCESS or FAIL");
            }
            this.state = state;
            this.methodName = methodName;
            this.millisElapsed = millisElapsed;
        }

        @Override
        @NotNull
        public TestResultState getState() {
            return state;
        }

        @Override
        @NotNull
        public String getMessage() {
            return "Test " + methodName + ": " + state.toString() + ", time elapsed: " + millisElapsed + " ms.";
        }
    }
}
