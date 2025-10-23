package app.controller;

import app.model.Cart;
import app.repositories.CartRepository;
import app.repositories.Repositories;

import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Cart entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Cart objects.
 * This controller manages cart data including identification, naming, and weight specifications.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class CartController {
    private final CartRepository cartRepository;

    /**
     * Default constructor that initializes the cart repository.
     */
    public CartController() {
        this.cartRepository = Repositories.getInstance().getCartRepository();
    }

    /**
     * Creates and adds a new cart to the system.
     *
     * @param cart the Cart object to be created
     * @return the created Cart object
     */
    public Cart createCart(Cart cart) {
        if (cart != null) {
            cartRepository.addCart(cart);
        }
        return cart;
    }

    /**
     * Retrieves all carts from the system.
     *
     * @return a list of all Cart objects
     */
    public List<Cart> getAllCarts() {
        return cartRepository.getAllCarts();
    }

    /**
     * Retrieves a cart by its unique identifier.
     *
     * @param cartId the unique identifier of the cart
     * @return an Optional containing the Cart if found, or empty if not found
     */
    public Optional<Cart> getCartById(Integer cartId) {
        Cart cart = cartRepository.getCartById(cartId);
        return Optional.ofNullable(cart);
    }

    /**
     * Updates an existing cart in the system.
     *
     * @param cartId the unique identifier of the cart to update
     * @param updatedCart the Cart object with updated information
     * @return true if the cart was updated successfully, false otherwise
     */
    public boolean updateCart(Integer cartId, Cart updatedCart) {
        return cartRepository.updateCart(cartId, updatedCart);
    }

    /**
     * Deletes a cart from the system.
     *
     * @param cartId the unique identifier of the cart to delete
     * @return true if the cart was deleted successfully, false otherwise
     */
    public boolean deleteCart(Integer cartId) {
        return cartRepository.deleteCart(cartId);
    }
}
