package ru.hse.anstkras.myjunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Method annotated by {@code Test} is invoked during testing unless
 * {@code ignored} is set. The {@code exception} parameter is checked to be
 * equal to an exception that method actually throws
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    /** Default value for ignored that means that test is not ignored */
    String DEFAULT_IGNORED = "ru.hse.anstkras.myjunit.test_default_ignored";

    /** If set means that the annotated as a test method should be ignored with specified reason */
    String ignored() default DEFAULT_IGNORED;

    /**
     * If set means that the annotated method must throw specified exception,
     * otherwise the annotated method must not throw any exceptions
     */
    Class exception() default DefaultException.class;

    /** Default value for exception that means that test does not throw any exceptions */
    final class DefaultException {
    }
}

