package ru.hse.anstkras.hashtable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/** Doubly linked list implementation of the EntryList interface */

class LinkedEntryList<K, V> implements EntryList<K, V> {
    private ListNode<K, V> head;
    private ListNode<K, V> tail;
    private int size;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean empty() {
        return size == 0;
    }

    /**
     * Removes the first occurrence of the given element if it is contained in the list,
     * otherwise returns null
     *
     * @param value element to remove
     * @return removed element if it was contained in the list, null otherwise
     */
    @Override
    @Nullable
    public HashTable.Entry<K, V> remove(@NotNull HashTable.Entry<?, V> value) {
        for (ListNode<K, V> node = head; node != null; node = node.next) {
            if (node.value.equals(value)) {
                ListNode<K, V> next = node.next;
                ListNode<K, V> prev = node.prev;

                if (next == null) {
                    tail = prev;
                } else {
                    next.prev = prev;
                    node.next = null;
                }

                if (prev == null) {
                    head = next;
                } else {
                    prev.next = next;
                    node.next = null;
                }

                size--;
                HashTable.Entry<K, V> nodeValue = node.value;
                node.value = null;
                return nodeValue;
            }
        }
        return null;
    }

    /**
     * Add the element to the end of the list
     *
     * @param value element to be added to the list
     */
    @Override
    public void add(@NotNull HashTable.Entry<K, V> value) {
        if ((head == null && tail != null) || (head != null && tail == null)) {
            throw new IllegalStateException();
        }
        if (head == null && tail == null) {
            tail = new ListNode<>(value);
            head = tail;
        } else {
            tail.next = new ListNode<>(value);
            tail.next.prev = tail;
            tail = tail.next;
        }
        size++;
    }

    /**
     * Finds the first occurrence of the element that is equal to the given element
     *
     * @param value element to find
     * @return the first occurrence of the element that is equal to the given element
     */
    @Override
    @Nullable
    public HashTable.Entry<K, V> find(@NotNull HashTable.Entry<?, V> value) {
        for (ListNode<K, V> node = head; node != null; node = node.next) {
            if (node.value.equals(value)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public boolean contains(@NotNull HashTable.Entry<K, V> value) {
        return find(value) != null;
    }

    @Override
    public void clear() {
        ListNode node = head;

        while (node != null) {
            ListNode next = node.next;
            node.next = null;
            node.prev = null;
            node.value = null;
            node = next;
        }

        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Concatenates the other list to the end of this list
     *
     * @param other list to concatenate
     */
    @Override
    public void concat(@NotNull EntryList<K, V> other) {
        if (other.empty()) {
            return;
        }

        if (other.getClass() != getClass()) {
            for (HashTable.Entry<K, V> entry : other) {
                add(entry);
            }
            return;
        }

        var otherList = (LinkedEntryList<K, V>) other;
        if ((head == null && tail != null) || (head != null && tail == null)) {
            throw new IllegalStateException();
        }
        if (head == null && tail == null) {
            tail = otherList.head;
            head = tail;
            size = otherList.size;
        } else {
            tail.next = otherList.head;
            if (otherList.head != null) {
                otherList.head.prev = tail;
            }
            size += otherList.size;
            tail = otherList.tail;
        }
    }

    /**
     * Returns an iterator that traverses the list in
     * the order the elements were added
     *
     * @return an iterator
     */
    @NotNull
    @Override
    public Iterator<HashTable.Entry<K, V>> iterator() {
        return new Iterator<>() {
            private ListNode<K, V> node = head;

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public HashTable.Entry<K, V> next() {
                HashTable.Entry<K, V> value = node.value;
                node = node.next;
                return value;
            }
        };
    }

    private static class ListNode<K, V> {
        private HashTable.Entry<K, V> value;
        private ListNode<K, V> next;
        private ListNode<K, V> prev;

        private ListNode() {
        }

        private ListNode(@NotNull HashTable.Entry<K, V> value) {
            this.value = value;
        }
    }
}