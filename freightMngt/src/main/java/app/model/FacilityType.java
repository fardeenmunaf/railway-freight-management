package app.model;

/**
 * Represents a type of facility in the freight management system.
 * This class defines the categories or classifications that facilities can belong to,
 * such as warehouse, station, terminal, or depot. Each facility type has a unique
 * identifier and descriptive name.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class FacilityType {

    private Integer facilityTypeId;
    private String name;

    /**
     * Default constructor that creates a FacilityType with null values.
     * Initializes both facilityTypeId and name to null.
     */

    public FacilityType() {
        this.facilityTypeId = null;
        this.name = null;
    }

    /**
     * Parameterized constructor that creates a FacilityType with specified values.
     *
     * @param facilityTypeId the unique identifier for this facility type
     * @param name             the descriptive name of this facility type
     */

    public FacilityType(Integer facilityTypeId, String name) {
        this.facilityTypeId = facilityTypeId;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this facility type.
     *
     * @return the facility type identifier as an Integer
     */

    public Integer getFacilityTypeId() {
        return facilityTypeId;
    }

    /**
     * Sets the unique identifier of this facility type.
     *
     * @param facilityTypeId the facility type identifier to set
     */

    public void setFacilityTypeId(Integer facilityTypeId) {
        this.facilityTypeId = facilityTypeId;
    }

    /**
     * Gets the name of this facility type.
     *
     * @return the facility type name as a String
     */

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this facility type.
     *
     * @param name the facility type name to set
     */

    public void setName(String name) {
        this.name = name;
    }
}
