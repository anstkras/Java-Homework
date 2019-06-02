package ru.hse.anstkras.myjunit;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Tester {
    public static void test(Class<?> clazz) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        Method[] methods = clazz.getDeclaredMethods();
        List<Method> beforeClassMethods = new ArrayList<>();
        List<Method> afterClassMethods = new ArrayList<>();
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        for (Method method : methods) {
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
        Constructor<?> constructor = clazz.getConstructor();
        var instance = constructor.newInstance();
        for (Method method : beforeClassMethods) {
            method.invoke(instance);
        }
        for (Method method : testMethods) {
            for (Method beforeMethod : beforeMethods) {
                beforeMethod.invoke(instance);
            }
            method.invoke(instance);
            for (Method afterMethod : afterMethods) {
                afterMethod.invoke(instance);
            }
        }
        for (Method method : afterClassMethods) {
            method.invoke(instance);
        }
    }
}
