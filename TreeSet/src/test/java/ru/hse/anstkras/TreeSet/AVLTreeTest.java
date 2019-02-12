package ru.hse.anstkras.TreeSet;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    @Test
    void addTheSameValue() {
        var tree = new AVLTree<Integer>();
        assertTrue(tree.add(1));
        assertFalse(tree.add(1));
    }

    @Test
    void removeTheSameValue() {
        var tree = new AVLTree<Integer>();
        assertTrue(tree.add(1));
        assertTrue(tree.remove(1));
        assertFalse(tree.remove(1));
    }

    @Test
    void addRemoveThanAddAgain() {
        var tree = new AVLTree<Integer>();
        assertTrue(tree.add(1));
        assertTrue(tree.remove(1));
        assertTrue(tree.add(1));
    }

    @Test
    void ascendingIterator() {
        var tree = new AVLTree<Integer>();
        Integer[] array = {4, 1, 100, 2, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        Integer[] newArray = new Integer[7];
        var it = tree.iterator();
        int i = 0;
        while (it.hasNext()) {
            newArray[i++] = it.next();
        }
        Arrays.sort(array);
        assertArrayEquals(array, newArray);
    }

    @Test
    void descendingIterator() {
        AVLTree<Integer> tree = new AVLTree<>();
        Integer[] array = {4, 1, 100, 2, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        Integer[] newArray = new Integer[7];
        var it = tree.descendingIterator();
        int i = 0;
        while (it.hasNext()) {
            newArray[i++] = it.next();
        }
        Arrays.sort(array, Collections.reverseOrder());
        assertArrayEquals(array, newArray);
    }

    @Test
    void descendingSet() {
        AVLTree<Integer> tree = new AVLTree<>();
        Integer[] array = {4, 1, 100, 2, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        Integer[] newArray = new Integer[7];
        MyTreeSet<Integer> reverseTree = tree.descendingSet();
        reverseTree.toArray(newArray);
        Arrays.sort(array, Collections.reverseOrder());
        assertArrayEquals(array, newArray);
    }

    @Test
    void removeByValue() {
        AVLTree<Integer> tree = new AVLTree<>();
        assertTrue(tree.add(1));
        assertTrue(tree.remove(1));
    }

    @Test
    void removeWrongValue() {
        AVLTree<Integer> tree = new AVLTree<>();
        assertTrue(tree.add(1));
        assertThrows(ClassCastException.class, () -> tree.remove("1"));
    }

    @Test
    void removeAndAddMultipleTimes() {
        var tree = getIntegerFilledTree();
        assertTrue(tree.remove(100));
        assertTrue(tree.remove(23));
        assertFalse(tree.remove(11));
        assertFalse(tree.remove(23));
        assertTrue(tree.add(42));
        assertTrue(tree.remove(-21));
        assertTrue(tree.add(23));
    }

    @Test
    void contains() {
        AVLTree<Integer> tree = new AVLTree<>();
        for (int i = 0; i < 10; i++) {
            tree.add(i);
        }
        for (int i = 0; i < 10; i++) {
            assertTrue(tree.contains(i));
        }
    }

    @Test
    void first() {
        AVLTree<Integer> tree = new AVLTree<>();
        Integer[] array = {-10, 4, 1, 100, 2, -21, 88, 32, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        assertEquals(-21, (int) tree.first());
    }

    @Test
    void last() {
        AVLTree<Integer> tree = new AVLTree<>();
        Integer[] array = {-10, 4, 1, 100, 2, -21, 88, 32, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        assertEquals(100, (int) tree.last());
    }

    @Test
    void lower() {
        AVLTree<Integer> tree = new AVLTree<>();
        Integer[] array = {-10, 4, 1, 100, 2, -21, 88, 32, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        assertEquals(12, (int) tree.lower(23));
    }

    @Test
    void higher() {
        AVLTree<Integer> tree = new AVLTree<>();
        Integer[] array = {-10, 4, 1, 100, 2, -21, 88, 32, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        assertEquals(100, (int) tree.higher(88));
    }

    @Test
    void ceiling() {
        AVLTree<Integer> tree = new AVLTree<>();
        Integer[] array = {-10, 4, 1, 100, 2, -21, 88, 32, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        assertEquals(32, (int) tree.ceiling(31));
    }

    @Test
    void floor() {
        AVLTree<Integer> tree = new AVLTree<>();
        Integer[] array = {-10, 4, 1, 100, 2, -21, 88, 32, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        assertEquals(4, (int) tree.floor(11));
    }

    private AVLTree<Integer> getIntegerFilledTree() {
        var tree = new AVLTree<Integer>();
        Integer[] array = {-10, 4, 1, 100, 2, -21, 88, 32, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        return tree;
    }
}