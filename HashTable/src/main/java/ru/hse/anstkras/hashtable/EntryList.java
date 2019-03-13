package ru.hse.anstkras.hashtable;

/** An ordered collection of elements. */

interface EntryList<K, V> extends Iterable<HashTable.Entry<K, V>> {
    int size();

    boolean empty();

    HashTable.Entry<K, V> remove(HashTable.Entry<K, V> value);

    /**
     * Add the element to the end of the list
     *
     * @param value element to be added to the list
     */
    void add(HashTable.Entry<K, V> value);

    /**
     * Finds the first occurrence of the element that is equal to the given element
     *
     * @param value element to find
     * @return the first occurrence of the element that is equal to the given element
     */
    HashTable.Entry find(HashTable.Entry<K, V> value);

    boolean contains(HashTable.Entry<K, V> value);

    void clear();

    /**
     * Concatenates the other list to the end of this list
     *
     * @param other list to concatenate
     */
    void concat(EntryList<K, V> other);
}