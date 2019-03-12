package ru.hse.anstkras.reflector;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReflectorTest {
    @Test
    void test() throws IOException, InterruptedException {
        Reflector.printStructure(TestClass.class);
        ProcessBuilder pb = new ProcessBuilder("javac", "TestClass.java");
        Process process = pb.start();
        int errCode = process.waitFor();
        assertEquals(0, errCode);

        Path generatedFile = Paths.get("TestClass.java");
        Path expectedFile = Paths.get("TestClassExpected.java");
        byte[] generatedFileBytes = Files.readAllBytes(generatedFile);
        byte[] expectedFileBytes = Files.readAllBytes(expectedFile);
        assertArrayEquals(expectedFileBytes, generatedFileBytes);
    }

    @Test
    void testDiffClasses() throws IOException {
        var stringWriter = new StringWriter();
        Reflector.diffClasses(A.class, B.class, stringWriter);
        stringWriter.close();
        String correctResult = "A contains fields that B does not contain:\n" +
                "double c;\n" +
                "public static int a;\n" +
                "\n" +
                "B contains fields that A does not contain:\n" +
                "private static int a;\n" +
                "\n" +
                "A contains methods that B does not contain:\n" +
                "static java.lang.Integer method2()\n" +
                "<E> double method3()\n" +
                "\n" +
                "B contains methods that A does not contain:\n" +
                "public static java.lang.Integer method2()\n" +
                "\n";
        assertEquals(stringWriter.toString(), correctResult);
    }

    @Test
    void diffClassesForEqualClasses() throws IOException {
        var stringWriter = new StringWriter();
        Reflector.diffClasses(A.class, AA.class, stringWriter);
        stringWriter.close();
        String correctResult = "Classes are not different\n";
        assertEquals(stringWriter.toString(), correctResult);
    }
}