package ru.hse.anstkras.threadpool;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ThreadPoolTest {
    @Test
    void simpleTestForOneThread() {
        assertDoesNotThrow(() -> {
            var threadPool = new ThreadPool(1);
            LightFuture<Integer> lightFuture1 = threadPool.submit(() -> 5);
            while (!lightFuture1.isReady()) {
                ;
            }
            assertEquals(5, (int) lightFuture1.get());
            threadPool.shutdown();
        });
    }

    @Test
    void simpleTestForManyThreads() {
        assertDoesNotThrow(() -> {
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
        });
    }

    @Test
    void simpleTestForThenApply() {
        assertDoesNotThrow(() -> {
            var threadPool = new ThreadPool(1);
            LightFuture<Integer> future = threadPool.submit(() -> 42);
            LightFuture<Integer> futureThen = future.thenApply(value -> value + 137);
            assertEquals(179, (int) futureThen.get());
            assertTrue(future.isReady());
            assertEquals(42, (int) future.get());
            threadPool.shutdown();
        });
    }

    @Test
    void severalThenApply() {
        assertDoesNotThrow(() -> {
            var threadPool = new ThreadPool(1);
            LightFuture<Integer> future = threadPool.submit(() -> 42);
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
        });
    }

    @Test
    void throwLightFutureException() {
        var threadPool = new ThreadPool(1);
        LightFuture<Integer> future = threadPool.submit(() -> {
            throw new NullPointerException();
        });
        assertThrows(LightExecutionException.class, future::get);
        threadPool.shutdown();
    }

    @Test
    void testIsShutDown() {
        var threadPool = new ThreadPool(2);
        threadPool.shutdown();
        assertTrue(threadPool.isShutDown());
    }

    @Test
    void testGetThreadsNumber() {
        var threadPool = new ThreadPool(10);
        assertEquals(10, threadPool.getThreadsNumber());
    }

    @Test
    void thenApplyWithSleep() {
        assertDoesNotThrow(() -> {
            var threadPool = new ThreadPool(1);
            LightFuture<Integer> future = threadPool.submit(() -> {
                try {
                    Thread.sleep(1000);
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
        });
    }

    @Test
    void threadPoolHasNThreads() {
        assertDoesNotThrow(() -> {
            var threadPool = new ThreadPool(10);
            List<LightFuture<Integer>> futures = new ArrayList<>();
            long start = System.currentTimeMillis();
            for (int i = 0; i < 10; i++) {
                futures.add(threadPool.submit(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                    }
                    return 0;
                }));
            }
            for (LightFuture<Integer> future : futures) {
                future.get();
            }
            long end = System.currentTimeMillis();
            assertTrue(end - start < 1500);
            threadPool.shutdown();
        });
    }

    @Test
    void waitForFutureInOtherThreads() {
        assertDoesNotThrow(() -> {
            var threadPool = new ThreadPool(5);
            LightFuture<String> future = threadPool.submit(() -> {
                try {
                    Thread.sleep(1000);
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
        });
    }

    @Test
    void tryCreateThreadPoolWithNegativeThreadsNumber() {
        assertThrows(IllegalArgumentException.class, () -> new ThreadPool(-42));
    }

    @Test
    void tryAddTaskToShutDownThreadPool() {
        var threadPool = new ThreadPool(1);
        threadPool.shutdown();
        assertThrows(IllegalStateException.class, () -> threadPool.submit(() -> 42));
    }
}