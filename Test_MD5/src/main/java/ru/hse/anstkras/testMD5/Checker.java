package ru.hse.anstkras.testMD5;

import java.nio.file.Path;

/**
 * Introduce an interface for checking directory hash sums
 */
public interface Checker {
    String checkSum(Path path);
}
