package ru.hse.anstkras;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {
    private LinkedList<String, String> list;

    private void fill(List<String, String> list, int n) {
        for (int i = 0; i < n; i++) {
            list.add(new HashTable.Entry<>("key" + i, "value" + i));
        }
    }

    @BeforeEach
    void init() {
        list = new LinkedList<>();
    }

    @AfterEach
    void makeNull() {
        list = null;
    }

    @Test
    void empty() {
        assertTrue(list.empty());
    }

    @Test
    void notEmpty() {
        list.add(new HashTable.Entry<>("key0", "value0"));
        assertFalse(list.empty());
    }

    @Test
    void size() {
        list.add(new HashTable.Entry<>("key0", "value0"));
        list.add(new HashTable.Entry<>("key1", "value1"));
        assertEquals(2, list.size());
    }

    @Test
    void find() {
        fill(list, 50);
        HashTable.Entry<String, String> entryToFind = new HashTable.Entry<>("key21", null);
        assertEquals(new HashTable.Entry<>("key21", "value21"), list.find(entryToFind));
    }

    @Test
    void contains() {
        fill(list, 50);
        HashTable.Entry<String, String> entryToFind = new HashTable.Entry<>("key21", null);
        assertTrue(list.contains(entryToFind));
    }

    @Test
    void addAndRemove() {
        HashTable.Entry<String, String> entry = new HashTable.Entry<>("key", "value");
        list.add(entry);
        list.remove(entry);
        assertTrue(list.empty());
    }

    @Test
    void add() {
        fill(list, 42);
        assertEquals(42, list.size());
    }


    @Test
    void remove() {
        fill(list, 50);
        HashTable.Entry<String, String> entryToRemove = new HashTable.Entry<>("key21", null);
        list.remove(entryToRemove);
        assertFalse(list.contains(entryToRemove));
    }

    @Test
    void removeFromEmptyList() {
        assertNull(list.remove(new HashTable.Entry<>("123", "456")));
    }

    @Test
    void clear() {
        fill(list, 100);
        list.clear();
        assertTrue(list.empty());
    }

    @Test
    void concat() {
        fill(list, 100);
        var list2 = new LinkedList<String, String>();
        fill(list2, 100);
        list.concat(list2);
        assertEquals(200, list.size());
    }

    @Test
    void iterator() {
        fill(list, 100);
        Iterator<HashTable.Entry<String, String>> it = list.iterator();
        int count = 0;
        while (it.hasNext()) {
            count++;
            it.next();
        }
        assertEquals(100, count);
    }
}