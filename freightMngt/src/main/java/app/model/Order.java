package app.model;

import java.time.LocalDateTime;

public class Order {
    private Integer orderId;
    private String status;
    private Integer priority;
    private LocalDateTime dueDate;

    public Order() {
        this.orderId = null;
        this.status = null;
        this.priority = null;
        this.dueDate = null;
    }

    public Order(Integer orderId, String status, Integer priority, LocalDateTime dueDate) {
        this.orderId = orderId;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

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
