package ru.hse.anstkras.reflector;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class TestClass<E extends List<Set<E>> & Comparable<List<? super E>>, T> {
    private static int a;
    public volatile transient E e;
    public List<? super Collection<? extends List<? super E>>> list;
    public int[] array;
    static final List<? super Collection<? extends List<? super String>>>[] arr = null;
}
