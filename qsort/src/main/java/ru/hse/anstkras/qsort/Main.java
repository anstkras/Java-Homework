package ru.hse.anstkras.qsort;

import java.util.Random;

public class Main {
    private final static int[] threadsNumber = {1, 2, 4, 8, 16, 32, 64, 128, 256};
    private final static int[] sizesOfArrays = {10, 100, 1000, 10000, 100000, 1000000};
    private final static Random random = new Random(1);

    public static void main(String[] args) {
        for (int size : sizesOfArrays) {
            int[] array = random.ints(-10000, 10000).limit(size).toArray();
            long startTime = System.nanoTime();
            quickSort(array, 0, array.length - 1);
            long endTime = System.nanoTime();
            System.out.println("Execution time for one-threaded quick sort = " + (endTime - startTime) / 1000000 + " ms");
        }
        for (int threadNumber : threadsNumber) {
            var qSort = new QSort(50, threadNumber);
            for (int size : sizesOfArrays) {
                int[] array = random.ints(-10000, 10000).limit(size).toArray();
                long startTime = System.nanoTime();
                qSort.sort(array);
                long endTime = System.nanoTime();
                System.out.println("Threads number = " + threadNumber + ", size = " + size + ", execution time = " + (endTime - startTime) / 1000000 + " ms");
            }
            qSort.shutDown();
        }
    }

    private static void quickSort(int[] array, int start, int end) {
        if (start >= end) {
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
            quickSort(array, start, j);
        }
        if (i < end) {
            quickSort(array, i, end);
        }
    }
}
