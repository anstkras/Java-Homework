package ru.hse.anstkras.threadpool;

import java.util.function.Function;

public interface LightFuture<V> {
    boolean isReady();

    V get();

    <R> LightFuture<R> thenApply(Function<V, LightFuture<R>> function);

}
