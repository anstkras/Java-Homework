package ru.hse.anstkras.TreeSet;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class AVLTree<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private final TreeNode NULL = new TreeNode(null, 0, this.NULL, this.NULL);
    private final Comparator<? super E> comparator;
    private TreeNode root = NULL;
    private int size;

    public AVLTree() {
        comparator = null;
    }

    public AVLTree(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(E e) {
        if (root == NULL) {
            root = new TreeNode(e);
            return true;
        }

        TreeNode node = root;
        TreeNode parent = NULL;

        while (node != NULL) {
            if (compare(e, node.value) == 0) {
                return false;
            }

            parent = node;
            if (compare(e, node.value) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        if (compare(e, parent.value) < 0) {
            parent.left = new TreeNode(e, parent);
        } else {
            parent.right = new TreeNode(e, parent);
        }
        balance(parent);
        size++;
        return true;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return null;
    }

    @Override
    public TreeSet<E> descendingSet() {
        return null;
    }

    @Override
    public E first() {
        return null;
    }

    @Override
    public E last() {
        return null;
    }

    @Override
    public E lower(E e) {
        return null;
    }

    @Override
    public E floor(E e) {
        return null;
    }

    @Override
    public E ceiling(E e) {
        return null;
    }

    @Override
    public E higher(E e) {
        return null;
    }

    private TreeNode rotateRight(TreeNode node) {
        TreeNode pivot = node.left;
        pivot.parent = node.parent;

        node.left = pivot.right;
        if (node.left != NULL) {
            node.left.parent = node;
        }

        pivot.right = node;
        node.parent = pivot;

        pivot.updateParent(node);
        node.updateHeight();
        pivot.updateHeight();
        return pivot;
    }

    private TreeNode rotateLeft(TreeNode node) {
        TreeNode pivot = node.right;
        pivot.parent = node.parent;

        node.right = pivot.left;

        if (node.right != NULL) {
            node.right.parent = node;
        }

        pivot.left = node;
        node.parent = pivot;

        pivot.updateParent(node);
        node.updateHeight();
        pivot.updateHeight();

        return pivot;
    }

    private void balance(TreeNode node) {
        node.updateHeight();

        if (node.balanceFactor() == 2) {
            if (node.right.balanceFactor() < 0) {
                rotateRight(node.right);
            }
            node = rotateLeft(node);
        }
        if (node.balanceFactor() == -2) {
            if (node.left.balanceFactor() > 0) {
                node.left = rotateLeft(node.left);
            }
            node = rotateRight(node);
        }

        if (node.parent != NULL) {
            balance(node.parent);
        } else {
            root = node;
        }
    }

    private int compare(E value1, E value2) {
        if (comparator == null) {
            var comparableValue1 = (Comparable<E>) value1;
            return (comparableValue1.compareTo(value2));
        } else {
            return comparator.compare(value1, value2);
        }
    }

    private class AVLIterator implements Iterator {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            return null;
        }
    }

    private class TreeNode {
        private E value;
        private int height;
        private TreeNode left = NULL;
        private TreeNode right = NULL;
        private TreeNode parent = NULL;

        private TreeNode(E value, int height, TreeNode left, TreeNode right) {
            this.value = value;
            this.height = height;
            this.left = left;
            this.right = right;
        }

        private TreeNode(E value, TreeNode parent) {
            this.value = value;
            this.parent = parent;
        }

        private TreeNode(E value) {
            this.value = value;
        }

        private int balanceFactor() {
            return left.height - right.height;
        }

        private void updateHeight() {
            height = Math.max(left.height, right.height) + 1;
        }

        private void updateParent(TreeNode oldNode) {
            if (parent != NULL) {
                if (parent.right == oldNode) {
                    parent.right = this;
                } else {
                    parent.left = this;
                }
            }
        }

    }
}
