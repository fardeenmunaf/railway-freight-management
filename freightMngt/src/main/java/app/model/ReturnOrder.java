package app.model;

import java.time.LocalDateTime;

/**
 * Represents a return order in the freight management system.
 * Each return order tracks products being returned from customers or facilities,
 * including the reason for return, current status, and timestamp information.
 * This class is essential for managing reverse logistics operations.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class ReturnOrder {

    private Integer returnId;
    private Order origOrder;
    private String reason;
    private String status;
    private LocalDateTime receivedAt;

    /**
     * Default constructor that creates a ReturnOrder with null values.
     * Initializes all fields to null.
     */

    public ReturnOrder() {
        this.returnId = null;
        this.origOrder = null;
        this.reason = null;
        this.status = null;
        this.receivedAt = null;
    }

    /**
     * Parameterized constructor that creates a ReturnOrder with specified values.
     *
     * @param returnId the unique identifier for this return order
     * @param origOrder the original order that is being returned
     * @param reason the reason for the return
     * @param status the current status of the return order
     * @param receivedAt the timestamp when the return was received
     */

    public ReturnOrder(Integer returnId, Order origOrder, String reason, String status, LocalDateTime receivedAt) {
        this.returnId = returnId;
        this.origOrder = origOrder;
        this.reason = reason;
        this.status = status;
        this.receivedAt = receivedAt;
    }

    /**
     * Gets the unique identifier of this return order.
     *
     * @return the return identifier as an Integer
     */

    public Integer getReturnId() {
        return returnId;
    }

    /**
     * Sets the unique identifier of this return order.
     *
     * @param returnId the return identifier to set
     */

    public void setReturnId(Integer returnId) {
        this.returnId = returnId;
    }

    /**
     * Gets the original order that is being returned.
     *
     * @return the original order as an Order object
     */

    public Order getOrigOrder() {
        return origOrder;
    }

    /**
     * Sets the original order that is being returned.
     *
     * @param origOrder the original order to set
     */

    public void setOrigOrder(Order origOrder) {
        this.origOrder = origOrder;
    }

    /**
     * Gets the reason for the return.
     *
     * @return the reason as a String
     */

    public String getReason() {
        return reason;
    }

    /**
     * Sets the reason for the return.
     *
     * @param reason the reason to set
     */

    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Gets the current status of the return order.
     *
     * @return the status as a String
     */

    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the return order.
     *
     * @param status the status to set
     */

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the timestamp when the return was received.
     *
     * @return the received timestamp as a LocalDateTime
     */

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    /**
     * Sets the timestamp when the return was received.
     *
     * @param receivedAt the received timestamp to set
     */

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }

    @Override
    public String toString() {
        return "ReturnOrder{" +
                "returnId=" + returnId +
                ", origOrder=" + origOrder +
                ", reason='" + reason + '\'' +
                ", status='" + status + '\'' +
                ", receivedAt=" + receivedAt +
                '}';
    }
}
