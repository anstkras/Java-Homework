package ru.hse.anstkras.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class AVLTreeTest {

    private final Integer[] integerArray = {-10, 4, 1, 100, 2, -21, 88, 32, 51, 23, 12};
    private final String[] stringArray = {"eQD", "eqA", "e", "Fsd", "ABC", "AbD", "EQ"};

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
        var tree = getIntegerFilledTree();
        Integer[] newArray = new Integer[integerArray.length];
        var iterator = tree.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            newArray[i++] = iterator.next();
        }
        Arrays.sort(integerArray);
        assertArrayEquals(integerArray, newArray);
    }

    @Test
    void descendingIterator() {
        AVLTree<Integer> tree = getIntegerFilledTree();
        Integer[] newArray = new Integer[integerArray.length];
        var iterator = tree.descendingIterator();
        int i = 0;
        while (iterator.hasNext()) {
            newArray[i++] = iterator.next();
        }
        Arrays.sort(integerArray, Collections.reverseOrder());
        assertArrayEquals(integerArray, newArray);
    }

    @Test
    void descendingSet() {
        AVLTree<Integer> tree = getIntegerFilledTree();
        Integer[] newArray = new Integer[integerArray.length];
        MyTreeSet<Integer> reverseTree = tree.descendingSet();
        reverseTree.toArray(newArray);
        Arrays.sort(integerArray, Collections.reverseOrder());
        assertArrayEquals(integerArray, newArray);
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
        AVLTree<Integer> tree = getIntegerFilledTree();
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
        AVLTree<Integer> tree = getIntegerFilledTree();
        assertEquals(-21, (int) tree.first());
    }

    @Test
    void last() {
        AVLTree<Integer> tree = getIntegerFilledTree();
        assertEquals(100, (int) tree.last());
    }

    @Test
    void lower() {
        AVLTree<Integer> tree = getIntegerFilledTree();
        assertEquals(12, (int) tree.lower(23));
    }

    @Test
    void higher() {
        AVLTree<Integer> tree = getIntegerFilledTree();
        assertEquals(100, (int) tree.higher(88));
    }

    @Test
    void ceiling() {
        AVLTree<Integer> tree = getIntegerFilledTree();
        assertEquals(32, (int) tree.ceiling(31));
    }

    @Test
    void floor() {
        AVLTree<Integer> tree = getIntegerFilledTree();
        assertEquals(4, (int) tree.floor(11));
    }

    @Test
    void descendingSetIterator() {
        AVLTree<Integer> tree = getIntegerFilledTree();
        Iterator<Integer> descendingIterator = tree.descendingIterator();
        Iterator<Integer> descendingTreeIterator = tree.descendingSet().iterator();
        while (descendingIterator.hasNext() && descendingTreeIterator.hasNext()) {
            assertEquals(descendingIterator.next(), descendingTreeIterator.next());
        }
        assertFalse(descendingIterator.hasNext());
        assertFalse(descendingTreeIterator.hasNext());
    }

    @Test
    void descendingSetDescendingIterator() {
        AVLTree<Integer> tree = getIntegerFilledTree();
        Iterator<Integer> iterator = tree.iterator();
        Iterator<Integer> descendingTreeDescendingIterator = tree.descendingSet().descendingIterator();
        while (iterator.hasNext() && descendingTreeDescendingIterator.hasNext()) {
            assertEquals(iterator.next(), descendingTreeDescendingIterator.next());
        }
        assertFalse(iterator.hasNext());
        assertFalse(descendingTreeDescendingIterator.hasNext());
    }

    @Test
    void descendingSetDescendingSet() {
        MyTreeSet<Integer> tree = getIntegerFilledTree();
        MyTreeSet<Integer> twiceDescendingTree = tree.descendingSet().descendingSet();
        Integer[] treeArray = new Integer[tree.size()];
        tree.toArray(treeArray);
        Integer[] twiceDescendingTreeArray = new Integer[twiceDescendingTree.size()];
        twiceDescendingTree.toArray(twiceDescendingTreeArray);
        assertArrayEquals(treeArray, twiceDescendingTreeArray);
    }

    @Test
    void descendingSetFirst() {
        MyTreeSet<Integer> tree = getIntegerFilledTree().descendingSet();
        assertEquals(100, (int) tree.first());
    }

    @Test
    void descendingSetLast() {
        MyTreeSet<Integer> tree = getIntegerFilledTree().descendingSet();
        assertEquals(-21, (int) tree.last());
    }

    @Test
    void descendingSetLower() {
        MyTreeSet<Integer> tree = getIntegerFilledTree().descendingSet();
        assertEquals(32, (int) tree.lower(23));
    }

    @Test
    void descendingSetHigher() {
        MyTreeSet<Integer> tree = getIntegerFilledTree().descendingSet();
        assertEquals(51, (int) tree.higher(88));
    }

    @Test
    void descendingSetCeiling() {
        MyTreeSet<Integer> tree = getIntegerFilledTree().descendingSet();
        assertEquals(23, (int) tree.ceiling(23));
    }

    @Test
    void descendingSetFloor() {
        MyTreeSet<Integer> tree = getIntegerFilledTree().descendingSet();
        assertEquals(12, (int) tree.floor(11));
    }

    @Test
    void testAddToDescendingSetChangeSet() {
        AVLTree<Integer> tree = getIntegerFilledTree();
        MyTreeSet<Integer> descendingTree = tree.descendingSet();
        assertTrue(descendingTree.add(8));
        assertTrue(descendingTree.contains(8));
        assertTrue(tree.contains(8));
    }

    @Test
    void testRemoveToDescendingSetChangeSet() {
        MyTreeSet<Integer> tree = getIntegerFilledTree();
        MyTreeSet<Integer> descendingTree = tree.descendingSet();
        assertTrue(descendingTree.remove(100));
        assertFalse(descendingTree.contains(100));
        assertFalse(tree.contains(100));
    }

    @Test
    void firstWithComparator() {
        MyTreeSet<String> tree = getStringFilledTreeWithComparator();
        assertEquals("Fsd", tree.first());
    }

    @Test
    void removeWithComparator() {
        MyTreeSet<String> tree = getStringFilledTreeWithComparator();
        assertTrue(tree.remove("E"));
    }

    @Test
    void lowerWithComparator() {
        MyTreeSet<String> tree = getStringFilledTreeWithComparator();
        assertEquals("eQD", tree.lower("EQb"));
    }

    @Test
    void addNullToEmptyTreeWithNaturalOrdering() {
        var tree = new AVLTree<String>();
        assertThrows(NullPointerException.class, () -> tree.add(null));
    }

    @Test
    void addNullToFilledTreeWithNaturalOrdering() {
        MyTreeSet<Integer> tree = getIntegerFilledTree();
        assertThrows(NullPointerException.class, () -> tree.add(null));
    }

    @Test
    void addNullToFilledTreeWithComparator() {
        var tree = new AVLTree<String>((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return -1;
            }
            if (o2 == null) {
                return 1;
            }
            return o1.compareTo(o2);
        });
        assertDoesNotThrow(() -> tree.add(null));
    }

    @Test
    void testTreeSetOfNonComparableElements() {
        class NonComparable {
        }
        var tree = new AVLTree<NonComparable>();
        assertThrows(ClassCastException.class, () -> tree.add(new NonComparable()));
    }

    @Test
    void testContainsForOtherType() {
        MyTreeSet<Integer> tree = getIntegerFilledTree();
        var comparableWithInteger = new ComparableWithInteger(100);
        assertTrue(tree.contains(comparableWithInteger));
    }

    @Test
    void removeForOtherType() {
        MyTreeSet<Integer> tree = getIntegerFilledTree();
        var comparableWithInteger = new ComparableWithInteger(100);
        assertTrue(tree.remove(comparableWithInteger));
        assertFalse(tree.contains(100));
    }

    @Test
    void testIteratorNoSuchElementException() {
        MyTreeSet<Integer> tree = getIntegerFilledTree();
        Iterator<Integer> iterator = tree.iterator();
        while (iterator.hasNext()) {
            iterator.next();
        }
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    void testFirstEmptyTreeSet() {
        MyTreeSet<Integer> tree = new AVLTree<>();
        assertThrows(NoSuchElementException.class, tree::first);
    }

    @Test
    void testLastEmptyTreeSet() {
        MyTreeSet<Integer> tree = new AVLTree<>();
        assertThrows(NoSuchElementException.class, tree::last);
    }

    private class ComparableWithInteger implements Comparable<Integer> {
        private final Integer value;

        private ComparableWithInteger(int value) {
            this.value = value;
        }

        @Override
        public int compareTo(@NotNull Integer valueToCompare) {
            return value.compareTo(valueToCompare);
        }
    }

    private AVLTree<Integer> getIntegerFilledTree() {
        var tree = new AVLTree<Integer>();
        tree.addAll(Arrays.asList(integerArray));
        return tree;
    }

    private AVLTree<String> getStringFilledTreeWithComparator() {
        var tree = new AVLTree<>(Collections.reverseOrder(String::compareToIgnoreCase));
        tree.addAll(Arrays.asList(stringArray));
        return tree;
    }
}