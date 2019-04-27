package ru.hse.anstkras.threadpool;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPool {
    private final int threadsNumber;
    private final Queue<ThreadPoolTask> tasks = new ArrayDeque<>();
    private final List<Thread> threads;
    private boolean isShutDown = false;

    public ThreadPool(int threadsNumber) {
        this.threadsNumber = threadsNumber;
        threads = new ArrayList<>();
        for (int i = 0; i < threadsNumber; i++) {
            threads.add(new LightThread());
        }
        for (Thread thread : threads) {
            thread.start();
        }
    }

    public <R> LightFuture<R> submit(Supplier<R> task) {
        if (task == null) {
            throw new NullPointerException();
        }
        synchronized (tasks) {
            var threadPoolTask = new ThreadPoolTask<>(task);
            tasks.add(threadPoolTask);
            tasks.notify();
            return threadPoolTask;
        }
    }

    public void shutdown() {
        isShutDown = true;
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    public boolean isShutDown() {
        return isShutDown;
    }

    private static class ThreadPoolTask<R> implements LightFuture<R> {
        private final Supplier<R> supplier;

        private R result;
        private volatile boolean isReady = false;

        private ThreadPoolTask(Supplier<R> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            synchronized (supplier) {
                return isReady;
            }
        }

        @Override
        public R get() {
            synchronized (supplier) {
                if (!isReady) {
                    compute();
                }
                return result;
            }
        }

        @Override
        public <R1> LightFuture<R1> thenApply(Function<R, LightFuture<R1>> function) {
            return null;
        }

        private void compute() {
            synchronized (supplier) {
                result = supplier.get();
                isReady = true;
            }
        }
    }

    private class LightThread extends Thread {

        @Override
        public void run() {
            ThreadPoolTask task;
            while (!interrupted()) {
                synchronized (tasks) {
                    while (tasks.isEmpty()) {
                        try {
                            tasks.wait();
                        } catch (InterruptedException e) {

                        }
                    }
                    task = tasks.remove();
                }
                task.compute();
            }
        }
    }
}
