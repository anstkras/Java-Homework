package ru.hse.anstkras.hashtable;

/** Implementation of the polynomial hashing algorithm */
public class PolynomialHasher implements Hasher {
    @Override
    public int hash(String string) {
        long result = 0;
        for (int i = 0; i < string.length(); i++)
            result = (result * 37 + Character.getNumericValue(string.charAt(i))) % (int) (1e9 + 7);
        return (int) result;
    }
}