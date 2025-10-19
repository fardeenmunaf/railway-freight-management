package app.model;

public class FacilityFeatureType {

    private Integer featureId;
    private String name;

    public FacilityFeatureType() {
        this.featureId = null;
        this.name = null;
    }

    public FacilityFeatureType(Integer featureId, String name) {
        this.featureId = featureId;
        this.name = name;
    }

    public Integer getFeatureId() {
        return featureId;
    }

    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
