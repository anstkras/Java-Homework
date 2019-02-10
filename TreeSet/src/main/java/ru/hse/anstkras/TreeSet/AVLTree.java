package ru.hse.anstkras.TreeSet;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AVLTree<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private final Comparator<? super E> comparator;
    private final boolean isReverse;
    private TreeNode<E> root;
    private int size;

    private AVLTree(TreeNode<E> root, int size, Comparator<? super E> comparator, boolean isReverse) {
        this.root = root;
        this.size = size;
        this.comparator = comparator;
        this.isReverse = isReverse;
    }

    public AVLTree() {
        this(null);
    }

    public AVLTree(Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.isReverse = false;
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
        return new AVLIterator();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean add(E e) {
        if (root == null) {
            root = new TreeNode<E>(e);
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
            parent.left = new TreeNode<E>(e, parent);
        } else {
            parent.right = new TreeNode<E>(e, parent);
        }
        balance(parent);
        size++;
        return true;
    }

    @Override
    public Iterator<E> descendingIterator() {
        return descendingSet().iterator();
    }

    @Override
    public MyTreeSet<E> descendingSet() {
        return new AVLTree<E>(root, size, comparator, !isReverse);
    }

    @Override
    public E first() {
        if (root == null) {
            return null;
        }
        TreeNode<E> node = root;
        while (node.left(isReverse) != null)
            node = node.left(isReverse);
        return node.value;
    }

    @Override
    public E last() {
        if (root == null) {
            return null;
        }
        TreeNode<E> node = root;
        while (node.right(isReverse) != null)
            node = node.right(isReverse);
        return node.value;
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
        if (node.left(isReverse) == null && node.right(isReverse) == null) {
            if (node == root) {
                root = null;
            } else {
                TreeNode<E> parent = node.parent;
                if (parent.left(isReverse) == node) {
                    parent.left = null;
                } else {
                    parent.right = null;
                }
                balance(parent);
            }
            return;
        }

        if (node.left(isReverse) != null) {
            TreeNode<E> rightMostChild = node.left(isReverse);
            while (rightMostChild.right(isReverse) != null) {
                rightMostChild = rightMostChild.right(isReverse);
            }
            node.value = rightMostChild.value;
            removeNode(rightMostChild);
        } else {
            TreeNode<E> leftMostChild = node.right(isReverse);
            while (leftMostChild.left(isReverse) != null) {
                leftMostChild = leftMostChild.left(isReverse);
            }
            node.value = leftMostChild.value;
            removeNode(leftMostChild);
        }
    }

    private class AVLIterator implements Iterator<E> {
        private TreeNode<E> node;
        private TreeNode<E> lastNode;
        private final boolean isReverse;

        public AVLIterator() {
            this(false);
        }

        public AVLIterator(boolean isReverse) {
            this.isReverse = AVLTree.this.isReverse ^ isReverse;
            node = root;
            while (node.left(this.isReverse) != null) {
                node = node.left(this.isReverse);
            }
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
            lastNode = node;
            E valueToReturn = node.value;
            if (node.right(isReverse) != null) {
                node = node.right(isReverse);
                while (node.left(isReverse) != null) {
                    node = node.left(isReverse);
                }
                return valueToReturn;
            }

            while (node != root && node.parent.left(isReverse) != node) {
                node = node.parent;
            }
            node = node.parent;
            return valueToReturn;
        }

        @Override
        public void remove() {
            if (lastNode == null) {
                throw new IllegalStateException();
            }
            removeNode(lastNode);
            lastNode = null;
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

        private TreeNode<E> left(boolean isReverse) {
            return isReverse ? right : left;
        }

        private void left(boolean isReverse, TreeNode<E> node) {
            if (isReverse) {
                right = node;
            } else {
                left = node;
            }
        }

        private TreeNode<E> right(boolean isReverse) {
            return isReverse ? left : right;
        }

        private void right(boolean isReverse, TreeNode<E> node) {
            if (isReverse) {
                left = node;
            } else {
                right = node;
            }
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
}
