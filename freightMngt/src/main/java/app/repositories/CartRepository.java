package app.repositories;

import app.model.Cart;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing carts in the freight management system.
 * This class provides methods to add, retrieve, and manage cart entities.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class CartRepository {

    private final List<Cart> carts = new ArrayList<>();

    public void addCart(Cart cart) {
        if (cart != null && cart.getCartId() != null) {
            if (!containsCart(cart.getCartId())) {
                carts.add(cart);
            }
        }
    }

    public List<Cart> getAllCarts() {
        return new ArrayList<>(carts);
    }

    public Cart getCartById(Integer cartId) {
        for (Cart cart : carts) {
            if (cartId.equals(cart.getCartId())) {
                return cart;
            }
        }
        return null;
    }

    public boolean updateCart(Integer cartId, Cart updatedCart) {
        Cart existingCart = getCartById(cartId);
        if (existingCart != null) {
            int index = carts.indexOf(existingCart);
            carts.set(index, updatedCart);
            return true;
        }
        return false;
    }

    public boolean deleteCart(Integer cartId) {
        return carts.removeIf(cart -> cartId.equals(cart.getCartId()));
    }

    public Integer getNextCartId() {
        int maxId = 0;
        for (Cart cart : carts) {
            if (cart.getCartId() != null && cart.getCartId() > maxId) {
                maxId = cart.getCartId();
            }
        }
        return maxId + 1;
    }

    private boolean containsCart(Integer cartId) {
        return getCartById(cartId) != null;
    }

    public void clear() {
        carts.clear();
    }

    public void initializeDefaultCarts() {
        addCart(new Cart(1, "Carrinho Pequeno", 5.5));
        addCart(new Cart(2, "Carrinho Médio", 10.0));
        addCart(new Cart(3, "Carrinho Grande", 20.0));
        addCart(new Cart(4, "Carrinho Extra Grande", 35.0));
        addCart(new Cart(5, "Empilhadora", 50.0));
    }
}

