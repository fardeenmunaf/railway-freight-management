package app.model;

public class OrderLine {
    private Integer orderLineId;
    private Order order;
    private Item sku;
    private Double qty;

    public OrderLine() {
        this.orderLineId = null;
        this.order = null;
        this.sku = null;
        this.qty = null;
    }

    public OrderLine(Integer orderLineId, Order order, Item sku, Double qty) {
        this.orderLineId = orderLineId;
        this.order = order;
        this.sku = sku;
        this.qty = qty;
    }

    public Integer getOrderLineId() {
        return orderLineId;
    }

    public void setOrderLineId(Integer orderLineId) {
        this.orderLineId = orderLineId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Item getSku() {
        return sku;
    }

    public void setSku(Item sku) {
        this.sku = sku;
    }

    public Double getQty() {
        return qty;
    }

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
