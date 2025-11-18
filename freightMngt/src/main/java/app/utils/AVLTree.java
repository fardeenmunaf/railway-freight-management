package app.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic AVL Tree implementation for efficient searching and range queries.
 * Maintains balance through rotations after insertions.
 *
 * @param <K> the type of keys (must be Comparable)
 * @param <V> the type of values
 * @author G113 2DK
 * @version 1.0
 */
public class AVLTree<K extends Comparable<K>, V> {

    private Node<K, V> root;

    /**
     * Inserts a key-value pair into the tree.
     * If the key already exists, the value is added to the list.
     *
     * @param key the key to insert
     * @param value the value to insert
     */
    public void insert(K key, V value) {
        root = insert(root, key, value);
    }

    private Node<K, V> insert(Node<K, V> node, K key, V value) {
        if (node == null) {
            return new Node<>(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = insert(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insert(node.right, key, value);
        } else {
            node.values.add(value);
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        // Left-Left case
        if (balance > 1 && key.compareTo(node.left.key) < 0) {
            return rotateRight(node);
        }

        // Right-Right case
        if (balance < -1 && key.compareTo(node.right.key) > 0) {
            return rotateLeft(node);
        }

        // Left-Right case
        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Right-Left case
        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    /**
     * Searches for values associated with the given key.
     *
     * @param key the key to search for
     * @return list of values, or empty list if not found
     */
    public List<V> search(K key) {
        Node<K, V> node = search(root, key);
        return node != null ? new ArrayList<>(node.values) : new ArrayList<>();
    }

    private Node<K, V> search(Node<K, V> node, K key) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);
        if (cmp < 0) return search(node.left, key);
        if (cmp > 0) return search(node.right, key);
        return node;
    }

    /**
     * Performs a range search returning all values with keys in [from, to].
     *
     * @param from the start of the range (inclusive)
     * @param to the end of the range (inclusive)
     * @return list of all values in the range
     */
    public List<V> rangeSearch(K from, K to) {
        List<V> result = new ArrayList<>();
        rangeSearch(root, from, to, result);
        return result;
    }

    private void rangeSearch(Node<K, V> node, K from, K to, List<V> result) {
        if (node == null) return;

        int cmpFrom = from.compareTo(node.key);
        int cmpTo = to.compareTo(node.key);

        if (cmpFrom < 0) {
            rangeSearch(node.left, from, to, result);
        }

        if (cmpFrom <= 0 && cmpTo >= 0) {
            result.addAll(node.values);
        }

        if (cmpTo > 0) {
            rangeSearch(node.right, from, to, result);
        }
    }

    /**
     * Returns all values in the tree in sorted order by key.
     *
     * @return list of all values in sorted order
     */
    public List<V> inOrderTraversal() {
        List<V> result = new ArrayList<>();
        inOrderTraversal(root, result);
        return result;
    }

    private void inOrderTraversal(Node<K, V> node, List<V> result) {
        if (node != null) {
            inOrderTraversal(node.left, result);
            result.addAll(node.values);
            inOrderTraversal(node.right, result);
        }
    }

    private Node<K, V> rotateRight(Node<K, V> y) {
        Node<K, V> x = y.left;
        Node<K, V> T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    private Node<K, V> rotateLeft(Node<K, V> x) {
        Node<K, V> y = x.right;
        Node<K, V> T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    private int height(Node<K, V> node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(Node<K, V> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private static class Node<K, V> {
        K key;
        List<V> values;
        Node<K, V> left, right;
        int height;

        Node(K key, V value) {
            this.key = key;
            this.values = new ArrayList<>();
            this.values.add(value);
            this.height = 1;
        }
    }
}
