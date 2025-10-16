package app.model;

/**
 * Represents a railway wagon with its characteristics.
 * <p>
 * Contains information about the operator, wagon type, weights (tare and payload),
 * and physical dimensions.
 * </p>
 */
public class Wagon {
    private Integer wagonId;
    private User operator;
    private WagonType wagonType;
    private Double tareWeight;
    private Double payloadWeight;
    private Dimensions dimensions;

    public Wagon() {
    }

    public Wagon(Integer wagonId, User operator, WagonType wagonType, Double tareWeight, Double payloadWeight, Dimensions dimensions) {
        this.wagonId = wagonId;
        this.operator = operator;
        this.wagonType = wagonType;
        this.tareWeight = tareWeight;
        this.payloadWeight = payloadWeight;
        this.dimensions = dimensions;
    }

    public Integer getWagonId() {
        return wagonId;
    }

    public void setWagonId(Integer wagonId) {
        this.wagonId = wagonId;
    }

    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    public WagonType getWagonType() {
        return wagonType;
    }

    public void setWagonType(WagonType wagonType) {
        this.wagonType = wagonType;
    }

    public Double getTareWeight() {
        return tareWeight;
    }

    public void setTareWeight(Double tareWeight) {
        this.tareWeight = tareWeight;
    }

    public Double getPayloadWeight() {
        return payloadWeight;
    }

    public void setPayloadWeight(Double payloadWeight) {
        this.payloadWeight = payloadWeight;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public String toString() {
        return "Wagon{" +
                "wagonId=" + wagonId +
                ", operator=" + operator +
                ", wagonType=" + wagonType +
                ", tareWeight=" + tareWeight +
                ", payloadWeight=" + payloadWeight +
                ", dimensions=" + dimensions +
                '}';
    }
}
