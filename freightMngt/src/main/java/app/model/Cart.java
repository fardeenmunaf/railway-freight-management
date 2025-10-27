package app.model;

/**
 * Represents a cart in the freight management system.
 * Each cart is used for organizing and transporting items within the warehouse,
 * with specific weight capacity limitations.
 * This class stores cart identification, naming, and weight capacity specifications.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Cart {
    private Integer cartId;
    private String name;
    private Double maxWeight;

    /**
     * Default constructor that creates a Cart with null values.
     * Initializes all fields to null.
     */

    public Cart() {
        this.cartId = null;
        this.name = null;
        this.maxWeight = null;
    }

    /**
     * Parameterized constructor that creates a Cart with specified values.
     *
     * @param cartId the unique identifier for the cart
     * @param name the name of the cart
     * @param maxWeight the maximum weight capacity of the cart in kilograms
     */

    public Cart(Integer cartId, String name, Double maxWeight) {
        this.cartId = cartId;
        this.name = name;
        this.maxWeight = maxWeight;
    }

    /**
     * Gets the unique identifier of this cart.
     *
     * @return the cart identifier as an Integer
     */

    public Integer getCartId() {
        return cartId;
    }

    /**
     * Sets the unique identifier of this cart.
     *
     * @param cartId the cart identifier to set
     */

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    /**
     * Gets the name of this cart.
     *
     * @return the cart name as a String
     */

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this cart.
     *
     * @param name the cart name to set
     */

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the maximum weight capacity of this cart.
     *
     * @return the maximum weight capacity as a Double in kilograms
     */

    public Double getMaxWeight() {
        return maxWeight;
    }

    /**
     * Sets the maximum weight capacity of this cart.
     *
     * @param maxWeight the maximum weight capacity to set in kilograms
     */

    public void setMaxWeight(Double maxWeight) {
        this.maxWeight = maxWeight;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", name='" + name + '\'' +
                ", maxWeight=" + maxWeight +
                '}';
    }
}
