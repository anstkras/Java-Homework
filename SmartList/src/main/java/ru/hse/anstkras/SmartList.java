package ru.hse.anstkras;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;

/** An implementation of list that provide efficient storage of small number of elements */
public class SmartList<E> extends AbstractList<E> {
    private int size;
    private Object data;

    public SmartList() {

    }

    public SmartList(Collection<? extends E> collection) {
        addAll(collection);
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
            return ((E[]) data)[index];
        } else {
            return ((ArrayList<E>) data).get(index);
        }
    }

    @Override
    public int size() {
        return size;
    }

    /**
     * Set the element at given index and changing the type of inner collection if appropriate
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index >= size})
     */
    @Override
    public E set(int index, E element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        if (size == 1) {
            Object result = data;
            data = element;
            return (E) result;
        } else if (size >= 2 && size <= 5) {
            E result = ((E[]) data)[index];
            ((Object[]) data)[index] = element;
            return result;

        } else {
            return ((ArrayList<E>) data).set(index, element);
        }
    }

    /**
     * Add the element at given index and changing the type of inner collection if appropriate
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index > size})
     */
    @Override
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        if (size == 0 && index == 0) {
            data = element;
        } else if (size == 1) {
            if (index > 0) {
                Object result = data;
                data = new Object[5];
                ((Object[]) data)[0] = result;
                ((Object[]) data)[1] = element;
            } else {
                data = element;
            }
        } else if (size >= 2 && size < 5) {
            for (int i = size; i > index; i--) {
                ((Object[]) data)[i] = ((Object[]) data)[i - 1];
            }
            ((Object[]) data)[index] = element;
        } else if (size == 5) {
            if (index == 5) {
                Object result = data;
                data = new ArrayList<E>();
                for (int i = 0; i < 5; i++) {
                    ((ArrayList<E>) data).add(((E[]) result)[i]);
                }
                ((ArrayList<E>) data).add(index, element);
            } else {
                for (int i = size; i > index; i--) {
                    ((Object[]) data)[i] = ((Object[]) data)[i - 1];
                }
                ((Object[]) data)[index] = element;
            }

        } else {
            ((ArrayList<E>) data).add(index, element);
        }
        if (index == size) {
            size++;
        }
    }

    /**
     * Removes the element at given index and changing the type of inner collection if appropriate
     * @return an element that was at that index
     * @throws IndexOutOfBoundsException if the index is out of range
     *         ({@code index < 0 || index > size})
     */
    @Override
    public E remove(int index) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }

        if (size == 1) {
            Object result = data;
            data = null;
            size = 0;
            return (E) result;
        }
        if (size == 2) {
            int indexToStay = index == 0 ? 1 : 0;
            E result = ((E[]) data)[index];
            size--;
            data = ((E[]) data)[indexToStay];
            return result;
        }

        if (size > 2 && size <= 5) {
            E[] array = (E[]) data;
            E result = array[index];
            for (int i = index; i < 4; i++) {
                array[i] = array[i + 1];
            }
            array[4] = null;
            size--;
            return result;
        }
        if (size == 6) {
            ArrayList<E> arrayList = (ArrayList<E>) data;
            E result = arrayList.remove(index);
            data = new Object[5];
            for (int i = 0; i < 5; i++) {
                ((E[]) data)[i] = arrayList.get(i);
            }
            size--;
            return result;
        }
        if (size > 6) {
            size--;
            return ((ArrayList<E>) data).remove(index);
        }
        return null;
    }
}

