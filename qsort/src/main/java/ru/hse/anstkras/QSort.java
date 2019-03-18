package ru.hse.anstkras;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

/** Implements multithreaded quick sort algorithm */
public class QSort {
    private final static int DEFAULT_SIZE_LIMIT = 10;
    private final int sizeLimit;

    private final ExecutorService threadPool;

    /**
     * Initializes thread pool for quick sort with default values, default value for threads number is
     * obtained by calling {@code Runtime.getRuntime().availableProcessors()}
     */
    public QSort() {
        this(DEFAULT_SIZE_LIMIT, Runtime.getRuntime().availableProcessors());
    }

    /**
     * Initializes thread pool for quick sort
     *
     * @param sizeLimit     defines the number of elements in subarray to sort without creating new threads
     * @param threadsNumber defines the number of threads in thread pool
     */
    public QSort(int sizeLimit, int threadsNumber) {
        this.sizeLimit = sizeLimit;
        threadPool = Executors.newFixedThreadPool(threadsNumber);
    }

    /** Sorts the array  using multithreaded quick sort algorithm */
    public void sort(@NotNull int[] array) {
        final Phaser phaser = new Phaser(1);
        phaser.register();
        threadPool.execute(new QSortRunnable(0, array.length - 1, array, phaser));
        phaser.arriveAndAwaitAdvance();
    }

    /** Shuts down the thread pool used by quick sort algorithm */
    public void shutDown() {
        threadPool.shutdown();
    }

    private class QSortRunnable implements Runnable {
        private final int start;
        private final int end;
        private final int[] array;
        private final Phaser phaser;


        private QSortRunnable(int start, int end, int[] array, @NotNull Phaser phaser) {
            this.start = start;
            this.end = end;
            this.array = array;
            this.phaser = phaser;
        }

        @Override
        public void run() {
            quickSort(start, end);
            phaser.arriveAndDeregister();
        }

        private void quickSort(int start, int end) {
            if (start >= end) {
                return;
            }
            if (end - start + 1 <= sizeLimit) {
                Arrays.sort(array, start, end + 1);
                return;
            }
            int i = start;
            int j = end;
            int pivot = array[start + (end - start) / 2];
            while (i <= j) {
                while (array[i] < pivot) {
                    i++;
                }
                while (array[j] > pivot) {
                    j--;
                }
                if (i <= j) {
                    int temporary = array[i];
                    array[i] = array[j];
                    array[j] = temporary;
                    i++;
                    j--;
                }
            }
            if (start < j) {
                phaser.register();
                threadPool.execute(new QSortRunnable(start, j, array, phaser));
            }
            if (i < end) {
                phaser.register();
                threadPool.execute(new QSortRunnable(i, end, array, phaser));
            }
        }
    }
}
