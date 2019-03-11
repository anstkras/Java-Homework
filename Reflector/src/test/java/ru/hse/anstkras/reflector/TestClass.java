package ru.hse.anstkras.reflector;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.*;

public class TestClass<E extends List<Set<E>> & Comparable<List<? super E>>, T> extends AbstractSet<E> implements Comparable<T>, Serializable {
    private static int a;
    public volatile transient E e;
    public List<? super Collection<? extends List<? super E>>> list;
    public int[] array;
    public List<E> liste;
    static List<? super Collection<? extends List<? super String>>>[] arr = null;

    public int compareTo(T o) {
        return 0;
    }

    public <T> TestClass() {
        T t;
    }

    public Iterator<E> iterator() {
        return null;
    }

    public int size() {
        return 0;
    }

    public static final <T, E> int psf(int i, T ttt) {
        throw new UnsupportedOperationException();
    }
}
