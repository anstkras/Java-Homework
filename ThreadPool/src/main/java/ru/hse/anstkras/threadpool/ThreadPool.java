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
            addTask(threadPoolTask);
            return threadPoolTask;
        }
    }

    private <R> void addTask(ThreadPoolTask<R> task) {
        tasks.add(task);
        tasks.notify();
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

    private class ThreadPoolTask<R> implements LightFuture<R>, Runnable {
        private final Supplier<R> supplier;
        private final List<ThreadPoolTask<?>> children = new ArrayList<>();
        private R result;
        private volatile boolean isReady = false;

        private ThreadPoolTask(Supplier<R> supplier) {
            this.supplier = supplier;
        }

        @Override
        synchronized public boolean isReady() {
            return isReady; // TODO is synchronized here necessary?
        }

        @Override
        synchronized public R get() {
            if (!isReady) {
                run();
            }
            return result;

        }

        @Override
        public <R1> LightFuture<R1> thenApply(Function<R, R1> function) {
            synchronized (children) {
                ThreadPoolTask<R1> task = new ThreadPoolTask<>(() -> function.apply(get()));
                children.add(task);
                return task;
            }
        }

        @Override
        synchronized public void run() {
            result = supplier.get();
            isReady = true;
            for (var child : children) {
                addTask(child);
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
                task.run();
            }
        }
    }
}
