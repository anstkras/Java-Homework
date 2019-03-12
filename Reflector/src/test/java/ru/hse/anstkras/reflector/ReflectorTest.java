package ru.hse.anstkras.reflector;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ReflectorTest {
    @Test
    void test() throws IOException {
        Reflector.printStructure(TestClass.class);
    }

    @Test
    void testDiffClasses() {
        Reflector.diffClasses(A.class, B.class);
    }
}