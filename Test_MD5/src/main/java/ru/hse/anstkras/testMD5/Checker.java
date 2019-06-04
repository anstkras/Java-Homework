package ru.hse.anstkras.testMD5;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Introduce an interface for checking directory hash sums
 */
public interface Checker {
    @NotNull
    String checkSum(@NotNull Path path);
}
