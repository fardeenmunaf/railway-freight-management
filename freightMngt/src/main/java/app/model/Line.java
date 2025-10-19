package app.model;

public class Line {

    private Integer lineId;
    private User owner;

    public Line() {
        this.lineId = null;
        this.owner = null;
    }

    public Line(Integer lineId, User owner) {
        this.lineId = lineId;
        this.owner = owner;
    }

    public Integer getLineId() {
        return lineId;
    }

    public void setLineId(Integer lineId) {
        this.lineId = lineId;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
