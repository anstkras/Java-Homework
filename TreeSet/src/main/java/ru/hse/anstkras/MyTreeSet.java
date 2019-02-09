package ru.hse.anstkras;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public interface MyTreeSet<E> extends Set<E> {

    /** {@link TreeSet#descendingIterator()} **/
    Iterator<E> descendingIterator();

    /** {@link TreeSet#descendingSet()} **/
    TreeSet<E> descendingSet();


    /** {@link TreeSet#first()} **/
    E first();

    /** {@link TreeSet#last()} **/
    E last();


    /** {@link TreeSet#lower(Object)} **/
    E lower(E e);

    /** {@link TreeSet#floor(Object)} **/
    E floor(E e);


    /** {@link TreeSet#ceiling(Object)} **/
    E ceiling(E e);

    /** {@link TreeSet#higher(Object)}  **/
    E higher(E e);
}