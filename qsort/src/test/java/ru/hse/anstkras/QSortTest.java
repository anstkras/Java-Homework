package ru.hse.anstkras;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QSortTest {
    private QSort qSort;

    @BeforeEach
    void init() {
        qSort = new QSort();
    }

    @Test
    void sortEmptyArray() {
        int[] array = {};
        qSort.sort(array);
        int[] expectedArray = {};
        assertArrayEquals(expectedArray, array);
    }

    @Test
    void sortOneElement() {
        int[] array = {1, 2};
        qSort.sort(array);
        int[] expectedArray = {1, 2};
        assertArrayEquals(expectedArray, array);
    }

    @Test
    void sortSomeElements() {
        int[] array = {1, 4, 5, 7, -100, 3, 2, 43, 43, 12, 43, 76, 12, 34, 1, 0, 1};
        qSort.sort(array);
        int[] expectedArray = {-100, 0, 1, 1, 1, 2, 3, 4, 5, 7, 12, 12, 34, 43, 43, 43, 76};
        assertArrayEquals(expectedArray, array);
    }

    @Test
    void sortManyRandomElements() {
        var random = new Random(1);
        int[] array = random.ints(-10000, 10000).limit(10000).toArray();
        qSort.sort(array);
        for (int i = 0; i < array.length - 1; i++) {
            assertTrue(array[i] <= array[i + 1]);
        }
    }
}