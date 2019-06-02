package ru.hse.anstkras.myjunit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Tester {
    public static List<TestResult> test(Class<?> clazz) throws MyJUnitException {
        Object instance;
        try {
            Constructor<?> constructor = clazz.getConstructor();
            instance = constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException exception) {
            throw new MyJUnitException("No accessible constructor with no parameters", exception);
        }

        Method[] methods = clazz.getDeclaredMethods();
        List<Method> beforeClassMethods = new ArrayList<>();
        List<Method> afterClassMethods = new ArrayList<>();
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();

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

        List<TestResult> results = new ArrayList<>();
        try {
            for (Method method : beforeClassMethods) {
                method.invoke(instance);
            }
            for (Method method : testMethods) {
                for (Method beforeMethod : beforeMethods) {
                    beforeMethod.invoke(instance);
                }

                Test testAnnotation = method.getAnnotation(Test.class);
                Class<?> annotationException = testAnnotation.exception();
                String annotationIgnored = testAnnotation.ignored();
                if (!annotationIgnored.equals(Test.DEFAULT_IGNORED)) {
                    results.add(new IgnoredTestResult(method.getName(), annotationIgnored));
                } else {
                    InvocationTargetException exception = null;
                    try {
                        method.invoke(instance);
                    } catch (InvocationTargetException invocationException) {
                        exception = invocationException;
                    }
                    if ((exception == null && annotationException.equals(Test.DefaultException.class))
                            || (exception.getTargetException().getClass().equals(annotationException))) {
                        results.add(new RunTestResult(TestResult.TestResultState.SUCCESS, method.getName(), 0));
                    } else {
                        results.add(new RunTestResult(TestResult.TestResultState.FAIL, method.getName(), 0));
                    }
                }

                for (Method afterMethod : afterMethods) {
                    afterMethod.invoke(instance);
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

    private static boolean methodHasMJUnitAnnotation(Method method) {
        return method.getAnnotation(Test.class) != null || method.getAnnotation(BeforeClass.class) != null
                || method.getAnnotation(Before.class) != null || method.getAnnotation(AfterClass.class) != null
                ||method.getAnnotation(After.class) != null;
    }

    private static class IgnoredTestResult implements TestResult {
        private static final TestResultState STATE = TestResultState.IGNORED;
        private final String methodName;
        private final String reason;

        private IgnoredTestResult(String methodName, String reason) {
            this.methodName = methodName;
            this.reason = reason;
        }

        @Override
        public TestResultState getState() {
            return STATE;
        }

        @Override
        public String getMessage() {
            return "Test " + methodName + " disabled. The reason is " + reason + '.';
        }
    }

    private static class RunTestResult implements TestResult {
        private final TestResultState state;
        private final String methodName;
        private final long millisElapsed;

        private RunTestResult(TestResultState state, String methodName, long millisElapsed) {
            if (state != TestResultState.SUCCESS && state != TestResultState.FAIL) {
                throw new IllegalArgumentException("State must be equal to SUCCESS or FAIL");
            }
            this.state = state;
            this.methodName = methodName;
            this.millisElapsed = millisElapsed;
        }

        @Override
        public TestResultState getState() {
            return state;
        }

        @Override
        public String getMessage() {
            return "Test " + methodName + ": " + state.toString() + ", time elapsed: " + String.valueOf(millisElapsed) + " ms.";
        }
    }
}
