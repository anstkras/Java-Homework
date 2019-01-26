package ru.hse.anstkras.list;

import ru.hse.anstkras.hashtable.HashTable;

import java.util.Iterator;

/**
 * Doubly linked list implementation of the List interface
 */

public class LinkedList implements List {
    protected ListNode head;
    protected ListNode tail;
    protected int size;

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
    public HashTable.Entry remove(HashTable.Entry value) {
        for (ListNode node = head; node != null; node = node.next) {
            if ((value == null && node.value == null) || node.value.equals(value)) {
                ListNode next = node.next;
                ListNode prev = node.prev;

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
                HashTable.Entry nodeValue = node.value;
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
    public void add(HashTable.Entry value) {
        if ((head == null && tail != null) || (head != null && tail == null)) {
            throw new IllegalStateException();
        }
        if (head == null && tail == null) {
            tail = new ListNode(value);
            head = tail;
        } else {
            tail.next = new ListNode(value);
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
    public HashTable.Entry find(HashTable.Entry value) {
        for (ListNode node = head; node != null; node = node.next) {
            if ((value == null && node.value == null) || node.value.equals(value)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public boolean contains(HashTable.Entry value) {
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
    public void concat(List other) {
        if (other == null || other.empty()) {
            return;
        }

        if (other.getClass() != getClass()) {
            for (HashTable.Entry entry : other) {
                add(entry);
            }
            return;
        }

        var otherList = (LinkedList) other;
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
    @Override
    public Iterator<HashTable.Entry> iterator() {
        return new Iterator<>() {
            private ListNode node = head;

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public HashTable.Entry next() {
                HashTable.Entry value = node.value;
                node = node.next;
                return value;
            }
        };
    }

    protected static class ListNode {
        protected HashTable.Entry value;
        protected ListNode next;
        protected ListNode prev;

        protected ListNode() {
        }

        protected ListNode(HashTable.Entry value) {
            this.value = value;
        }
    }
}
