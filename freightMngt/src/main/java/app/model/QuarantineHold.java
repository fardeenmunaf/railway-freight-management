package app.model;

import java.time.LocalDateTime;

public class QuarantineHold {

    private Integer qholdId;
    private ReturnOrder returnOrder;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime releasedAt;

    public QuarantineHold() {
        this.qholdId = null;
        this.returnOrder = null;
        this.status = null;
        this.createdAt = LocalDateTime.now();
        this.releasedAt = null;
    }

    public QuarantineHold(Integer qholdId, ReturnOrder returnOrder, String status, LocalDateTime createdAt, LocalDateTime releasedAt) {
        this.qholdId = qholdId;
        this.returnOrder = returnOrder;
        this.status = status;
        this.createdAt = createdAt;
        this.releasedAt = releasedAt;
    }

    public Integer getQholdId() {
        return qholdId;
    }

    public void setQholdId(Integer qholdId) {
        this.qholdId = qholdId;
    }

    public ReturnOrder getReturnOrder() {
        return returnOrder;
    }

    public void setReturnOrder(ReturnOrder returnOrder) {
        this.returnOrder = returnOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReleasedAt() {
        return releasedAt;
    }

    public void setReleasedAt(LocalDateTime releasedAt) {
        this.releasedAt = releasedAt;
    }
}
