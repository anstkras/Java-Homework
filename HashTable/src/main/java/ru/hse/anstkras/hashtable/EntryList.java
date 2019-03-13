package ru.hse.anstkras.hashtable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An ordered collection of elements. */

interface EntryList<K, V> extends Iterable<HashTable.Entry<K, V>> {
    int size();

    boolean empty();


    /**
     * Removes the first occurrence of the given element if it is contained in the list,
     * otherwise returns null
     *
     * @param value element to remove
     * @return removed element if it was contained in the list, null otherwise
     */
    @Nullable
    HashTable.Entry<K, V> remove(@NotNull HashTable.Entry<?, V> value);

    /**
     * Add the element to the end of the list
     *
     * @param value element to be added to the list
     */
    void add(@NotNull HashTable.Entry<K, V> value);

    /**
     * Finds the first occurrence of the element that is equal to the given element
     *
     * @param value element to find
     * @return the first occurrence of the element that is equal to the given element
     */
    @Nullable
    HashTable.Entry<K, V> find(@NotNull HashTable.Entry<?, V> value);

    boolean contains(@NotNull HashTable.Entry<K, V> value);

    void clear();

    /**
     * Concatenates the other list to the end of this list
     *
     * @param other list to concatenate
     */
    void concat(@NotNull EntryList<K, V> other);
}