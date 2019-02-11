package ru.hse.anstkras.list;

import ru.hse.anstkras.hashtable.HashTable;

/** An ordered collection of elements. */

public interface List extends Iterable<HashTable.Entry> {
    int size();

    boolean empty();

    HashTable.Entry remove(HashTable.Entry value);

    /**
     * Add the element to the end of the list
     *
     * @param value element to be added to the list
     */
    void add(HashTable.Entry value);

    /**
     * Finds the first occurrence of the element that is equal to the given element
     *
     * @param value element to find
     * @return the first occurrence of the element that is equal to the given element
     */
    HashTable.Entry find(HashTable.Entry value);

    boolean contains(HashTable.Entry value);

    void clear();

    /**
     * Concatenates the other list to the end of this list
     *
     * @param other list to concatenate
     */
    void concat(List other);
}
