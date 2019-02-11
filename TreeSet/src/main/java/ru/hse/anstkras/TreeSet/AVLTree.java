package ru.hse.anstkras.TreeSet;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AVLTree<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private final Comparator<? super E> comparator;
    private TreeNode<E> root;
    private int size;

    private AVLTree(TreeNode<E> root, int size, Comparator<? super E> comparator) {
        this.root = root;
        this.size = size;
        this.comparator = comparator;
    }

    public AVLTree() {
        this(null);
    }

    public AVLTree(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean remove(Object value) {
        E eValue = (E) value;
        TreeNode<E> node = getByValue(eValue);
        if (node == null) {
            return false;
        } else {
            removeNode(node);
            return true;
        }
    }

    @Override
    public boolean contains(Object value) {
        return getByValue((E) value) != null;
    }

    private TreeNode<E> getByValue(E value) {
        if (root == null) {
            return null;
        }

        TreeNode<E> child = root;
        while (child != null) {
            TreeNode<E> node = child;
            if (compare(node.value, value) <= 0) {
                child = node.right;
            } else {
                child = node.left;
            }
            if (compare(value, node.value) == 0) {
                return node;
            }
        }
        return null;
    }

    @Override
    public Iterator<E> iterator() {
        return new AVLIterator<>(firstNode());
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(E e) {
        if (root == null) {
            root = new TreeNode<>(e);
            size++;
            return true;
        }

        TreeNode<E> node = root;
        TreeNode<E> parent = null;

        while (node != null) {
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
            parent.left = new TreeNode<>(e, parent);
        } else {
            parent.right = new TreeNode<>(e, parent);
        }
        balance(parent);
        size++;
        return true;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return new DescendingAVLIterator(lastNode());
    }

    @Override
    public MyTreeSet<E> descendingSet() {
        return new DescendingAVLTree(this);
    }

    @Override
    public E first() {
        TreeNode<E> result = firstNode();
        return result == null ? null : result.value;
    }

    private TreeNode<E> firstNode() {
        if (root == null) {
            return null;
        }
        TreeNode<E> node = root;
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public E last() {
        TreeNode<E> result = lastNode();
        return result == null ? null : result.value;
    }

    private TreeNode<E> lastNode() {
        if (root == null) {
            return null;
        }
        TreeNode<E> node = root;
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    @Override
    public E lower(E e) {
        TreeNode<E> node = root;
        while (node != null) {
            if (compare(node.value, e) < 0) {
                if (node.right == null) {
                    return node.value;
                } else {
                    node = node.right;
                }
            } else {
                if (node.left != null) {
                    node = node.left;
                } else {
                    TreeNode<E> parent = node.parent;
                    while (parent != null && parent.left == node) {
                        node = parent;
                        parent = parent.parent;
                    }
                    return parent == null ? null : parent.value;
                }
            }
        }
        return null;
    }

    @Override
    public E floor(E e) {
        TreeNode<E> node = root;
        while (node != null) {
            if (compare(node.value, e) == 0) {
                return node.value;
            }
            if (compare(node.value, e) < 0) {
                if (node.right == null) {
                    return node.value;
                } else {
                    node = node.right;
                }
            } else {
                if (node.left != null) {
                    node = node.left;
                } else {
                    TreeNode<E> parent = node.parent;
                    while (parent != null && parent.left == node) {
                        node = parent;
                        parent = parent.parent;
                    }
                    return parent == null ? null : parent.value;
                }
            }
        }
        return null;
    }

    @Override
    public E ceiling(E e) {
        TreeNode<E> node = root;
        while (node != null) {
            if (compare(node.value, e) == 0) {
                return node.value;
            }
            if (compare(node.value, e) > 0) {
                if (node.left == null) {
                    return node.value;
                } else {
                    node = node.left;
                }
            } else {
                if (node.right != null) {
                    node = node.right;
                } else {
                    TreeNode<E> parent = node.parent;
                    while (parent != null && parent.right == node) {
                        node = parent;
                        parent = parent.parent;
                    }
                    return parent == null ? null : parent.value;
                }
            }
        }
        return null;
    }

    @Override
    public E higher(E e) {
        TreeNode<E> node = root;
        while (node != null) {
            if (compare(node.value, e) > 0) {
                if (node.left == null) {
                    return node.value;
                } else {
                    node = node.left;
                }
            } else {
                if (node.right != null) {
                    node = node.right;
                } else {
                    TreeNode<E> parent = node.parent;
                    while (parent != null && parent.right == node) {
                        node = parent;
                        parent = parent.parent;
                    }
                    return parent == null ? null : parent.value;
                }
            }
        }
        return null;
    }

    private TreeNode<E> rotateRight(TreeNode<E> node) {
        TreeNode<E> pivot = node.left;
        pivot.parent = node.parent;

        node.left = pivot.right;
        if (node.left != null) {
            node.left.parent = node;
        }

        pivot.right = node;
        node.parent = pivot;

        pivot.updateParent(node);
        updateHeight(node);
        updateHeight(pivot);
        return pivot;
    }

    private TreeNode<E> rotateLeft(TreeNode<E> node) {
        TreeNode<E> pivot = node.right;
        pivot.parent = node.parent;

        node.right = pivot.left;

        if (node.right != null) {
            node.right.parent = node;
        }

        pivot.left = node;
        node.parent = pivot;

        pivot.updateParent(node);
        updateHeight(node);
        updateHeight(pivot);

        return pivot;
    }

    private void balance(TreeNode<E> node) {
        updateHeight(node);
        if (balanceFactor(node) == 2) {
            if (balanceFactor(node.right) < 0) {
                rotateRight(node.right);
            }
            node = rotateLeft(node);
        }
        if (balanceFactor(node) == -2) {
            if (balanceFactor(node.left) > 0) {
                node.left = rotateLeft(node.left);
            }
            node = rotateRight(node);
        }

        if (node.parent != null) {
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

    private int height(TreeNode<E> node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(TreeNode<E> node) {
        if (node == null) {
            return;
        }
        node.height = Math.max(height(node.left), height(node.right)) + 1;
    }

    private int balanceFactor(TreeNode<E> node) {
        if (node == null) {
            return 0;
        }
        return height(node.right) - height(node.left);
    }

    private void removeNode(TreeNode<E> node) {
        if (node.left == null && node.right == null) {
            if (node == root) {
                root = null;
            } else {
                TreeNode<E> parent = node.parent;
                if (parent.left == node) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
                balance(parent);
            }
            return;
        }

        if (node.left != null) {
            TreeNode<E> rightMostChild = node.left;
            while (rightMostChild.right != null) {
                rightMostChild = rightMostChild.right;
            }
            node.value = rightMostChild.value;
            removeNode(rightMostChild);
        } else {
            TreeNode<E> leftMostChild = node.right;
            while (leftMostChild.left != null) {
                leftMostChild = leftMostChild.left;
            }
            node.value = leftMostChild.value;
            removeNode(leftMostChild);
        }
    }

    private static class TreeNode<E> {
        private E value;
        private int height;
        private TreeNode<E> left;
        private TreeNode<E> right;
        private TreeNode<E> parent;

        private TreeNode(E value, int height, TreeNode<E> left, TreeNode<E> right) {
            this.value = value;
            this.height = height;
            this.left = left;
            this.right = right;
        }

        private TreeNode(E value, TreeNode<E> parent) {
            this.value = value;
            this.parent = parent;
        }

        private TreeNode(E value) {
            this.value = value;
        }

        private void updateParent(TreeNode<E> oldNode) {
            if (parent != null) {
                if (parent.right == oldNode) {
                    parent.right = this;
                } else {
                    parent.left = this;
                }
            }
        }
    }

    private class DescendingAVLTree extends AbstractSet<E> implements MyTreeSet<E> {
        private AVLTree<E> tree;

        private DescendingAVLTree(AVLTree<E> tree) {
            this.tree = tree;
        }

        @Override
        public Iterator<E> iterator() {
            return tree.descendingIterator();
        }

        @Override
        public int size() {
            return tree.size;
        }

        @Override
        public Iterator<E> descendingIterator() {
            return tree.iterator();
        }

        @Override
        public MyTreeSet<E> descendingSet() {
            return tree;
        }

        @Override
        public E first() {
            return tree.last();
        }

        @Override
        public E last() {
            return tree.first();
        }

        @Override
        public E lower(E e) {
            return AVLTree.this.higher(e);
        }

        @Override
        public E floor(E e) {
            return AVLTree.this.ceiling(e);
        }

        @Override
        public E ceiling(E e) {
            return AVLTree.this.floor(e);
        }

        @Override
        public E higher(E e) {
            return AVLTree.this.lower(e);
        }
    }

    private class AVLIterator<E> implements Iterator<E> {
        private TreeNode<E> node;

        public AVLIterator(TreeNode<E> startNode) {
            node = startNode;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E valueToReturn = node.value;
            if (node.right != null) {
                node = node.right;
                while (node.left != null) {
                    node = node.left;
                }
                return valueToReturn;
            }

            while (node != root && node.parent.left != node) {
                node = node.parent;
            }
            node = node.parent;
            return valueToReturn;
        }

        E prev() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            E valueToReturn = node.value;
            if (node.left != null) {
                node = node.left;
                while (node.right != null) {
                    node = node.right;
                }
                return valueToReturn;
            }

            while (node != root && node.parent.right != node) {
                node = node.parent;
            }
            node = node.parent;
            return valueToReturn;
        }
    }

    private class DescendingAVLIterator extends AVLIterator<E> {

        public DescendingAVLIterator(TreeNode<E> startNode) {
            super(startNode);
        }

        @Override
        public E next() {
            return prev();
        }
    }
}
