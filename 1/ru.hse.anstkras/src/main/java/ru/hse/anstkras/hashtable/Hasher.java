package ru.hse.anstkras.hashtable;

/**
 * Interface that allows to use different hashing algorithms
 */
public interface Hasher {
    int hash(String string);
}
