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
        try(var outputStream = new FileOutputStream(file.toFile())) {
            outputStream.write(123);
        }
        Path oneMoreDir = Files.createTempDirectory(dir, "oneMoreDir");
        Path oneMoreFile = Files.createTempFile(oneMoreDir, "123", "123");
        var checkerMD5 = new CheckerMD5MultiThreaded();
        var checkerMD5SingleThreaded = new CheckerMD5SingleThreaded();
        assertEquals(checkerMD5.checkSum(dir), checkerMD5SingleThreaded.checkSum(dir));
    }
}