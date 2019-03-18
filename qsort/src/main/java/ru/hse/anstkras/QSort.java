package ru.hse.anstkras;

import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class QSort {
    private final static int SIZE_LIMIT = 10;

    private final Phaser phaser = new Phaser(1);

    private final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    private Executor threadPool = Executors.newFixedThreadPool(MAX_THREADS);

    public void quickSort(int[] array) {
        threadPool.execute(new QSortRunnable(0, array.length, array));
        phaser.arriveAndAwaitAdvance();

    }

    private class QSortRunnable implements Runnable {
        private final int start;
        private final int end;
        private final int[] array;

        private QSortRunnable(int start, int end, int[] array) {
            this.start = start;
            this.end = end;
            this.array = array;
        }

        @Override
        public void run() {
            quickSort(start, end);
            phaser.arriveAndDeregister();
        }

        private void quickSort(int start, int end) {
            if (end - start + 1 > SIZE_LIMIT) {
                Arrays.sort(array, start, end);
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
                    int temp = array[i];
                    array[i] = array[j];
                    array[j] = temp;
                    i++;
                    j--;
                }
            }
            if (start < j) {
                phaser.register();
                threadPool.execute(new QSortRunnable(start, j, array));
            }
            if (i < end) {
                phaser.register();
                threadPool.execute(new QSortRunnable(i, end, array));
            }
        }
    }
}
