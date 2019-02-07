package ru.hse.anstkras.Trie;

import ru.hse.anstkras.Serializable.Serializable;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

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
        if (element == null) {
            throw new IllegalArgumentException(NULL_ERROR);
        }

        var nodes = new ArrayList<TrieNode>();
        TrieNode curNode = root;
        nodes.add(curNode);

        for (int i = 0; i < element.length(); i++) {
            char c = element.charAt(i);
            if (curNode.hasChild(c)) {
                curNode = curNode.getChild(c);
                nodes.add(curNode);
            } else {
                var newNode = new TrieNode();
                newNode.size = 1;
                curNode.addEdge(c, newNode);
                curNode = newNode;
            }
        }

        if (!curNode.isTerminal) {
            curNode.isTerminal = true;
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
        if (element == null) {
            throw new IllegalArgumentException(NULL_ERROR);
        }

        TrieNode curNode = root;
        for (int i = 0; i < element.length(); i++) {
            char c = element.charAt(i);
            if (curNode.hasChild(c)) {
                curNode = curNode.getChild(c);
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
     *
     * @param element String to remove
     * @return {@code true} if the element was removed,
     * {@code false} if the element was not presented in
     * the trie
     * @throws IllegalArgumentException in case of null argument
     */
    public boolean remove(String element) {
        if (element == null) {
            throw new IllegalArgumentException(NULL_ERROR);
        }

        var nodes = new ArrayList<TrieNode>();
        TrieNode curNode = root;
        nodes.add(root);
        for (int i = 0; i < element.length(); i++) {
            char c = element.charAt(i);
            if (curNode.hasChild(c)) {
                curNode = curNode.getChild(c);
                nodes.add(curNode);
            } else {
                return false;
            }
        }
        if (curNode.isTerminal) {
            for (TrieNode node : nodes) {
                node.size--;
            }
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
        if (prefix == null) {
            throw new IllegalArgumentException(NULL_ERROR);
        }

        TrieNode curNode = root;
        for (int i = 0; i < prefix.length(); i++) {
            char c = prefix.charAt(i);
            if (curNode.hasChild(c)) {
                curNode = curNode.getChild(c);
            } else {
                return 0;
            }
        }
        return curNode.size;
    }

    /** {@inheritDoc} */
    @Override
    public void serialize(OutputStream out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The out can not be null");
        }

        serializeNode(root, out);
    }

    /** {@inheritDoc} */
    @Override
    public void deserialize(InputStream in) throws IOException {
        if (in == null) {
            throw new IllegalArgumentException("The in can not be null");
        }

        root = deserializeNode(in);
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

    private void serializeNode(TrieNode node, OutputStream out) throws IOException {
        var dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeBoolean(node.isTerminal);
        dataOutputStream.writeInt(node.next.size());
        for (var entry : node.next.entrySet()) {
            dataOutputStream.writeChar(entry.getKey());
            serializeNode(entry.getValue(), out);
        }
    }

    private TrieNode deserializeNode(InputStream in) throws IOException {
        var dataInputStream = new DataInputStream(in);
        var newNode = new TrieNode();
        newNode.isTerminal = dataInputStream.readBoolean();
        int nextSize = dataInputStream.readInt();
        for (int i = 0; i < nextSize; i++) {
            char c = dataInputStream.readChar();
            newNode.addEdge(c, deserializeNode(in));
        }
        return newNode;
    }

    private static class TrieNode {
        private final HashMap<Character, TrieNode> next = new HashMap<>();
        private boolean isTerminal;
        private int size;

        private TrieNode() {
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

            if (next.size() != otherNode.next.size()) {
                return false;
            }
            for (var entry : next.entrySet()) {
                if (!otherNode.hasChild(entry.getKey())) {
                    return false;
                }
                if (!entry.getValue().equals(otherNode.getChild(entry.getKey()))) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hashCode = 0;
            for (var entry : next.entrySet()) {
                hashCode += entry.getKey().hashCode();
                hashCode += entry.getValue().hashCode();
            }
            return hashCode;
        }

        private int countSize() {
            size = 0;
            if (isTerminal) {
                size++;
            }

            for (TrieNode curNode : next.values()) {
                size += curNode.countSize();
            }
            return size;
        }

        private void addEdge(char symbol, TrieNode node) {
            next.put(symbol, node);
        }

        private TrieNode getChild(char symbol) {
            return next.get(symbol);
        }

        private boolean hasChild(char symbol) {
            return next.containsKey(symbol);
        }
    }
}
