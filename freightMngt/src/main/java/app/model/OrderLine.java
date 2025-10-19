package app.model;

/**
 * Represents an order line item in the freight management system.
 * Each order line contains information about individual line items within an order,
 * including the associated order, item details, and quantity specifications.
 * This class is used to break down orders into specific item requirements.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class OrderLine {
    private Integer orderLineId;
    private Order order;
    private Item sku;
    private Double qty;

    /**
     * Default constructor that creates an OrderLine with null values.
     * Initializes all fields to null.
     */

    public OrderLine() {
        this.orderLineId = null;
        this.order = null;
        this.sku = null;
        this.qty = null;
    }

    /**
     * Parameterized constructor that creates an OrderLine with specified values.
     *
     * @param orderLineId the unique identifier for this order line
     * @param order the parent order this line belongs to
     * @param sku the item for this order line
     * @param qty the quantity of the item requested
     */

    public OrderLine(Integer orderLineId, Order order, Item sku, Double qty) {
        this.orderLineId = orderLineId;
        this.order = order;
        this.sku = sku;
        this.qty = qty;
    }

    /**
     * Gets the unique identifier of this order line.
     *
     * @return the order line identifier as an Integer
     */

    public Integer getOrderLineId() {
        return orderLineId;
    }

    /**
     * Sets the unique identifier of this order line.
     *
     * @param orderLineId the order line identifier to set
     */

    public void setOrderLineId(Integer orderLineId) {
        this.orderLineId = orderLineId;
    }

    /**
     * Gets the parent order this line belongs to.
     *
     * @return the order as an Order object
     */

    public Order getOrder() {
        return order;
    }

    /**
     * Sets the parent order this line belongs to.
     *
     * @param order the order to set
     */

    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Gets the item for this order line.
     *
     * @return the item as an Item object
     */

    public Item getSku() {
        return sku;
    }

    /**
     * Sets the item for this order line.
     *
     * @param sku the item to set
     */

    public void setSku(Item sku) {
        this.sku = sku;
    }

    /**
     * Gets the quantity requested.
     *
     * @return the quantity as a Double
     */

    public Double getQty() {
        return qty;
    }

    /**
     * Sets the quantity requested.
     *
     * @param qty the quantity to set
     */

    public void setQty(Double qty) {
        this.qty = qty;
    }

    @Override
    public String toString() {
        return "OrderLine{" +
                "orderLineId=" + orderLineId +
                ", order=" + order +
                ", sku=" + sku +
                ", qty=" + qty +
                '}';
    }
}
