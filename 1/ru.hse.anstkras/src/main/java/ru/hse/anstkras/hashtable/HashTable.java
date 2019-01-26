package ru.hse.anstkras.hashtable;

import ru.hse.anstkras.list.LinkedList;
import ru.hse.anstkras.list.List;

/**
 * Implementation of hashtable based on separate chaining technique
 */
public class HashTable {
    protected static final double DEFAULT_LOADFACTOR = 0.75;
    protected static final int DEFAULT_CAPACITY = 10;
    protected final double loadfactor;
    protected final Hasher hasher;
    protected List[] lists;
    protected int capacity;
    protected int size;

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(int capacity) {
        this(capacity, DEFAULT_LOADFACTOR, new PolynomialHasher());
    }

    public HashTable(int capacity, double loadFactor, Hasher hasher) {
        lists = new LinkedList[capacity];
        for (int i = 0; i < capacity; i++) {
            lists[i] = new LinkedList();
        }
        this.capacity = capacity;
        this.loadfactor = loadFactor;
        this.hasher = hasher;
    }

    public int size() {
        return size;
    }

    public boolean empty() {
        return size == 0;
    }

    public boolean contains(String key) {
        int hashKey = hashMod(key);
        return lists[hashKey].contains(new Entry(key, null));
    }

    public String get(String key) {
        int hashKey = hashMod(key);
        Entry entry = lists[hashKey].find(new Entry(key, null));
        if (entry == null) {
            return null;
        }
        return entry.value;
    }

    /**
     * Associates the given key with the given value.
     * If there was an old value associated with the given key,
     * than returns the old value, otherwise returns null
     *
     * @param key
     * @param value
     * @return the old value if exists, null otherwise
     */
    public String put(String key, String value) {
        int hashKey = hashMod(key);
        Entry entry = lists[hashKey].remove(new Entry(key, null));
        lists[hashKey].add(new Entry(key, value));
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
     *
     * @param key
     * @return the value of removed entry if exists, null otherwise
     */
    public String remove(String key) {
        int hashKey = hashMod(key);
        Entry removeEntry = lists[hashKey].remove(new Entry(key, null));
        if (removeEntry == null) {
            return null;
        } else {
            size--;
            return removeEntry.value;
        }
    }

    /**
     * Delete all the values in the hashtable and shrinks its capacity
     */
    public void clear() {
        assignLists(new HashTable());
    }

    /**
     * Constructs the list of all entries in the hashtable in arbitrary order
     *
     * @return the list of all entries in the hashtable in arbitrary order
     */
    public List entries() {
        List entries = new LinkedList();
        for (List list : lists) {
            entries.concat(list);
        }
        return entries;
    }

    protected final void checkLoadFactor() {
        if ((double) size / capacity > loadfactor) {
            rebuild();
        }
    }

    protected final int hashMod(String string) {
        return hasher.hash(string) % capacity;
    }

    protected final void rebuild() {
        HashTable newHashTable = new HashTable(capacity * 2, loadfactor, hasher);
        List entries = entries();
        for (Entry entry : entries) {
            newHashTable.put(entry.key, entry.value);
        }
        assignLists(newHashTable);
    }

    protected final void assignLists(HashTable hashTable) {
        deleteAll();
        lists = hashTable.lists;
        size = hashTable.size;
        capacity = hashTable.capacity;
    }

    protected final void deleteAll() {
        for (List list : lists) {
            list.clear();
        }
        lists = null;
        size = 0;
    }

    public static class Entry {
        protected String key;
        protected String value;

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Entry otherEntry = (Entry) obj;
            return otherEntry.key.equals(key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }
}
