package app.model;

import java.time.LocalDateTime;

public class ReturnOrder {

    private Integer returnId;
    private Order origOrder;
    private String reason;
    private String status;
    private LocalDateTime receivedAt;

    public ReturnOrder() {
        this.returnId = null;
        this.origOrder = null;
        this.reason = null;
        this.status = null;
        this.receivedAt = null;
    }

    public ReturnOrder(Integer returnId, Order origOrder, String reason, String status, LocalDateTime receivedAt) {
        this.returnId = returnId;
        this.origOrder = origOrder;
        this.reason = reason;
        this.status = status;
        this.receivedAt = receivedAt;
    }

    public Integer getReturnId() {
        return returnId;
    }

    public void setReturnId(Integer returnId) {
        this.returnId = returnId;
    }

    public Order getOrigOrder() {
        return origOrder;
    }

    public void setOrigOrder(Order origOrder) {
        this.origOrder = origOrder;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(LocalDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}
