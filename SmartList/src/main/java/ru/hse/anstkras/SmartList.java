package ru.hse.anstkras;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

public class SmartList<E> extends AbstractList<E> {
    private int size;
    private Object data;

    public SmartList() {

    }

    public SmartList(Collection<? extends E> collection) {

    }

    @Override
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        if (size == 1) {
            return (E) data;
        }
        if (size >= 2 && size <= 5) {
            return ((E[])data)[index];
        }
        else {
            return ((ArrayList<E>)data).get(index);
        }
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (size == 1) {
            Object tmp = data;
            data = element;
            return (E) tmp;
        } else if (size >= 2 && size <= 5) {
            Object tmp = ((Object[]) data)[index];
            ((Object[]) data)[index] = element;
            return (E) tmp;

        } else {
            return ((ArrayList<E>) data).set(index, element);
        }
    }

    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        if (size == 0 && index == 0) {
            data = element;
        } else if (size == 1) {
            if (index > 0) {
                Object tmp = data;
                data = new Object[5];
                ((Object[]) data)[0] = tmp;
                ((Object[]) data)[1] = element;
            } else {
                data = element;
            }
        } else if (size >= 2 && size < 5) {
            ((Object[]) data)[index] = element;
        } else if (size == 5) {
            if (index == 5) {
                Object tmp = data;
                data = new ArrayList<E>();
                for (int i = 0; i < 5; i++) {
                    ((ArrayList<E>) data).add(((E[]) tmp)[i]);
                }
            }
            else {
                ((Object[]) data)[index] = element;
            }

        } else {
            ((ArrayList<E>) data).add(index, element);
        }
        if (index == size) {
            size++;
        }
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }
}

