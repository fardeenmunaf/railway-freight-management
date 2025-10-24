package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for PickWave model.
 * Tests constructors, getters, setters, and toString with 100% coverage.
 */
public class PickWaveTest {

    private PickWave pickWave;

    @Before
    public void setUp() {
        pickWave = new PickWave();
    }

    @Test
    public void testDefaultConstructor() {
        PickWave defaultPickWave = new PickWave();

        assertNull("Pick Wave ID should be null", defaultPickWave.getPickWaveId());
        assertNull("Cart should be null", defaultPickWave.getCart());
        assertNull("Strategy should be null", defaultPickWave.getStrategy());
        assertNull("Status should be null", defaultPickWave.getStatus());
    }

    @Test
    public void testParameterizedConstructor() {
        Cart cart = new Cart();
        PickWave paramPickWave = new PickWave(1, cart, "FIFO", "Pending");

        assertEquals("Pick Wave ID should be 1", Integer.valueOf(1), paramPickWave.getPickWaveId());
        assertEquals("Cart should match", cart, paramPickWave.getCart());
        assertEquals("Strategy should be 'FIFO'", "FIFO", paramPickWave.getStrategy());
        assertEquals("Status should be 'Pending'", "Pending", paramPickWave.getStatus());
    }

    @Test
    public void testSetAndGetPickWaveId() {
        pickWave.setPickWaveId(10);

        assertEquals("Pick Wave ID should be 10", Integer.valueOf(10), pickWave.getPickWaveId());
    }

    @Test
    public void testSetAndGetCart() {
        Cart cart = new Cart();
        pickWave.setCart(cart);

        assertEquals("Cart should match", cart, pickWave.getCart());
    }

    @Test
    public void testSetAndGetStrategy() {
        pickWave.setStrategy("FEFO");

        assertEquals("Strategy should be 'FEFO'", "FEFO", pickWave.getStrategy());
    }

    @Test
    public void testSetAndGetStatus() {
        pickWave.setStatus("In Progress");

        assertEquals("Status should be 'In Progress'", "In Progress", pickWave.getStatus());
    }

    @Test
    public void testSetPickWaveIdToNull() {
        pickWave.setPickWaveId(5);
        pickWave.setPickWaveId(null);

        assertNull("Pick Wave ID should be null", pickWave.getPickWaveId());
    }

    @Test
    public void testSetCartToNull() {
        Cart cart = new Cart();
        pickWave.setCart(cart);
        pickWave.setCart(null);

        assertNull("Cart should be null", pickWave.getCart());
    }

    @Test
    public void testSetStrategyToNull() {
        pickWave.setStrategy("LIFO");
        pickWave.setStrategy(null);

        assertNull("Strategy should be null", pickWave.getStrategy());
    }

    @Test
    public void testSetStatusToNull() {
        pickWave.setStatus("Completed");
        pickWave.setStatus(null);

        assertNull("Status should be null", pickWave.getStatus());
    }

    @Test
    public void testConstructorWithNullValues() {
        PickWave nullPickWave = new PickWave(null, null, null, null);

        assertNull("Pick Wave ID should be null", nullPickWave.getPickWaveId());
        assertNull("Cart should be null", nullPickWave.getCart());
        assertNull("Strategy should be null", nullPickWave.getStrategy());
        assertNull("Status should be null", nullPickWave.getStatus());
    }

    @Test
    public void testToString() {
        Cart cart = new Cart();
        pickWave.setPickWaveId(7);
        pickWave.setCart(cart);
        pickWave.setStrategy("Zone Picking");
        pickWave.setStatus("Active");

        String result = pickWave.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain pickWaveId", result.contains("pickWaveId=7"));
        assertTrue("toString should contain strategy", result.contains("strategy='Zone Picking'"));
        assertTrue("toString should contain status", result.contains("status='Active'"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = pickWave.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'PickWave{'", result.contains("PickWave{"));
    }
}

