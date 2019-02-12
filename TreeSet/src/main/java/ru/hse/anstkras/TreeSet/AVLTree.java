package ru.hse.anstkras.TreeSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AVLTree<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private final Comparator<? super E> comparator;
    private TreeNode<E> root;
    private int size;
    private MyTreeSet<E> cashedDescendingTree;

    public AVLTree() {
        this(null);
    }

    public AVLTree(Comparator<? super E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public boolean remove(@NotNull Object value) {
        TreeNode<E> node = getByValue(value);
        if (node == null) {
            return false;
        } else {
            removeNode(node);
            return true;
        }
    }

    @Override
    public boolean contains(@NotNull Object value) {
        return getByValue(value) != null;
    }

    @Nullable
    private TreeNode<E> getByValue(@Nullable Object value) {
        if (root == null) {
            return null;
        }

        TreeNode<E> child = root;
        while (child != null) {
            TreeNode<E> node = child;
            if (compareObjectToE(value, node.value) >= 0) {
                child = node.right;
            } else {
                child = node.left;
            }
            if (compareObjectToE(value, node.value) == 0) {
                return node;
            }
        }
        return null;
    }

    @Override
    @NotNull
    public Iterator<E> iterator() {
        return new AVLIterator<>(firstNode());
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(@NotNull E e) {
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
    @NotNull
    public Iterator<E> descendingIterator() {
        return new DescendingAVLIterator(lastNode());
    }

    @Override
    @NotNull
    public MyTreeSet<E> descendingSet() {
        if (cashedDescendingTree == null) {
            cashedDescendingTree = new DescendingAVLTree(this);
        }
        return cashedDescendingTree;
    }

    @Override
    @Nullable
    public E first() {
        TreeNode<E> result = firstNode();
        return result == null ? null : result.value;
    }

    @Nullable
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
    @Nullable
    public E last() {
        TreeNode<E> result = lastNode();
        return result == null ? null : result.value;
    }

    @Nullable
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
    @Nullable
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
                    while (node.parent != null && node.parent.left == node) {
                        node = node.parent;
                    }
                    return node.parent == null ? null : node.parent.value;
                }
            }
        }
        return null;
    }

    @Override
    @Nullable
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
                    while (node.parent != null && node.parent.left == node) {
                        node = node.parent;
                    }
                    return node.parent == null ? null : node.parent.value;
                }
            }
        }
        return null;
    }

    @Override
    @Nullable
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
                    while (node.parent != null && node.parent.right == node) {
                        node = node.parent;
                    }
                    return node.parent == null ? null : node.parent.value;
                }
            }
        }
        return null;
    }

    @Override
    @Nullable
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
                    while (node.parent != null && node.parent.right == node) {
                        node = node.parent;
                    }
                    return node.parent == null ? null : node.parent.value;
                }
            }
        }
        return null;
    }

    private TreeNode<E> rotateRight(@NotNull TreeNode<E> node) {
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

    private TreeNode<E> rotateLeft(@NotNull TreeNode<E> node) {
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

    private void balance(@NotNull TreeNode<E> node) {
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
            @SuppressWarnings("unchecked")
            var comparableValue1 = (Comparable<? super E>) value1;
            return (comparableValue1.compareTo(value2));
        } else {
            return comparator.compare(value1, value2);
        }
    }

    private int compareObjectToE(Object value1, E value2) {
        if (comparator == null) {
            @SuppressWarnings("unchecked")
            var comparableValue1 = (Comparable<? super E>) value1;
            return (comparableValue1.compareTo(value2));
        } else {
            @SuppressWarnings("unchecked")
            E eValue1 = (E) value1;
            return comparator.compare(eValue1, value2);
        }
    }

    private int height(@Nullable TreeNode<E> node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(@Nullable TreeNode<E> node) {
        if (node == null) {
            return;
        }
        node.height = Math.max(height(node.left), height(node.right)) + 1;
    }

    private int balanceFactor(@Nullable TreeNode<E> node) {
        if (node == null) {
            return 0;
        }
        return height(node.right) - height(node.left);
    }

    private void removeNode(@NotNull TreeNode<E> node) {
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

        private TreeNode(E value, TreeNode<E> parent) {
            this.value = value;
            this.parent = parent;
        }

        private TreeNode(E value) {
            this.value = value;
        }

        private void updateParent(@Nullable TreeNode<E> oldNode) {
            if (parent != null) {
                if (parent.right == oldNode) {
                    parent.right = this;
                } else {
                    parent.left = this;
                }
            }
        }
    }

    private static class DescendingAVLTree<E> extends AbstractSet<E> implements MyTreeSet<E> {
        private final AVLTree<E> tree;

        private DescendingAVLTree(@NotNull AVLTree<E> tree) {
            this.tree = tree;
        }

        @Override
        @NotNull
        public Iterator<E> iterator() {
            return tree.descendingIterator();
        }

        @Override
        public int size() {
            return tree.size;
        }

        @Override
        @NotNull
        public Iterator<E> descendingIterator() {
            return tree.iterator();
        }

        @Override
        @NotNull
        public MyTreeSet<E> descendingSet() {
            return tree;
        }

        @Override
        @Nullable
        public E first() {
            return tree.last();
        }

        @Override
        @Nullable
        public E last() {
            return tree.first();
        }

        @Override
        @Nullable
        public E lower(E e) {
            return tree.higher(e);
        }

        @Override
        @Nullable
        public E floor(E e) {
            return tree.ceiling(e);
        }

        @Override
        @Nullable
        public E ceiling(E e) {
            return tree.floor(e);
        }

        @Override
        @Nullable
        public E higher(E e) {
            return tree.lower(e);
        }
    }

    private class AVLIterator<E> implements Iterator<E> {
        private TreeNode<E> node;

        private AVLIterator(@Nullable TreeNode<E> startNode) {
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

        private DescendingAVLIterator(@Nullable TreeNode<E> startNode) {
            super(startNode);
        }

        @Override
        public E next() {
            return prev();
        }
    }
}
