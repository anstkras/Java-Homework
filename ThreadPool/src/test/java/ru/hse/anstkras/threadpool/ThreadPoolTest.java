package ru.hse.anstkras.threadpool;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThreadPoolTest {
    @Test
    void simpleTestForOneThread() {
        var threadPool = new ThreadPool(1);
        LightFuture<Integer> lightFuture1 = threadPool.submit(() -> 5);
        while (!lightFuture1.isReady()) {
            ;
        }
        assertEquals(5, (int) lightFuture1.get());
        threadPool.shutdown();
    }

    @Test
    void simpleTestForManyThreads() {
        final int SIZE = 100;
        var threadPool = new ThreadPool(SIZE);
        List<LightFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            final int value = i;
            futures.add(threadPool.submit(() -> value));
        }
        for (int i = 0; i < SIZE; i++) {
            assertEquals(i, (int) futures.get(i).get());
        }
        threadPool.shutdown();
    }
}