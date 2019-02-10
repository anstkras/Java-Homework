package ru.hse.anstkras.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    @Test
    void addTheSameValue() {
        AVLTree<Integer> tree = new AVLTree<>();
        assertTrue(tree.add(1));
        assertFalse(tree.add(1));
    }

    @Test
    void ascendingIterator() {
        AVLTree<Integer> tree = new AVLTree<>();
        Integer[] array = {4, 1, 100, 2, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        Integer[] newArray = new Integer[7];
        tree.toArray(newArray);
        Arrays.sort(array);
        assertArrayEquals(array, newArray);
    }

    @Test
    void descendingIterator() {
        AVLTree<Integer> tree = new AVLTree<>();
        Integer[] array = {4, 1, 100, 2, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        MyTreeSet<Integer> reverseTree = tree.descendingSet();
        var it = tree.descendingIterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
        Integer[] newArray = new Integer[7];
        reverseTree.toArray(newArray);
        Arrays.sort(array, Collections.reverseOrder());
        assertArrayEquals(array, newArray);
    }

    @Test
    void addAndRemove() {
        AVLTree<Integer> tree = new AVLTree<>();
        assertTrue(tree.add(1));
        var it = tree.iterator();
        it.next();
        it.remove();
        assertTrue(tree.add(1));
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
        assertEquals(-21, (int)tree.first());
    }

    @Test
    void last() {
        AVLTree<Integer> tree = new AVLTree<>();
        Integer[] array = {-10, 4, 1, 100, 2, -21, 88, 32, 51, 23, 12};
        tree.addAll(Arrays.asList(array));
        assertEquals(100, (int)tree.last());
    }

}