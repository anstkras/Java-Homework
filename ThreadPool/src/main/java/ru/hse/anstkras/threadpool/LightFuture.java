package ru.hse.anstkras.threadpool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * Represents a result of computation that could not be completed yet.
 *
 * @param <V> specify the type of computation result
 */
public interface LightFuture<V> {
    /** Check if the computation is completed and result is ready */
    boolean isReady();

    /**
     * If computation is completed than returns result
     * otherwise waits until computation is completed
     *
     * @return the result of computation
     * @throws LightExecutionException in case of task computation throws an exception
     */
    @Nullable
    V get() throws LightExecutionException;

    /**
     * Constructs new LightFuture that represents an application of
     * the given function to computed result of the task
     */
    @NotNull
    <R> LightFuture<R> thenApply(@NotNull Function<? super V, R> function);
}
