package ru.hse.anstkras.myjunit;

public interface TestResult {
    TestResultState getState();

    String getMessage();

    enum TestResultState {
        SUCCESS("SUCCESS"),
        FAIL("FAIL"),
        IGNORED("IGNORED");

        private String stringRepresentation;

        TestResultState(String toString) {
            this.stringRepresentation = toString;
        }

        public String toString() {
            return stringRepresentation;
        }
    }
}
