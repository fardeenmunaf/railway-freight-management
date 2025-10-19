package app.model;

/**
 * Represents a cart in the freight management system.
 * <p>
 * Contains cart identification, naming, and weight capacity specifications
 * used for organizing and transporting items in the warehouse.
 * </p>
 */
public class Cart {
    private Integer cartId;
    private String name;
    private Double maxWeight;

    /**
     * Default constructor for Cart.
     * Initializes all fields to null.
     */
    public Cart() {
        this.cartId = null;
        this.name = null;
        this.maxWeight = null;
    }

    /**
     * Parameterized constructor for Cart.
     *
     * @param cartId   the unique identifier for the cart
     * @param name     the name of the cart
     * @param maxWeight the maximum weight capacity of the cart
     */
    public Cart(Integer cartId, String name, Double maxWeight) {
        this.cartId = cartId;
        this.name = name;
        this.maxWeight = maxWeight;
    }

    /**
     * Gets the cart identifier.
     *
     * @return the cart ID
     */
    public Integer getCartId() {
        return cartId;
    }

    /**
     * Sets the cart identifier.
     *
     * @param cartId the cart ID to set
     */
    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    /**
     * Gets the cart name.
     *
     * @return the cart name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the cart name.
     *
     * @param name the cart name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the maximum weight capacity of the cart.
     *
     * @return the maximum weight in kilograms
     */
    public Double getMaxWeight() {
        return maxWeight;
    }

    /**
     * Sets the maximum weight capacity of the cart.
     *
     * @param maxWeight the maximum weight to set in kilograms
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
