package ru.hse.anstkras;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

class QSortTest {
    @Test
    void test() {
        int[] array = {1, 2, 6, 4, 2, 1, -1, 10, 2, 4, 1, 2, 6, 7};
        QSort qSort = new QSort();
        qSort.quickSort(array);
        Arrays.stream(array).forEach(System.out::println);
    }
}