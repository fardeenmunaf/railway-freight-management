package app.model;

/**
 * Represents a return line item in the freight management system.
 * Each return line defines a specific item within a return order,
 * including the SKU, quantity being returned, and the condition of the returned goods.
 * This class is used to break down return orders into individual item details.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class ReturnLine {

    private Integer returnLineId;
    private ReturnOrder returnOrder;
    private String sku;
    private Integer qty;
    private String condition;

    /**
     * Default constructor that creates a ReturnLine with null values.
     * Initializes all fields to null.
     */

    public ReturnLine() {
        this.returnLineId = null;
        this.returnOrder = null;
        this.sku = null;
        this.qty = null;
        this.condition = null;
    }

    /**
     * Parameterized constructor that creates a ReturnLine with specified values.
     *
     * @param returnLineId the unique identifier for this return line
     * @param returnOrder the return order this line belongs to
     * @param sku the stock keeping unit of the returned item
     * @param qty the quantity being returned
     * @param condition the condition of the returned item
     */

    public ReturnLine(Integer returnLineId, ReturnOrder returnOrder, String sku, Integer qty, String condition) {
        this.returnLineId = returnLineId;
        this.returnOrder = returnOrder;
        this.sku = sku;
        this.qty = qty;
        this.condition = condition;
    }

    /**
     * Gets the unique identifier of this return line.
     *
     * @return the return line identifier as an Integer
     */

    public Integer getReturnLineId() {
        return returnLineId;
    }

    /**
     * Sets the unique identifier of this return line.
     *
     * @param returnLineId the return line identifier to set
     */

    public void setReturnLineId(Integer returnLineId) {
        this.returnLineId = returnLineId;
    }

    /**
     * Gets the return order this line belongs to.
     *
     * @return the return order as a ReturnOrder object
     */

    public ReturnOrder getReturnOrder() {
        return returnOrder;
    }

    /**
     * Sets the return order this line belongs to.
     *
     * @param returnOrder the return order to set
     */

    public void setReturnOrder(ReturnOrder returnOrder) {
        this.returnOrder = returnOrder;
    }

    /**
     * Gets the stock keeping unit of the returned item.
     *
     * @return the SKU as a String
     */

    public String getSku() {
        return sku;
    }

    /**
     * Sets the stock keeping unit of the returned item.
     *
     * @param sku the SKU to set
     */

    public void setSku(String sku) {
        this.sku = sku;
    }

    /**
     * Gets the quantity being returned.
     *
     * @return the quantity as an Integer
     */

    public Integer getQty() {
        return qty;
    }

    /**
     * Sets the quantity being returned.
     *
     * @param qty the quantity to set
     */

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    /**
     * Gets the condition of the returned item.
     *
     * @return the condition as a String
     */

    public String getCondition() {
        return condition;
    }

    /**
     * Sets the condition of the returned item.
     *
     * @param condition the condition to set
     */

    public void setCondition(String condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        return "ReturnLine{" +
                "returnLineId=" + returnLineId +
                ", returnOrder=" + returnOrder +
                ", sku='" + sku + '\'' +
                ", qty=" + qty +
                ", condition='" + condition + '\'' +
                '}';
    }
}
