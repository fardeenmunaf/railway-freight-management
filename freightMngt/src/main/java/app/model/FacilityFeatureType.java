package app.model;

public class FacilityFeatureType {

    private Integer feature_id;
    private String name;

    public FacilityFeatureType() {
        this.feature_id = null;
        this.name = null;
    }

    public FacilityFeatureType(Integer feature_id, String name) {
        this.feature_id = feature_id;
        this.name = name;
    }

    public Integer getFeatureId() {
        return feature_id;
    }

    public void setFeatureId(Integer feature_id) {
        this.feature_id = feature_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
