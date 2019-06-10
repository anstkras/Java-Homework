package ru.hse.anstkras.myjunit;

import org.jetbrains.annotations.NotNull;

/** Represents result of the test with the state and message */
public interface TestResult {
    @NotNull TestResultState getState();

    @NotNull String getMessage();

    enum TestResultState {
        SUCCESS("SUCCESS"),
        FAIL("FAIL"),
        IGNORED("IGNORED");

        private final @NotNull String stringRepresentation;

        TestResultState(@NotNull String toString) {
            this.stringRepresentation = toString;
        }

        public @NotNull String toString() {
            return stringRepresentation;
        }
    }
}
