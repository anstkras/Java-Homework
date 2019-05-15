package ru.hse.anstkras.testMD5;

import java.nio.file.Path;

public interface Checker {
    String checkSum(Path path);
}
