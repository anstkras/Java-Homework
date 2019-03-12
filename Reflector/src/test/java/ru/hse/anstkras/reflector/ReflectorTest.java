package ru.hse.anstkras.reflector;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReflectorTest {
    @Test
    void test() throws IOException {
        Reflector.printStructure(TestClass.class);
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
}