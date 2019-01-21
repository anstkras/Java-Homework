package ru.hse.anstkras.hashtable;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HashTableTest {

    @Test
    void empty() {
        HashTable hashTable = new HashTable();
        assertTrue(hashTable.empty());
    }

    @Test
    void notEmpty() {
        HashTable hashTable = new HashTable();
        hashTable.put("key0", "value0");
        assertFalse(hashTable.empty());
    }

    @Test
    void size() {
        HashTable hashTable = new HashTable();
        hashTable.put("key0", "value0");
        hashTable.put("key1", "value1");
        assertEquals(2, hashTable.size());
    }

    @Test
    void contains() {
        HashTable hashTable = new HashTable();
        for (int i = 0; i < 50; i++) {
            hashTable.put("key" + i, "value" + i);
        }
        assertTrue(hashTable.contains("key21"));
    }

    @Test
    void get() {
        HashTable hashTable = new HashTable();
        for (int i = 0; i < 50; i++) {
            hashTable.put("key" + i, "value" + i);
        }
        assertEquals("value21", hashTable.get("key21"));
    }

    @Test
    void getNull() {
        HashTable hashTable = new HashTable();
        for (int i = 0; i < 50; i++) {
            hashTable.put("key" + i, "value" + i);
        }
        assertNull(hashTable.get("123"));
    }

    @Test
    void putIdenticalKeys() {
        HashTable hashTable = new HashTable();
        hashTable.put("123", "1");
        hashTable.put("123", "2");
        assertEquals("2", hashTable.get("123"));
    }

    @Test
    void put() {
        HashTable hashTable = new HashTable();
        for (int i = 0; i < 42; i++) {
            hashTable.put("key" + Integer.toString(i), "value" + Integer.toString(i));
        }
        assertEquals(42, hashTable.size());
    }

    @Test
    void remove() {
        HashTable hashTable = new HashTable();
        for (int i = 0; i < 50; i++) {
            hashTable.put("key" + i, "value" + i);
        }
        hashTable.remove("key21");
        assertFalse(hashTable.contains("key21"));
    }

    @Test
    void removeNull() {
        HashTable hashTable = new HashTable();
        for (int i = 0; i < 50; i++) {
            hashTable.put("key" + i, "value" + i);
        }
        assertNull(hashTable.remove("123"));
    }

    @Test
    void clear() {
        HashTable hashTable = new HashTable();
        for (int i = 0; i < 100; i++) {
            hashTable.put(Integer.toString(i), Integer.toString(i));
        }
        hashTable.clear();
        assertTrue(hashTable.empty());
    }
}