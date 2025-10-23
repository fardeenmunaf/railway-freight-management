package app.model;

public class WagonModel {
    private Integer WagonModelId;
    private String name;
    private WagonType wagonType;
    private String constructor;
    private String bogies;
    private Integer numberBogies;
    private Double tareWeight;
    private Double payloadWeight;
    private Double maxSpeed;
    private Dimensions dimensions;

    public WagonModel() {
        WagonModelId = null;
        name = null;
        wagonType = null;
        constructor = null;
        bogies = null;
        numberBogies = null;
        tareWeight = null;
        payloadWeight = null;
        maxSpeed = null;
        dimensions = null;
    }

    public WagonModel(Integer wagonModelId, String name, WagonType wagonType, String constructor, String bogies, Integer numberBogies, Double tareWeight, Double payloadWeight, Double maxSpeed, Dimensions dimensions) {
        WagonModelId = wagonModelId;
        this.name = name;
        this.wagonType = wagonType;
        this.constructor = constructor;
        this.bogies = bogies;
        this.numberBogies = numberBogies;
        this.tareWeight = tareWeight;
        this.payloadWeight = payloadWeight;
        this.maxSpeed = maxSpeed;
        this.dimensions = dimensions;
    }

    public Integer getWagonModelId() {
        return WagonModelId;
    }

    public void setWagonModelId(Integer wagonModelId) {
        WagonModelId = wagonModelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WagonType getWagonType() {
        return wagonType;
    }

    public void setWagonType(WagonType wagonType) {
        this.wagonType = wagonType;
    }

    public String getConstructor() {
        return constructor;
    }

    public void setConstructor(String constructor) {
        this.constructor = constructor;
    }

    public String getBogies() {
        return bogies;
    }

    public void setBogies(String bogies) {
        this.bogies = bogies;
    }

    public Integer getNumberBogies() {
        return numberBogies;
    }

    public void setNumberBogies(Integer numberBogies) {
        this.numberBogies = numberBogies;
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

    public Double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(Double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public Dimensions getDimensions() {
        return dimensions;
    }

    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }

    @Override
    public String toString() {
        return "WagonModel{" +
                "WagonModelId=" + WagonModelId +
                ", name='" + name + '\'' +
                ", wagonType=" + wagonType +
                ", constructor='" + constructor + '\'' +
                ", bogies='" + bogies + '\'' +
                ", numberBogies=" + numberBogies +
                ", tareWeight=" + tareWeight +
                ", payloadWeight=" + payloadWeight +
                ", maxSpeed=" + maxSpeed +
                ", dimensions=" + dimensions +
                '}';
    }
}
