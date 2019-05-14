package ru.hse.anstkras.threadpool;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {
    private static final int TESTS_NUMBER = 100;

    @RepeatedTest(TESTS_NUMBER)
    void simpleTestForOneThread() throws LightExecutionException, InterruptedException {
        var threadPool = new ThreadPool(1);
        LightFuture<Integer> lightFuture = threadPool.submit(() -> 5);
        assertEquals(5, (int) lightFuture.get());
        threadPool.shutdown();
    }

    @RepeatedTest(TESTS_NUMBER)
    void simpleTestForManyThreads() throws InterruptedException, LightExecutionException {
        final int size = 100;
        var threadPool = new ThreadPool(size);
        List<LightFuture<Integer>> futures = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            final int value = i;
            futures.add(threadPool.submit(() -> value));
        }
        for (int i = 0; i < size; i++) {
            assertEquals(i, (int) futures.get(i).get());
        }
        threadPool.shutdown();
    }

    @RepeatedTest(TESTS_NUMBER)
    void simpleTestForThenApply() throws InterruptedException, LightExecutionException {
        var threadPool = new ThreadPool(1);
        LightFuture<Integer> future = threadPool.submit(() -> 42);
        LightFuture<Integer> futureThen = future.thenApply(value -> value + 137);
        assertEquals(179, (int) futureThen.get());
        assertTrue(future.isReady());
        assertEquals(42, (int) future.get());
        threadPool.shutdown();
    }

    @RepeatedTest(TESTS_NUMBER)
    void severalThenApply() throws InterruptedException, LightExecutionException {
        var threadPool = new ThreadPool(1);
        LightFuture<Integer> future = threadPool.submit(() -> 42);
        var futures = new ArrayList<LightFuture<Integer>>();
        for (int i = 0; i < 100; i++) {
            final int newValue = i;
            futures.add(future.thenApply(value -> value + newValue));
        }
        for (int i = 0; i < 100; i++) {
            assertEquals(42 + i, (int) futures.get(i).get());
        }
        assertTrue(future.isReady());
        assertEquals(42, (int) future.get());
        threadPool.shutdown();
    }

    @RepeatedTest(TESTS_NUMBER)
    void throwLightFutureException() throws InterruptedException {
        var threadPool = new ThreadPool(1);
        LightFuture<Integer> future = threadPool.submit(() -> {
            throw new NullPointerException();
        });
        assertThrows(LightExecutionException.class, future::get);
        threadPool.shutdown();
    }

    @Test
    void testIsShutDown() throws InterruptedException {
        var threadPool = new ThreadPool(2);
        threadPool.shutdown();
        assertTrue(threadPool.isShutDown());
    }

    @Test
    void testGetThreadsNumber() {
        var threadPool = new ThreadPool(10);
        assertEquals(10, threadPool.getThreadsNumber());
    }

    @RepeatedTest(TESTS_NUMBER)
    void thenApplyWithSleep() throws InterruptedException, LightExecutionException {
        var threadPool = new ThreadPool(1);
        LightFuture<Integer> future = threadPool.submit(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            return 42;
        });
        var futures = new ArrayList<LightFuture<Integer>>();
        for (int i = 0; i < 1000; i++) {
            final int newValue = i;
            futures.add(future.thenApply(value -> value + newValue));
        }
        for (int i = 0; i < 1000; i++) {
            assertEquals(42 + i, (int) futures.get(i).get());
        }
        assertTrue(future.isReady());
        assertEquals(42, (int) future.get());
        threadPool.shutdown();
    }

    @RepeatedTest(TESTS_NUMBER)
    void threadPoolHasNThreads() throws InterruptedException {
        var threadPool = new ThreadPool(10);
        final List<Integer> list = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            threadPool.submit(() -> {
                synchronized (list) {
                    list.add(1);
                    list.notifyAll();
                }
                try {
                    Thread.sleep(Long.MAX_VALUE);
                } catch (InterruptedException ignored) {
                }
                return null;
            });
        }

        synchronized (list) {
            while (list.size() != 10) {
                list.wait();
            }
        }
        assertEquals(10, list.size());
        threadPool.shutdown();
    }

    @RepeatedTest(TESTS_NUMBER)
    void waitForFutureInOtherThreads() throws LightExecutionException {
        var threadPool = new ThreadPool(5);
        LightFuture<String> future = threadPool.submit(() -> {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
            return "42";
        });
        List<LightFuture<?>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            futures.add(threadPool.submit(() -> {
                try {
                    return future.get().length();
                } catch (LightExecutionException ignored) {
                }
                return null;
            }));
        }
        for (LightFuture<?> futureThen : futures) {
            assertEquals(2, futureThen.get());
        }
    }

    @Test
    void tryCreateThreadPoolWithNegativeThreadsNumber() {
        assertThrows(IllegalArgumentException.class, () -> new ThreadPool(-42));
    }

    @Test
    void tryAddTaskToShutDownThreadPool() throws InterruptedException {
        var threadPool = new ThreadPool(1);
        threadPool.shutdown();
        assertThrows(IllegalStateException.class, () -> threadPool.submit(() -> 42));
    }
}