package app.model;

import java.time.LocalDateTime;

public class QaInspection {

    private Integer qaId;
    private Integer returnLineId;
    private String status;
    private LocalDateTime inspectedAt;

    public QaInspection() {
        this.qaId = null;
        this.returnLineId = null;
        this.status = null;
        this.inspectedAt = null;
    }

    public QaInspection(Integer qaId, Integer returnLineId, String status, LocalDateTime inspectedAt) {
        this.qaId = qaId;
        this.returnLineId = returnLineId;
        this.status = status;
        this.inspectedAt = inspectedAt;
    }

    public Integer getQaId() {
        return qaId;
    }

    public void setQaId(Integer qaId) {
        this.qaId = qaId;
    }

    public Integer getReturnLineId() {
        return returnLineId;
    }

    public void setReturnLineId(Integer returnLineId) {
        this.returnLineId = returnLineId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getInspectedAt() {
        return inspectedAt;
    }

    public void setInspectedAt(LocalDateTime inspectedAt) {
        this.inspectedAt = inspectedAt;
    }
}
