package ru.hse.anstkras.reflector;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReflectorTest {
    @Test
    void printStructure() throws IOException, InterruptedException {
        var fileWriter = getTestClassWriter();
        Reflector.printStructure(TestClass.class, fileWriter);
        fileWriter.close();
        ProcessBuilder pb = new ProcessBuilder("javac", "tmp/TestClass.java");
        Process process = pb.start();
        int errorCode = process.waitFor();
        assertEquals(0, errorCode);

        Path generatedFile = Paths.get("tmp/TestClass.java");
        Path expectedFile = Paths.get("TestClassExpected.java");
        var generatedLines = Files.lines(generatedFile).sorted().collect(Collectors.toList());
        var expectedLines = Files.lines(expectedFile).sorted().collect(Collectors.toList());

        assertEquals(expectedLines, generatedLines);
        cleanTmp();
    }

    @Test
    void printStructureAndDiffClasses() throws IOException, InterruptedException, ClassNotFoundException {
        FileWriter fileWriter = getTestClassWriter();
        Reflector.printStructure(TestClass.class, fileWriter);
        fileWriter.close();
        ProcessBuilder pb = new ProcessBuilder("javac", "tmp/TestClass.java");
        Process process = pb.start();
        int errorCode = process.waitFor();
        assertEquals(0, errorCode);

        Class<?> clazz = new URLClassLoader(new URL[]{new File("tmp/").toURI().toURL()})
                .loadClass("TestClass");
        var stringWriter = new StringWriter();
        Reflector.diffClasses(TestClass.class, clazz, stringWriter);
        stringWriter.close();
        String correctResult = "Classes are not different\n";
        assertEquals(stringWriter.toString(), correctResult);
        cleanTmp();
    }

    private FileWriter getTestClassWriter() throws IOException {
        File tmpDir = new File("tmp/");
        tmpDir.mkdir();
        File file = new File("tmp/TestClass.java");
        file.createNewFile();
        return new FileWriter(file);
    }

    private void cleanTmp() {
        var tmpDir = new File("tmp/");
        Arrays.stream(tmpDir.listFiles()).forEach(File::delete);
        tmpDir.delete();
    }

    @Test
    void diffClasses() throws IOException {
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