package ru.hse.anstkras.Trie;

import ru.hse.anstkras.Serializable.Serializable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/** Implements a tree data structure that stores strings */
public class Trie implements Serializable {
    private final static String NULL_ERROR = "Null can not be an element in the trie";
    private TrieNode root = new TrieNode();

    /**
     * Adds the element in the trie if the trie
     * did not contain the element.
     * Takes linear time of element's length
     *
     * @param element string to add
     * @return {@code false} if element is already in the
     * trie, {@code true} otherwise
     * @throws IllegalArgumentException in case of null argument
     */
    public boolean add(String element) {
        checkIsNull(element, NULL_ERROR);

        var nodes = new ArrayList<TrieNode>();
        TrieNode currentNode = root;
        nodes.add(currentNode);

        for (int i = 0; i < element.length(); i++) {
            char symbol = element.charAt(i);
            if (currentNode.hasEdge(symbol)) {
                currentNode = currentNode.getChild(symbol);
                nodes.add(currentNode);
            } else {
                var newNode = new TrieNode(1);
                currentNode.addEdge(symbol, newNode);
                currentNode = newNode;
            }
        }

        if (!currentNode.isTerminal) {
            currentNode.isTerminal = true;
            for (TrieNode node : nodes) {
                node.size++;
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the trie contains the given element.
     * Takes linear time of element's length
     *
     * @return {@code true} if the element is presented in the trie
     * {@code false} otherwise
     * @throws IllegalArgumentException in case of null argument
     */
    public boolean contains(String element) {
        checkIsNull(element, NULL_ERROR);

        TrieNode currentNode = root;
        for (int i = 0; i < element.length(); i++) {
            char symbol = element.charAt(i);
            if (currentNode.hasEdge(symbol)) {
                currentNode = currentNode.getChild(symbol);
            } else {
                return false;
            }
        }
        return currentNode.isTerminal;
    }

    /**
     * Removes the element if it's presented in
     * the trie
     * Takes linear time of element's length
     *
     * @param element String to remove
     * @return {@code true} if the element was removed,
     * {@code false} if the element was not presented in
     * the trie
     * @throws IllegalArgumentException in case of null argument
     */
    public boolean remove(String element) {
        checkIsNull(element, NULL_ERROR);

        var nodes = new ArrayList<TrieNode>();
        TrieNode currentNode = root;
        nodes.add(root);
        for (int i = 0; i < element.length(); i++) {
            char symbol = element.charAt(i);
            if (currentNode.hasEdge(symbol)) {
                currentNode = currentNode.getChild(symbol);
                nodes.add(currentNode);
            } else {
                return false;
            }
        }
        if (currentNode.isTerminal) {
            for (TrieNode node : nodes) {
                node.size--;
            }
            currentNode.isTerminal = false;
            return true;
        }
        return false;
    }

    /** Returns the number of strings in the trie */
    public int size() {
        return root.size;
    }

    /** Checks if the trie has no strings */
    public boolean empty() {
        return root.size == 0;
    }

    /** Counts the number of element in the trie that starts with the given prefix */
    public int howManyStartWithPrefix(String prefix) {
        checkIsNull(prefix, NULL_ERROR);

        TrieNode currentNode = root;
        for (int i = 0; i < prefix.length(); i++) {
            char symbol = prefix.charAt(i);
            if (currentNode.hasEdge(symbol)) {
                currentNode = currentNode.getChild(symbol);
            } else {
                return 0;
            }
        }
        return currentNode.size;
    }

    /** {@inheritDoc} */
    @Override
    public void serialize(OutputStream out) throws IOException {
        checkIsNull(out, "The output stream can not be null");

        serializeNode(root, new DataOutputStream(out));
    }

    /** {@inheritDoc} */
    @Override
    public void deserialize(InputStream in) throws IOException {
        checkIsNull(in, "The input stream can not be null");

        root = deserializeNode(new DataInputStream(in));
        root.countSize();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        var otherTrie = (Trie) obj;
        return root.equals(otherTrie.root);
    }

    @Override
    public int hashCode() {
        return root.hashCode();
    }

    private void serializeNode(TrieNode node, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeBoolean(node.isTerminal);
        dataOutputStream.writeInt(node.next.size());
        for (var entry : node.next.entrySet()) {
            dataOutputStream.writeChar(entry.getKey());
            serializeNode(entry.getValue(), dataOutputStream);
        }
    }

    private TrieNode deserializeNode(DataInputStream dataInputStream) throws IOException {
        var newNode = new TrieNode();
        newNode.isTerminal = dataInputStream.readBoolean();
        int nextSize = dataInputStream.readInt();
        for (int i = 0; i < nextSize; i++) {
            char symbol = dataInputStream.readChar();
            newNode.addEdge(symbol, deserializeNode(dataInputStream));
        }
        return newNode;
    }

    private <T> void checkIsNull(T argument, String message) {
        if (argument == null) {
            throw new IllegalArgumentException(message);
        }
    }

    private static class TrieNode {
        private final HashMap<Character, TrieNode> next = new HashMap<>();
        private boolean isTerminal;
        private int size;

        private TrieNode() {
        }

        private TrieNode(int size) {
            this.size = size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }

            var otherNode = (TrieNode) obj;
            if (size != otherNode.size || isTerminal != otherNode.isTerminal) {
                return false;
            }

            return next.equals(otherNode.next);
        }

        @Override
        public int hashCode() {
            return Objects.hash(next, isTerminal, size);
        }

        /**
         * Updates the sizes of every node in the trie
         * @return the number of terminal nodes in the given
         * node's subtree
         */
        private int countSize() {
            size = 0;
            if (isTerminal) {
                size++;
            }

            for (TrieNode currentNode : next.values()) {
                size += currentNode.countSize();
            }
            return size;
        }

        private void addEdge(char symbol, TrieNode node) {
            next.put(symbol, node);
        }

        private TrieNode getChild(char symbol) {
            return next.get(symbol);
        }

        private boolean hasEdge(char symbol) {
            return next.containsKey(symbol);
        }
    }
}
