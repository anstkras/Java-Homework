package ru.hse.anstkras.threadpool;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

public class ThreadPool {
    private final int threadsNumber;
    private final Queue<ThreadPoolTask> tasks = new ArrayDeque<>();
    private final Thread[] threads;
    private boolean isShutDown = false;

    public ThreadPool(int threadsNumber) {
        if (threadsNumber <= 0) {
            throw new IllegalArgumentException("Threads number should be a positive integer");
        }
        this.threadsNumber = threadsNumber;
        threads = new Thread[threadsNumber];
        for (int i = 0; i < threadsNumber; i++) {
            threads[i] = new LightThread();
            threads[i].start();
        }
    }

    @NotNull
    public <R> LightFuture<R> submit(@NotNull Supplier<R> task) {
        if (isShutDown) {
            throw new IllegalStateException("Thread pool is shut down");
        }

        var threadPoolTask = new ThreadPoolTask<>(task);
        addTask(threadPoolTask);
        return threadPoolTask;

    }

    private <R> void addTask(ThreadPoolTask<R> task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
    }

    public void shutdown() {
        if (isShutDown) {
            return;
        }
        isShutDown = true;
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    public boolean isShutDown() {
        return isShutDown;
    }

    private class ThreadPoolTask<R> implements LightFuture<R> {
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
        synchronized public R get() throws LightExecutionException {
            if (!isReady) {
                execute();
            }
            return result;
        }

        @Override
        @NotNull
        public <R1> LightFuture<R1> thenApply(@NotNull Function<R, R1> function) {
            var task = new ThreadPoolTask<>(() -> {
                try {
                    return function.apply(get());
                } catch (LightExecutionException exception) {
                    throw new RuntimeException(exception);
                }
            });

            synchronized (children) {
                children.add(task);
            }
            return task;

        }

        private void execute() throws LightExecutionException {
            try {
                result = supplier.get();
                isReady = true;
                synchronized (children) {
                    for (var child : children) {
                        addTask(child);
                    }
                }
            } catch (Exception exception) {
                throw new LightExecutionException();
            }
        }
    }

    private class LightThread extends Thread {
        @Override
        public void run() {
            ThreadPoolTask task = null;
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
                try {
                    task.execute();
                } catch (LightExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
