package ru.hse.anstkras.hashtable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Implementation of hashtable based on separate chaining technique
 * Null keys are not allowed
 */
public class HashTable<K, V> extends AbstractMap<K, V> {
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    private static final int DEFAULT_CAPACITY = 10;
    private final double loadFactor;
    private EntryList<K, V>[] lists;
    private int capacity;
    private int size;
    private Entry<K, V> head = null;
    private Entry<K, V> tail = null;

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(int capacity) {
        this(capacity, DEFAULT_LOAD_FACTOR);
    }

    public HashTable(int capacity, double loadFactor) {
        lists = new LinkedEntryList[capacity];
        for (int i = 0; i < capacity; i++) {
            lists[i] = new LinkedEntryList<>();
        }
        this.capacity = capacity;
        this.loadFactor = loadFactor;
    }

    public int size() {
        return size;
    }

    public boolean empty() {
        return size == 0;
    }

    /**
     * Checks if the given key is presented in the hash table.
     * Throws {@code IllegalArgumentException} in case of null argument
     */
    public boolean contains(@NotNull K key) {
        int hashKey = hashMod(key);
        return lists[hashKey].contains(new Entry<>(key, null));
    }

    /**
     * Looks for a value by the given key.
     * If the key is not presented in the hash table returns null
     * Throws {@code IllegalArgumentException} in case of null argument
     */
    @Nullable
    public V get(@NotNull Object key) {

        int hashKey = hashMod(key);
        Entry<K, V> entry = lists[hashKey].find(new Entry<>((K) key, null));
        if (entry == null) {
            return null;
        }
        return entry.value;
    }

    /**
     * Associates the given key with the given value.
     * If there was an old value associated with the given key,
     * than returns the old value, otherwise returns null
     * Null value is allowed
     * Throws {@code IllegalArgumentException} in case of null key
     *
     * @return the old value if exists, null otherwise
     */
    @Nullable
    public V put(@NotNull K key, @Nullable V value) {
        var newEntry = new Entry<>(key, value);
        addToList(newEntry);
        int hashKey = hashMod(key);
        Entry<K, V> entry = lists[hashKey].remove(new Entry<>(key, null));
        lists[hashKey].add(newEntry);
        if (entry == null) {
            size++;
            checkLoadFactor();
            return null;
        } else {
            return entry.value;
        }
    }

    /**
     * Removes the entry by the given key if exists, returns null otherwise.
     * Throws {@code IllegalArgumentException} in case of null argument
     *
     * @return the value of removed entry if exists, null otherwise
     */
    @Nullable
    public V remove(@NotNull Object key) {
        int hashKey = hashMod(key);
        Entry<K, V> removeEntry = lists[hashKey].remove(new Entry<>((K) key, null));
        if (removeEntry == null) {
            return null;
        } else {
            size--;
            removeFromList(removeEntry);
            return removeEntry.value;
        }
    }

    /** Delete all the values in the hashtable and shrinks its capacity */
    public void clear() {
        assignLists(new HashTable<>());
    }

    @NotNull
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return new EntrySet();
    }

    private EntryList<K, V> entries() {
        var entries = new LinkedEntryList<K, V>();
        for (EntryList<K, V> list : lists) {
            entries.concat(list);
        }
        return entries;
    }

    private void checkLoadFactor() {
        if ((double) size / capacity > loadFactor) {
            rebuild();
        }
    }

    private int hashMod(@NotNull Object key) {
        int result = key.hashCode() % capacity;
        if (result < 0) {
            result += capacity;
        }
        return result;
    }

    private void rebuild() {
        var newHashTable = new HashTable<K, V>(capacity * 2, loadFactor);
        EntryList<K, V> entries = entries();
        for (Entry<K, V> entry : entries) {
            newHashTable.put(entry.key, entry.value);
        }
        assignLists(newHashTable);
    }

    private void assignLists(HashTable<K, V> hashTable) {
        deleteAll();
        lists = hashTable.lists;
        size = hashTable.size;
        capacity = hashTable.capacity;
    }

    private void deleteAll() {
        lists = null;
        size = 0;
    }

    private void addToList(@NotNull Entry<K, V> entry) {
        if ((head == null && tail != null) || (head != null && tail == null)) {
            throw new IllegalStateException();
        }
        if (head == null && tail == null) {
            tail = entry;
            head = tail;
        } else {
            tail.next = entry;
            tail.next.prev = tail;
            tail = tail.next;
        }
    }

    private void removeFromList(@NotNull Entry<K, V> entry) {
        Entry<K, V> next = entry.next;
        Entry<K, V> prev = entry.prev;

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            entry.next = null;
        }

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            entry.next = null;
        }
    }

    static class Entry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;
        private Entry<K, V> prev;
        private Entry<K, V> next;

        Entry(@NotNull K key, @Nullable V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        @NotNull
        public K getKey() {
            return key;
        }

        @Override
        @Nullable
        public V getValue() {
            return value;
        }

        @Override
        @Nullable
        public V setValue(@Nullable V value) {
            V previousValue = this.value;
            this.value = value;
            return previousValue;
        }

        @Override
        public boolean equals(@Nullable Object obj) {
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            var otherEntry = (Entry) obj;
            return otherEntry.key.equals(key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @NotNull
        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntryIterator(head);
        }

        @Override
        public int size() {
            return HashTable.this.size();
        }

        private class EntryIterator implements Iterator<Map.Entry<K, V>> {
            private Entry<K, V> next;

            private EntryIterator(@Nullable Entry<K, V> startEntry) {
                next = startEntry;
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Map.Entry<K, V> next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                Entry<K, V> entry = next;
                next = next.next;
                return entry;
            }
        }
    }
}