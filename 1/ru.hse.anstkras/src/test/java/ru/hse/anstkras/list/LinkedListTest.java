package ru.hse.anstkras.list;

import org.junit.jupiter.api.Test;
import ru.hse.anstkras.hashtable.HashTable;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

class LinkedListTest {

    @Test
    void empty() {
        LinkedList list = new LinkedList();
        assertTrue(list.empty());
    }

    @Test
    void notEmpty() {
        LinkedList list = new LinkedList();
        list.add(new HashTable.Entry("key0", "value0"));
        assertFalse(list.empty());
    }

    @Test
    void size() {
        LinkedList list = new LinkedList();
        list.add(new HashTable.Entry("key0", "value0"));
        list.add(new HashTable.Entry("key1", "value1"));
        assertEquals(2, list.size());
    }

    @Test
    void find() {
        LinkedList list = new LinkedList();
        for (int i = 0; i < 50; i++) {
            list.add(new HashTable.Entry("key" + i, "value" + i));
        }
        HashTable.Entry entryToFind = new HashTable.Entry("key21", null);
        assertEquals(new HashTable.Entry("key21", "value21"), list.find(entryToFind));
    }

    @Test
    void contains() {
        LinkedList list = new LinkedList();
        for (int i = 0; i < 50; i++) {
            list.add(new HashTable.Entry("key" + i, "value" + i));
        }
        HashTable.Entry entryToFind = new HashTable.Entry("key21", null);
        assertTrue(list.contains(entryToFind));
    }

    @Test
    void addAndRemove() {
        LinkedList list = new LinkedList();
        HashTable.Entry entry = new HashTable.Entry("key", "value");
        list.add(entry);
        list.remove(entry);
        assertTrue(list.empty());
    }

    @Test
    void add() {
        LinkedList list = new LinkedList();
        for (int i = 0; i < 42; i++) {
            list.add(new HashTable.Entry("key" + i, "value" + i));
        }
        assertEquals(42, list.size());
    }


    @Test
    void remove() {
        LinkedList list = new LinkedList();
        for (int i = 0; i < 50; i++) {
            list.add(new HashTable.Entry("key" + i, "value" + i));
        }
        HashTable.Entry entryToRemove = new HashTable.Entry("key21", null);
        list.remove(entryToRemove);
        assertFalse(list.contains(entryToRemove));
    }

    @Test
    void removeFromEmptyList() {
        LinkedList list = new LinkedList();
        assertNull(list.remove(new HashTable.Entry("123", "456")));
    }

    @Test
    void clear() {
        LinkedList list = new LinkedList();
        for (int i = 0; i < 100; i++) {
            list.add(new HashTable.Entry(Integer.toString(i), Integer.toString(i)));
        }
        list.clear();
        assertTrue(list.empty());
    }

    @Test
    void concat() {
        LinkedList list = new LinkedList();
        for (int i = 0; i < 100; i++) {
            list.add(new HashTable.Entry(Integer.toString(i), Integer.toString(i)));
        }
        LinkedList list2 = new LinkedList();
        for (int i = 0; i < 100; i++) {
            list2.add(new HashTable.Entry(Integer.toString(i), Integer.toString(i)));
        }
        list.concat(list2);
        assertEquals(200, list.size());
    }

    @Test
    void iterator() {
        LinkedList list = new LinkedList();
        for (int i = 0; i < 100; i++) {
            list.add(new HashTable.Entry(Integer.toString(i), Integer.toString(i)));
        }
        Iterator<HashTable.Entry> it = list.iterator();
        int count = 0;
        while (it.hasNext()) {
            count++;
            it.next();
        }
        assertEquals(100, count);
    }
}