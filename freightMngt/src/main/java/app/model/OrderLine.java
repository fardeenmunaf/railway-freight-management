package app.model;

/**
 * Represents an order line item in the freight management system.
 * <p>
 * Contains information about individual line items within an order,
 * including the associated order, item details, and quantity specifications.
 * Used to break down orders into specific item requirements.
 * </p>
 */
public class OrderLine {
    private Integer orderLineId;
    private Order order;
    private Item sku;
    private Double qty;

    /**
     * Default constructor for OrderLine.
     * Initializes all fields to null.
     */
    public OrderLine() {
        this.orderLineId = null;
        this.order = null;
        this.sku = null;
        this.qty = null;
    }

    /**
     * Parameterized constructor for OrderLine.
     *
     * @param orderLineId the unique identifier for the order line
     * @param order        the parent order this line belongs to
     * @param sku          the item (SKU) for this order line
     * @param qty          the quantity of the item requested
     */
    public OrderLine(Integer orderLineId, Order order, Item sku, Double qty) {
        this.orderLineId = orderLineId;
        this.order = order;
        this.sku = sku;
        this.qty = qty;
    }

    /**
     * Gets the order line identifier.
     *
     * @return the order line ID
     */
    public Integer getOrderLineId() {
        return orderLineId;
    }

    /**
     * Sets the order line identifier.
     *
     * @param orderLineId the order line ID to set
     */
    public void setOrderLineId(Integer orderLineId) {
        this.orderLineId = orderLineId;
    }

    /**
     * Gets the parent order.
     *
     * @return the order this line belongs to
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Sets the parent order.
     *
     * @param order the order to set for this line
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Gets the item (SKU) for this order line.
     *
     * @return the item
     */
    public Item getSku() {
        return sku;
    }

    /**
     * Sets the item (SKU) for this order line.
     *
     * @param sku the item to set
     */
    public void setSku(Item sku) {
        this.sku = sku;
    }

    /**
     * Gets the quantity requested.
     *
     * @return the quantity
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
