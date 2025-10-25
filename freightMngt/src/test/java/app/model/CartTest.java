package app.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for Cart model.
 * Tests constructors, getters, setters, and business logic.
 */
public class CartTest {

    private Cart cart;

    @Before
    public void setUp() {
        cart = new Cart();
    }

    @Test
    public void testDefaultConstructor() {
        Cart defaultCart = new Cart();

        assertNull("Cart ID should be null", defaultCart.getCartId());
        assertNull("Name should be null", defaultCart.getName());
        assertNull("Max weight should be null", defaultCart.getMaxWeight());
    }

    @Test
    public void testParameterizedConstructor() {
        Cart paramCart = new Cart(1, "Small Cart", 5.5);

        assertEquals("Cart ID should be 1", Integer.valueOf(1), paramCart.getCartId());
        assertEquals("Name should be 'Small Cart'", "Small Cart", paramCart.getName());
        assertEquals("Max weight should be 5.5", Double.valueOf(5.5), paramCart.getMaxWeight());
    }

    @Test
    public void testSetAndGetCartId() {
        cart.setCartId(10);

        assertEquals("Cart ID should be 10", Integer.valueOf(10), cart.getCartId());
    }

    @Test
    public void testSetAndGetName() {
        cart.setName("Medium Cart");

        assertEquals("Name should be 'Medium Cart'", "Medium Cart", cart.getName());
    }

    @Test
    public void testSetAndGetMaxWeight() {
        cart.setMaxWeight(15.0);

        assertEquals("Max weight should be 15.0", Double.valueOf(15.0), cart.getMaxWeight());
    }

    @Test
    public void testSetCartIdToNull() {
        cart.setCartId(5);
        cart.setCartId(null);

        assertNull("Cart ID should be null", cart.getCartId());
    }

    @Test
    public void testSetNameToNull() {
        cart.setName("Test Cart");
        cart.setName(null);

        assertNull("Name should be null", cart.getName());
    }

    @Test
    public void testSetMaxWeightToNull() {
        cart.setMaxWeight(20.0);
        cart.setMaxWeight(null);

        assertNull("Max weight should be null", cart.getMaxWeight());
    }

    @Test
    public void testSetMaxWeightToZero() {
        cart.setMaxWeight(0.0);

        assertEquals("Max weight should be 0.0", Double.valueOf(0.0), cart.getMaxWeight());
    }

    @Test
    public void testSetMaxWeightToNegative() {
        cart.setMaxWeight(-10.0);

        assertEquals("Max weight should be -10.0", Double.valueOf(-10.0), cart.getMaxWeight());
    }

    @Test
    public void testMultipleSettersAndGetters() {
        cart.setCartId(3);
        cart.setName("Large Cart");
        cart.setMaxWeight(50.0);

        assertEquals("Cart ID should be 3", Integer.valueOf(3), cart.getCartId());
        assertEquals("Name should be 'Large Cart'", "Large Cart", cart.getName());
        assertEquals("Max weight should be 50.0", Double.valueOf(50.0), cart.getMaxWeight());
    }

    @Test
    public void testConstructorWithNullValues() {
        Cart nullCart = new Cart(null, null, null);

        assertNull("Cart ID should be null", nullCart.getCartId());
        assertNull("Name should be null", nullCart.getName());
        assertNull("Max weight should be null", nullCart.getMaxWeight());
    }

    @Test
    public void testConstructorWithEmptyName() {
        Cart emptyNameCart = new Cart(1, "", 10.0);

        assertEquals("Cart ID should be 1", Integer.valueOf(1), emptyNameCart.getCartId());
        assertEquals("Name should be empty string", "", emptyNameCart.getName());
        assertEquals("Max weight should be 10.0", Double.valueOf(10.0), emptyNameCart.getMaxWeight());
    }

    @Test
    public void testToString() {
        cart.setCartId(5);
        cart.setName("Test Cart");
        cart.setMaxWeight(25.5);

        String result = cart.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain cartId", result.contains("cartId=5"));
        assertTrue("toString should contain name", result.contains("name='Test Cart'"));
        assertTrue("toString should contain maxWeight", result.contains("maxWeight=25.5"));
    }

    @Test
    public void testToStringWithNullValues() {
        String result = cart.toString();

        assertNotNull("toString should not return null", result);
        assertTrue("toString should contain 'Cart{'", result.contains("Cart{"));
    }
}

