package app.model;

/**
 * Represents a facility feature type in the freight management system.
 * Each feature type defines a specific characteristic or capability that a facility can have,
 * such as loading dock, crane, refrigeration, etc.
 * This class is used to categorize and identify available features at different facilities.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class FacilityFeatureType {

    private Integer featureId;
    private String name;

    /**
     * Default constructor that creates a FacilityFeatureType with null values.
     * Initializes all fields to null.
     */

    public FacilityFeatureType() {
        this.featureId = null;
        this.name = null;
    }

    /**
     * Parameterized constructor that creates a FacilityFeatureType with specified values.
     *
     * @param featureId the unique identifier for this feature type
     * @param name the name of the feature type
     */

    public FacilityFeatureType(Integer featureId, String name) {
        this.featureId = featureId;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this feature type.
     *
     * @return the feature identifier as an Integer
     */

    public Integer getFeatureId() {
        return featureId;
    }

    /**
     * Sets the unique identifier of this feature type.
     *
     * @param featureId the feature identifier to set
     */

    public void setFeatureId(Integer featureId) {
        this.featureId = featureId;
    }

    /**
     * Gets the name of this feature type.
     *
     * @return the feature type name as a String
     */

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this feature type.
     *
     * @param name the feature type name to set
     */

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "FacilityFeatureType{" +
                "featureId=" + featureId +
                ", name='" + name + '\'' +
                '}';
    }
}
