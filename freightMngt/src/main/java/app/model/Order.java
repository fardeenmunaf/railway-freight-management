package app.model;

import java.time.LocalDateTime;

/**
 * Represents an order in the freight management system.
 * <p>
 * Contains order information including identification, status tracking,
 * priority levels, and due date management for logistics operations.
 * </p>
 */
public class Order {
    private Integer orderId;
    private String status;
    private Integer priority;
    private LocalDateTime dueDate;

    /**
     * Default constructor for Order.
     * Initializes all fields to null.
     */
    public Order() {
        this.orderId = null;
        this.status = null;
        this.priority = null;
        this.dueDate = null;
    }

    /**
     * Parameterized constructor for Order.
     *
     * @param orderId  the unique identifier for the order
     * @param status   the current status of the order
     * @param priority the priority level of the order
     * @param dueDate  the due date for order completion
     */
    public Order(Integer orderId, String status, Integer priority, LocalDateTime dueDate) {
        this.orderId = orderId;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    /**
     * Gets the order identifier.
     *
     * @return the order ID
     */
    public Integer getOrderId() {
        return orderId;
    }

    /**
     * Sets the order identifier.
     *
     * @param orderId the order ID to set
     */
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    /**
     * Gets the order status.
     *
     * @return the order status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the order status.
     *
     * @param status the order status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the order priority.
     *
     * @return the order priority level
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * Sets the order priority.
     *
     * @param priority the order priority level to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * Gets the order due date.
     *
     * @return the due date for order completion
     */
    public LocalDateTime getDueDate() {
        return dueDate;
    }

    /**
     * Sets the order due date.
     *
     * @param dueDate the due date to set for order completion
     */
    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", status='" + status + '\'' +
                ", priority=" + priority +
                ", dueDate=" + dueDate +
                '}';
    }
}
