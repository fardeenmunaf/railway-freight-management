package app.model;

import java.time.LocalDateTime;

/**
 * Represents a quarantine hold in the freight management system.
 * Each quarantine hold restricts a return order from being processed or restocked
 * until quality issues are resolved or additional verification is completed.
 * This class tracks the quarantine status and timing information.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class QuarantineHold {

    private Integer qholdId;
    private ReturnOrder returnOrder;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime releasedAt;

    /**
     * Default constructor that creates a QuarantineHold with null/default values.
     * Initializes most fields to null and createdAt to current timestamp.
     */

    public QuarantineHold() {
        this.qholdId = null;
        this.returnOrder = null;
        this.status = null;
        this.createdAt = LocalDateTime.now();
        this.releasedAt = null;
    }

    /**
     * Parameterized constructor that creates a QuarantineHold with specified values.
     *
     * @param qholdId the unique identifier for this quarantine hold
     * @param returnOrder the return order being held in quarantine
     * @param status the current status of the quarantine hold
     * @param createdAt the timestamp when the hold was created
     * @param releasedAt the timestamp when the hold was released
     */

    public QuarantineHold(Integer qholdId, ReturnOrder returnOrder, String status, LocalDateTime createdAt, LocalDateTime releasedAt) {
        this.qholdId = qholdId;
        this.returnOrder = returnOrder;
        this.status = status;
        this.createdAt = createdAt;
        this.releasedAt = releasedAt;
    }

    /**
     * Gets the unique identifier of this quarantine hold.
     *
     * @return the quarantine hold identifier as an Integer
     */

    public Integer getQholdId() {
        return qholdId;
    }

    /**
     * Sets the unique identifier of this quarantine hold.
     *
     * @param qholdId the quarantine hold identifier to set
     */

    public void setQholdId(Integer qholdId) {
        this.qholdId = qholdId;
    }

    /**
     * Gets the return order being held in quarantine.
     *
     * @return the return order as a ReturnOrder object
     */

    public ReturnOrder getReturnOrder() {
        return returnOrder;
    }

    /**
     * Sets the return order being held in quarantine.
     *
     * @param returnOrder the return order to set
     */

    public void setReturnOrder(ReturnOrder returnOrder) {
        this.returnOrder = returnOrder;
    }

    /**
     * Gets the current status of the quarantine hold.
     *
     * @return the status as a String
     */

    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the quarantine hold.
     *
     * @param status the status to set
     */

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the timestamp when the hold was created.
     *
     * @return the creation timestamp as a LocalDateTime
     */

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when the hold was created.
     *
     * @param createdAt the creation timestamp to set
     */

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the timestamp when the hold was released.
     *
     * @return the release timestamp as a LocalDateTime
     */

    public LocalDateTime getReleasedAt() {
        return releasedAt;
    }

    /**
     * Sets the timestamp when the hold was released.
     *
     * @param releasedAt the release timestamp to set
     */

    public void setReleasedAt(LocalDateTime releasedAt) {
        this.releasedAt = releasedAt;
    }

    @Override
    public String toString() {
        return "QuarantineHold{" +
                "qholdId=" + qholdId +
                ", returnOrder=" + returnOrder +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", releasedAt=" + releasedAt +
                '}';
    }
}
