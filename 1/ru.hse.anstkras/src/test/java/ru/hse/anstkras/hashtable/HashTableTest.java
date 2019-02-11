package ru.hse.anstkras.hashtable;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {
    private HashTable hashTable;


    private void fill(HashTable hashTable, int n) {
        for (int i = 0; i < n; i++) {
            hashTable.put("key" + i, "value" + i);
        }
    }

    @BeforeEach
    void init() {
        hashTable = new HashTable();
    }

    @AfterEach
    void makeNull() {
        hashTable = null;
    }

    @Test
    void empty() {
        assertTrue(hashTable.empty());
    }

    @Test
    void notEmpty() {
        hashTable.put("key0", "value0");
        assertFalse(hashTable.empty());
    }

    @Test
    void size() {
        hashTable.put("key0", "value0");
        hashTable.put("key1", "value1");
        assertEquals(2, hashTable.size());
    }

    @Test
    void contains() {
        fill(hashTable, 50);
        assertTrue(hashTable.contains("key21"));
    }

    @Test
    void get() {
        fill(hashTable, 50);
        assertEquals("value21", hashTable.get("key21"));
    }

    @Test
    void getNull() {
        fill(hashTable, 50);
        assertNull(hashTable.get("123"));
    }

    @Test
    void putIdenticalKeys() {
        hashTable.put("123", "1");
        hashTable.put("123", "2");
        assertEquals("2", hashTable.get("123"));
    }

    @Test
    void put() {
        fill(hashTable, 42);
        assertEquals(42, hashTable.size());
    }

    @Test
    void remove() {
        fill(hashTable, 50);
        hashTable.remove("key21");
        assertFalse(hashTable.contains("key21"));
    }

    @Test
    void removeNull() {
        fill(hashTable, 50);
        assertNull(hashTable.remove("123"));
    }

    @Test
    void clear() {
        fill(hashTable, 100);
        hashTable.clear();
        assertTrue(hashTable.empty());
    }
}