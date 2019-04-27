package ru.hse.anstkras.threadpool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface LightFuture<V> {
    boolean isReady();

    @Nullable
    V get() throws LightExecutionException;

    @NotNull
    <R> LightFuture<R> thenApply(@NotNull Function<V, R> function);
}
