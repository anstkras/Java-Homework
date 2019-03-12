package ru.hse.anstkras.reflector;


import java.io.Serializable;
import java.util.*;

public class TestClass<E extends List<Set<E>> & Comparable<List<? super E>>, T> extends AbstractSet<E> implements Comparable<T>, Serializable {
    static List<? super Collection<? extends List<? super String>>>[] arr = null;
    private static final int a = 0;
    public volatile transient E e;
    public List<? super Collection<? extends List<? super E>>> list;
    public int[] array;
    public List<E> liste;

    public <T> TestClass() {
        T t;
    }

    public static final <T, E> int psf(int i, T ttt) {
        throw new UnsupportedOperationException();
    }

    public int compareTo(T o) {
        return 0;
    }

    public Iterator<E> iterator() {
        return null;
    }

    public int size() {
        return 0;
    }

    private abstract static interface inter {
        public abstract void q();

        default int v() {
            return 1;
        }
    }

    private static class A {
        int a;

        public A(int aaa) {

        }

        public static class B {
            static List<? super Collection<? extends List<? super String>>>[] arr = null;

        }
    }

    private class B {
        String b;

        private B(int a) {

        }

        private class C {

        }
    }
}
