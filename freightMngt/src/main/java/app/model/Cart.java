package app.model;

public class Cart {
    private Integer cartId;
    private String name;
    private Double maxWeight;

    public Cart() {
        this.cartId = null;
        this.name = null;
        this.maxWeight = null;
    }

    public Cart(Integer cartId, String name, Double maxWeight) {
        this.cartId = cartId;
        this.name = name;
        this.maxWeight = maxWeight;
    }

    public Integer getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMaxWeight() {
        return maxWeight;
    }

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
