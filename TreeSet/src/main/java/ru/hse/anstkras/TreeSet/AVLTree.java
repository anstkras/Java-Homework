package ru.hse.anstkras.TreeSet;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.TreeSet;

public class AVLTree<E> extends AbstractSet<E> implements MyTreeSet<E> {
    private TreeNode root;
    private final TreeNode NULL = new TreeNode(null, 0, this.NULL, this.NULL);


    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
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
