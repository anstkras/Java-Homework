package Trie;

import java.util.HashMap;

public class Trie {
    private Vertex root = new Vertex();
    private int size;

    /**
     * Adds the element in the trie if the trie
     * did not contain the element.
     * Takes linear time of element's length
     * Throws {@code IllegalArgumentException} in case of null argument
     *
     * @param element string to add
     * @return false if element is already in the
     * trie, true otherwise
     */

    public boolean add(String element) {
        if (element == null) {
            throw new IllegalArgumentException("Null can not be an element in the trie");
        }

        boolean result = false;
        Vertex curNode = root;
        for (int i = 0; i < element.length(); i++) {
            char c = element.charAt(i);
            if (curNode.next.containsKey(c)) {
                curNode = curNode.next.get(c);
            } else {
                result = true;
                Vertex newNode = new Vertex(c);
                curNode.next.put(c, newNode);
                curNode = newNode;

            }
        }
        if (!curNode.isTerminal) {
            result = true;
        }
        if (result) {
            size++;
        }
        curNode.isTerminal = true;

        return result;
    }

    /**
     * Checks if the trie contains the given element.
     * Takes linear time of element's length
     * Throws {@code IllegalArgumentException} in case of null argument
     *
     * @return {@code true} if the element is presented in the trie
     * {@code false} otherwise
     */
    public boolean contains(String element) {
        if (element == null) {
            throw new IllegalArgumentException("Null can not be an element in the trie");
        }

        Vertex curNode = root;
        for (int i = 0; i < element.length(); i++) {
            char c = element.charAt(i);
            if (curNode.next.containsKey(c)) {
                curNode = curNode.next.get(c);
            } else {
                return false;
            }
        }
        return curNode.isTerminal;
    }

    /**
     * Removes the element if it's presented in
     * the trie
     * Takes linear time of element's length
     * Throws {@code IllegalArgumentException} in case of null argument
     * @param element String to remove
     * @return {@code true} if the element was removed,
     * {@code false} if the element was not presented in
     * the trie
     */
    public boolean remove(String element) {
        if (element == null) {
            throw new IllegalArgumentException("Null can not be an element in the trie");
        }

        return false;
    }

    public int size() {
        return size;
    }

    public boolean empty() {
        return size == 0;
    }

    public int howManyStartWithPrefix(String prefix) {
        return 0;
    }

    private static class Vertex {
        private HashMap<Character, Vertex> next = new HashMap<>();
        private boolean isTerminal;
        private char symbol;
        private int size = 1;

        public Vertex() {
        }

        public Vertex(char symbol) {
            this.symbol = symbol;
        }
    }
}
