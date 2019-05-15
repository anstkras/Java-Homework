package ru.hse.anstkras.testMD5;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CheckerMD5Test {
    @Test
    void testCheckersComputeTheSame() throws IOException {
        Path dir = Files.createTempDirectory("testDir");
        Path file = Files.createTempFile(dir, "testFile", "123");
        try(OutputStream outputStream = new FileOutputStream(file.toFile())) {
            outputStream.write(123);
        }
        Path oneMoreDir = Files.createTempDirectory(dir, "oneMoreDir");
        Path oneMoreFile = Files.createTempFile(oneMoreDir, "123", "123");
        CheckerMD5MultiThreaded checkerMD5 = new CheckerMD5MultiThreaded();
        CheckerMD5SingleThreaded checkerMD5SingleThreaded = new CheckerMD5SingleThreaded();
        assertEquals(checkerMD5.checkSum(dir), checkerMD5SingleThreaded.checkSum(dir));
    }
}