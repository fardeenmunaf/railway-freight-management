package app.model;

import java.time.LocalDateTime;

/**
 * Represents a quality assurance inspection in the freight management system.
 * Each QA inspection tracks the quality control process for returned items,
 * including inspection status and timestamp information.
 * This class is essential for ensuring returned products meet quality standards before restocking.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class QaInspection {

    private Integer qaId;
    private Integer returnLineId;
    private String status;
    private LocalDateTime inspectedAt;

    /**
     * Default constructor that creates a QaInspection with null values.
     * Initializes all fields to null.
     */

    public QaInspection() {
        this.qaId = null;
        this.returnLineId = null;
        this.status = null;
        this.inspectedAt = null;
    }

    /**
     * Parameterized constructor that creates a QaInspection with specified values.
     *
     * @param qaId the unique identifier for this QA inspection
     * @param returnLineId the return line identifier being inspected
     * @param status the current status of the inspection
     * @param inspectedAt the timestamp when the inspection was performed
     */

    public QaInspection(Integer qaId, Integer returnLineId, String status, LocalDateTime inspectedAt) {
        this.qaId = qaId;
        this.returnLineId = returnLineId;
        this.status = status;
        this.inspectedAt = inspectedAt;
    }

    /**
     * Gets the unique identifier of this QA inspection.
     *
     * @return the QA identifier as an Integer
     */

    public Integer getQaId() {
        return qaId;
    }

    /**
     * Sets the unique identifier of this QA inspection.
     *
     * @param qaId the QA identifier to set
     */

    public void setQaId(Integer qaId) {
        this.qaId = qaId;
    }

    /**
     * Gets the return line identifier being inspected.
     *
     * @return the return line identifier as an Integer
     */

    public Integer getReturnLineId() {
        return returnLineId;
    }

    /**
     * Sets the return line identifier being inspected.
     *
     * @param returnLineId the return line identifier to set
     */

    public void setReturnLineId(Integer returnLineId) {
        this.returnLineId = returnLineId;
    }

    /**
     * Gets the current status of the inspection.
     *
     * @return the status as a String
     */

    public String getStatus() {
        return status;
    }

    /**
     * Sets the current status of the inspection.
     *
     * @param status the status to set
     */

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the timestamp when the inspection was performed.
     *
     * @return the inspected timestamp as a LocalDateTime
     */

    public LocalDateTime getInspectedAt() {
        return inspectedAt;
    }

    /**
     * Sets the timestamp when the inspection was performed.
     *
     * @param inspectedAt the inspected timestamp to set
     */

    public void setInspectedAt(LocalDateTime inspectedAt) {
        this.inspectedAt = inspectedAt;
    }
}
