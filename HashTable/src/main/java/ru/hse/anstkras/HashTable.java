package ru.hse.anstkras;

import org.jetbrains.annotations.NotNull;

import java.util.*;

/** Implementation of hashtable based on separate chaining technique */
public class HashTable<K, V> extends AbstractMap<K, V> {
    private static final String NULL_KEY_ERROR = "Null can not be a key in hash table";
    private static final double DEFAULT_LOADFACTOR = 0.75;
    private static final int DEFAULT_CAPACITY = 10;
    private final double loadfactor;
    private List<K, V>[] lists;
    private int capacity;
    private int size;
    private Entry<K, V> head = null;
    private Entry<K, V> tail = null;

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(int capacity) {
        this(capacity, DEFAULT_LOADFACTOR);
    }

    public HashTable(int capacity, double loadFactor) {
        lists = new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            lists[i] = new LinkedList<>();
        }
        this.capacity = capacity;
        this.loadfactor = loadFactor;
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
    public boolean contains(K key) {
        if (key == null) {
            throw new IllegalArgumentException(NULL_KEY_ERROR);
        }

        int hashKey = hashMod(key);
        return lists[hashKey].contains(new Entry<>(key, null));
    }

    /**
     * Looks for a value by the given key.
     * If the key is not presented in the hash table returns null
     * Throws {@code IllegalArgumentException} in case of null argument
     *
     * @param key
     */
    public V get(Object key) {
        if (key == null) {
            throw new IllegalArgumentException(NULL_KEY_ERROR);
        }

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
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException(NULL_KEY_ERROR);
        }
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
     * @param key
     * @return the value of removed entry if exists, null otherwise
     */
    public V remove(Object key) {
        if (key == null) {
            throw new IllegalArgumentException(NULL_KEY_ERROR);
        }

        int hashKey = hashMod(key);
        Entry<K, V> removeEntry = lists[hashKey].remove(new Entry<>((K) key, null));
        if (removeEntry == null) {
            return null;
        } else {
            size--;
            removeFromList(removeEntry);
            return removeEntry.value;
        }

        // TODO выпилить, как элемент списка
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

    private List<K, V> entries() {
        var entries = new LinkedList<K, V>();
        for (List<K, V> list : lists) {
            entries.concat(list);
        }
        return entries;
    }

    private void checkLoadFactor() {
        if ((double) size / capacity > loadfactor) {
            rebuild();
        }
    }

    private int hashMod(Object key) {
        int result = key.hashCode() % capacity;
        if (result < 0) {
            result += capacity;
        }
        return result;
    }

    private void rebuild() {
        var newHashTable = new HashTable<K, V>(capacity * 2, loadfactor);
        List<K, V> entries = entries();
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

    private void addToList(Entry<K, V> entry) {
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

    private void removeFromList(Entry<K, V> entry) {
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

    public static class Entry<K, V> implements Map.Entry<K, V> {
        private K key;
        private V value;
        private Entry<K, V> prev;
        private Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V previousValue = this.value;
            this.value = value;
            return previousValue;
        }

        @Override
        public boolean equals(Object obj) {
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

            private EntryIterator(Entry<K, V> startEntry) {
                next = startEntry;
            }

            @Override
            public boolean hasNext() {
                return next != null;
            }

            @Override
            public Map.Entry<K, V> next() {
                Entry<K, V> entry = next;
                next = next.next;
                return entry;
            }
        }
    }
}