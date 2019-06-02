package ru.hse.anstkras.myjunit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    String DEFAULT_IGNORED = "ru.hse.anstkras.myjunit.test_default_ignored";

    String ignored() default DEFAULT_IGNORED;

    Class exception() default DefaultException.class;

    final class DefaultException {
    }
}
