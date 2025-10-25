package app.controller;

import app.model.Cart;
import app.repositories.Repositories;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class CartControllerTest {

    private CartController controller;
    private Cart cart1;
    private Cart cart2;

    @Before
    public void setUp() {
        Repositories.getInstance().getCartRepository().clear();

        controller = new CartController();
        cart1 = new Cart(1, "Cart A", 100.0);
        cart2 = new Cart(2, "Cart B", 150.0);
    }

    @Test
    public void testCreateCart() {
        Cart result = controller.createCart(cart1);

        assertNotNull(result);
        assertEquals(cart1, result);
        assertEquals(1, controller.getAllCarts().size());
    }

    @Test
    public void testCreateCartWithNull() {
        Cart result = controller.createCart(null);

        assertNull(result);
        assertEquals(0, controller.getAllCarts().size());
    }

    @Test
    public void testGetAllCarts() {
        controller.createCart(cart1);
        controller.createCart(cart2);

        List<Cart> carts = controller.getAllCarts();

        assertEquals(2, carts.size());
        assertTrue(carts.contains(cart1));
        assertTrue(carts.contains(cart2));
    }

    @Test
    public void testGetAllCartsEmpty() {
        List<Cart> carts = controller.getAllCarts();

        assertNotNull(carts);
        assertEquals(0, carts.size());
    }

    @Test
    public void testGetCartByIdFound() {
        controller.createCart(cart1);
        controller.createCart(cart2);

        Optional<Cart> result = controller.getCartById(1);

        assertTrue(result.isPresent());
        assertEquals(cart1, result.get());
    }

    @Test
    public void testGetCartByIdNotFound() {
        controller.createCart(cart1);

        Optional<Cart> result = controller.getCartById(999);

        assertFalse(result.isPresent());
    }

    @Test
    public void testUpdateCartSuccess() {
        controller.createCart(cart1);
        Cart updatedCart = new Cart(1, "Cart A Updated", 200.0);

        boolean result = controller.updateCart(1, updatedCart);

        assertTrue(result);
        Optional<Cart> retrievedCart = controller.getCartById(1);
        assertTrue(retrievedCart.isPresent());
        assertEquals("Cart A Updated", retrievedCart.get().getName());
        assertEquals(Double.valueOf(200.0), retrievedCart.get().getMaxWeight());
    }

    @Test
    public void testUpdateCartNotFound() {
        controller.createCart(cart1);
        Cart updatedCart = new Cart(999, "Non-existent Cart", 200.0);

        boolean result = controller.updateCart(999, updatedCart);

        assertFalse(result);
    }

    @Test
    public void testUpdateCartWithNull() {
        controller.createCart(cart1);

        boolean result = controller.updateCart(1, null);

        assertFalse(result);
    }

    @Test
    public void testDeleteCartSuccess() {
        controller.createCart(cart1);
        controller.createCart(cart2);

        boolean result = controller.deleteCart(1);

        assertTrue(result);
        assertEquals(1, controller.getAllCarts().size());
        assertFalse(controller.getCartById(1).isPresent());
    }

    @Test
    public void testDeleteCartNotFound() {
        controller.createCart(cart1);

        boolean result = controller.deleteCart(999);

        assertFalse(result);
        assertEquals(1, controller.getAllCarts().size());
    }

    @Test
    public void testConstructor() {
        CartController newController = new CartController();

        assertNotNull(newController);
        assertNotNull(newController.getAllCarts());
    }
}
