package app.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AVLTreeTest {

    private AVLTree<Integer, String> tree;

    @Before
    public void setUp() {
        tree = new AVLTree<>();
    }

    @Test
    public void testInsertAndSearch() {
        tree.insert(10, "Ten");

        List<String> results = tree.search(10);

        assertEquals(1, results.size());
        assertEquals("Ten", results.get(0));
    }

    @Test
    public void testInsertMultipleValuesForSameKey() {
        tree.insert(5, "Value1");
        tree.insert(5, "Value2");
        tree.insert(5, "Value3");

        List<String> results = tree.search(5);

        assertEquals(3, results.size());
        assertTrue(results.contains("Value1"));
        assertTrue(results.contains("Value2"));
        assertTrue(results.contains("Value3"));
    }

    @Test
    public void testSearchNonExistentKey() {
        tree.insert(10, "Ten");

        List<String> results = tree.search(999);

        assertTrue("Should return empty list for non-existent key", results.isEmpty());
    }

    @Test
    public void testBalancing() {
        for (int i = 1; i <= 10; i++) {
            tree.insert(i, "Value" + i);
        }

        for (int i = 1; i <= 10; i++) {
            List<String> results = tree.search(i);
            assertFalse("Should find all inserted values", results.isEmpty());
        }
    }

    @Test
    public void testRangeSearch() {
        tree.insert(5, "Five");
        tree.insert(3, "Three");
        tree.insert(7, "Seven");
        tree.insert(2, "Two");
        tree.insert(4, "Four");
        tree.insert(6, "Six");
        tree.insert(8, "Eight");

        List<String> results = tree.rangeSearch(3, 7);

        assertEquals(5, results.size());
        assertTrue(results.contains("Three"));
        assertTrue(results.contains("Four"));
        assertTrue(results.contains("Five"));
        assertTrue(results.contains("Six"));
        assertTrue(results.contains("Seven"));
    }

    @Test
    public void testRangeSearchSingleValue() {
        tree.insert(10, "Ten");

        List<String> results = tree.rangeSearch(10, 10);

        assertEquals(1, results.size());
        assertEquals("Ten", results.get(0));
    }

    @Test
    public void testRangeSearchNoResults() {
        tree.insert(5, "Five");
        tree.insert(10, "Ten");

        List<String> results = tree.rangeSearch(6, 9);

        assertTrue("Should return empty list when no values in range", results.isEmpty());
    }

    @Test
    public void testInOrderTraversal() {
        tree.insert(5, "Five");
        tree.insert(3, "Three");
        tree.insert(7, "Seven");
        tree.insert(2, "Two");
        tree.insert(4, "Four");

        List<String> results = tree.inOrderTraversal();

        assertEquals(5, results.size());
        assertEquals("Two", results.get(0));
        assertEquals("Three", results.get(1));
        assertEquals("Four", results.get(2));
        assertEquals("Five", results.get(3));
        assertEquals("Seven", results.get(4));
    }

    @Test
    public void testInOrderTraversalEmpty() {
        List<String> results = tree.inOrderTraversal();

        assertTrue("Should return empty list for empty tree", results.isEmpty());
    }

    @Test
    public void testStringKeyTree() {
        AVLTree<String, Integer> stringTree = new AVLTree<>();

        stringTree.insert("apple", 1);
        stringTree.insert("banana", 2);
        stringTree.insert("cherry", 3);

        List<Integer> results = stringTree.search("banana");

        assertEquals(1, results.size());
        assertEquals(Integer.valueOf(2), results.get(0));
    }

    @Test
    public void testRangeSearchWithStrings() {
        AVLTree<String, String> stringTree = new AVLTree<>();

        stringTree.insert("alice", "Alice");
        stringTree.insert("bob", "Bob");
        stringTree.insert("charlie", "Charlie");
        stringTree.insert("david", "David");

        List<String> results = stringTree.rangeSearch("bob", "david");

        assertEquals(3, results.size());
        assertTrue(results.contains("Bob"));
        assertTrue(results.contains("Charlie"));
        assertTrue(results.contains("David"));
    }
}
