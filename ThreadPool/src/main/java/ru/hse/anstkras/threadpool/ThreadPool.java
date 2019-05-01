package ru.hse.anstkras.threadpool;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a thread pool with fixed number of threads
 */
public class ThreadPool {
    private final int threadsNumber;
    private final @NotNull Queue<ThreadPoolTask> tasks = new ArrayDeque<>();
    private final @NotNull Thread[] threads;
    private volatile boolean isShutDown = false;

    /** Creates a thread pool with the given number of threads */
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

    /**
     * Adds the task to the thread pool to be computed and returns a LightFuture
     * representation of the task's result
     *
     * @param task to be computed
     * @param <R>  the type of the task's result
     * @return the representation of task's result
     */
    @NotNull
    public <R> LightFuture<R> submit(@NotNull Supplier<R> task) {
        if (isShutDown) {
            throw new IllegalStateException("Thread pool is shut down");
        }

        var threadPoolTask = new ThreadPoolTask<>(task);
        addTaskToQueue(threadPoolTask);
        return threadPoolTask;
    }

    /**
     * Stops the thread pool's threads
     */
    public void shutdown() {
        if (isShutDown) {
            return;
        }
        isShutDown = true;
        for (Thread thread : threads) {
            thread.interrupt();
        }
    }

    /** Returns the threads number in the thread pool */
    public int getThreadsNumber() {
        return threadsNumber;
    }

    /** Checks if the thread pool is shut down */
    public boolean isShutDown() {
        return isShutDown;
    }

    private <R> void addTaskToQueue(@NotNull ThreadPoolTask<R> task) {
        synchronized (tasks) {
            tasks.add(task);
            tasks.notify();
        }
    }

    private class ThreadPoolTask<R> implements LightFuture<R> {
        private final @NotNull Supplier<R> supplier;
        private final @NotNull List<ThreadPoolTask<?>> children = new ArrayList<>();
        private @Nullable R result;
        private volatile boolean isReady = false;
        private volatile LightExecutionException exception;

        private ThreadPoolTask(@NotNull Supplier<R> supplier) {
            this.supplier = supplier;
        }

        @Override
        public boolean isReady() {
            return isReady;
        }

        @Override
        @Nullable
        public R get() throws LightExecutionException {
            if (!isReady) {
                synchronized (this) {
                    while (!isReady) {
                        try {
                            wait();
                        } catch (InterruptedException exception) {
                            throw new LightExecutionException(exception);
                        }
                    }
                }
            }
            if (exception != null) {
                throw exception;
            }
            return result;
        }

        @Override
        @NotNull
        public <T> LightFuture<T> thenApply(@NotNull Function<? super R, T> function) {
            var task = new ThreadPoolTask<>(() -> {
                try {
                    return function.apply(get());
                } catch (LightExecutionException exception) {
                    throw new RuntimeException(exception);
                }
            });

            if (isReady) {
                addTaskToQueue(task);
            } else {
                synchronized (children) {
                    children.add(task);
                }
            }
            return task;
        }

        // compute the task's result
        private void execute() {
            try {
                result = supplier.get();
                synchronized (children) {
                    for (var child : children) {
                        addTaskToQueue(child);
                    }
                }
            } catch (Exception exception) {
                this.exception = new LightExecutionException(exception);
            }

            isReady = true;
            synchronized (this) {
                notifyAll();
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
                        } catch (InterruptedException ignored) {
                        }
                    }
                    task = tasks.remove();
                }
                task.execute();
            }
        }
    }
}
