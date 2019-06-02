package ru.hse.anstkras.myjunit;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner SCANNER = new Scanner(System.in);

    private Main() {
    }

    @NotNull
    private static String getClassName() {
        System.out.println("Enter class name for test:");
        return SCANNER.next();
    }

    public static void main(String[] args) {
        String className = getClassName();
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            System.out.println("Class was not found");
            return;
        }
        Tester tester;
        List<TestResult> results;
        try {
            tester = new Tester(clazz);
            results = tester.runTests();
        } catch (MyJUnitException e) {
            System.out.println("Something went wrong while testing: " + e.getMessage());
            return;
        }
        for (TestResult testResult : results) {
            System.out.println(testResult.getMessage());
        }
    }
}
