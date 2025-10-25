package app.repositories;

import app.model.Cart;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Test class for CartRepository.
 * Tests all public methods including CRUD operations, initialization, and edge cases.
 */
public class CartRepositoryTest {

    private CartRepository repository;
    private Cart cart1;
    private Cart cart2;
    private Cart cart3;

    @Before
    public void setUp() {
        repository = new CartRepository();

        cart1 = new Cart(1, "Small Cart", 5.5);
        cart2 = new Cart(2, "Medium Cart", 10.0);
        cart3 = new Cart(3, "Large Cart", 20.0);
    }

    @Test
    public void testAddCart() {
        repository.addCart(cart1);

        List<Cart> carts = repository.getAllCarts();
        assertEquals(1, carts.size());
        assertTrue(carts.contains(cart1));
    }

    @Test
    public void testAddCartWithNull() {
        repository.addCart(null);

        List<Cart> carts = repository.getAllCarts();
        assertEquals(0, carts.size());
    }

    @Test
    public void testAddCartWithNullId() {
        Cart cartWithNullId = new Cart(null, "Test Cart", 15.0);
        repository.addCart(cartWithNullId);

        List<Cart> carts = repository.getAllCarts();
        assertEquals(0, carts.size());
    }

    @Test
    public void testAddDuplicateCart() {
        repository.addCart(cart1);
        repository.addCart(cart1);

        List<Cart> carts = repository.getAllCarts();
        assertEquals(1, carts.size());
    }

    @Test
    public void testGetAllCarts() {
        repository.addCart(cart1);
        repository.addCart(cart2);
        repository.addCart(cart3);

        List<Cart> carts = repository.getAllCarts();

        assertEquals(3, carts.size());
        assertTrue(carts.contains(cart1));
        assertTrue(carts.contains(cart2));
        assertTrue(carts.contains(cart3));
    }

    @Test
    public void testGetAllCartsEmpty() {
        List<Cart> carts = repository.getAllCarts();

        assertNotNull(carts);
        assertEquals(0, carts.size());
    }

    @Test
    public void testGetCartByIdFound() {
        repository.addCart(cart1);
        repository.addCart(cart2);

        Cart result = repository.getCartById(1);

        assertNotNull(result);
        assertEquals(cart1, result);
    }

    @Test
    public void testGetCartByIdNotFound() {
        repository.addCart(cart1);

        Cart result = repository.getCartById(999);

        assertNull(result);
    }

    @Test
    public void testUpdateCartSuccess() {
        repository.addCart(cart1);

        Cart updatedCart = new Cart(1, "Updated Cart", 7.5);

        boolean result = repository.updateCart(1, updatedCart);

        assertTrue(result);
        Cart retrievedCart = repository.getCartById(1);
        assertNotNull(retrievedCart);
        assertEquals("Updated Cart", retrievedCart.getName());
        assertEquals(7.5, retrievedCart.getMaxWeight(), 0.01);
    }

    @Test
    public void testUpdateCartNotFound() {
        repository.addCart(cart1);

        boolean result = repository.updateCart(999, cart2);

        assertFalse(result);
    }

    @Test
    public void testDeleteCartSuccess() {
        repository.addCart(cart1);
        repository.addCart(cart2);
        repository.addCart(cart3);

        boolean result = repository.deleteCart(2);

        assertTrue(result);
        assertEquals(2, repository.getAllCarts().size());
        assertNull(repository.getCartById(2));
    }

    @Test
    public void testDeleteCartNotFound() {
        repository.addCart(cart1);

        boolean result = repository.deleteCart(999);

        assertFalse(result);
        assertEquals(1, repository.getAllCarts().size());
    }

    @Test
    public void testGetNextCartId() {
        repository.addCart(cart1);
        repository.addCart(cart2);

        Integer nextId = repository.getNextCartId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(3), nextId);
    }

    @Test
    public void testGetNextCartIdEmpty() {
        Integer nextId = repository.getNextCartId();

        assertNotNull(nextId);
        assertEquals(Integer.valueOf(1), nextId);
    }

    @Test
    public void testGetNextCartIdWithNullIds() {
        repository.addCart(cart1);

        Integer nextId = repository.getNextCartId();

        assertEquals(Integer.valueOf(2), nextId);
    }

    @Test
    public void testClear() {
        repository.addCart(cart1);
        repository.addCart(cart2);
        repository.addCart(cart3);
        assertEquals(3, repository.getAllCarts().size());

        repository.clear();

        assertEquals(0, repository.getAllCarts().size());
    }

    @Test
    public void testInitializeDefaultCarts() {
        repository.initializeDefaultCarts();

        List<Cart> carts = repository.getAllCarts();
        assertEquals(5, carts.size());

        Cart smallCart = repository.getCartById(1);
        assertNotNull(smallCart);
        assertEquals("Carrinho Pequeno", smallCart.getName());
        assertEquals(5.5, smallCart.getMaxWeight(), 0.01);

        Cart mediumCart = repository.getCartById(2);
        assertNotNull(mediumCart);
        assertEquals("Carrinho Médio", mediumCart.getName());
        assertEquals(10.0, mediumCart.getMaxWeight(), 0.01);

        Cart largeCart = repository.getCartById(3);
        assertNotNull(largeCart);
        assertEquals("Carrinho Grande", largeCart.getName());
        assertEquals(20.0, largeCart.getMaxWeight(), 0.01);

        Cart extraLargeCart = repository.getCartById(4);
        assertNotNull(extraLargeCart);
        assertEquals("Carrinho Extra Grande", extraLargeCart.getName());
        assertEquals(35.0, extraLargeCart.getMaxWeight(), 0.01);

        Cart forklift = repository.getCartById(5);
        assertNotNull(forklift);
        assertEquals("Empilhadora", forklift.getName());
        assertEquals(50.0, forklift.getMaxWeight(), 0.01);
    }

    @Test
    public void testConstructor() {
        CartRepository newRepository = new CartRepository();

        assertNotNull(newRepository);
        assertNotNull(newRepository.getAllCarts());
    }
}

