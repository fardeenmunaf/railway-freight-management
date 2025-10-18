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

    private Integer facility_type_id;
    private String name;

    /**
     * Default constructor that creates a FacilityType with null values.
     * Initializes both facility_type_id and name to null.
     */

    public FacilityType() {
        this.facility_type_id = null;
        this.name = null;
    }

    /**
     * Parameterized constructor that creates a FacilityType with specified values.
     *
     * @param facility_type_id the unique identifier for this facility type
     * @param name             the descriptive name of this facility type
     */

    public FacilityType(Integer facility_type_id, String name) {
        this.facility_type_id = facility_type_id;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this facility type.
     *
     * @return the facility type identifier as an Integer
     */

    public Integer getFacilityTypeId() {
        return facility_type_id;
    }

    /**
     * Sets the unique identifier of this facility type.
     *
     * @param facility_type_id the facility type identifier to set
     */

    public void setFacilityTypeId(Integer facility_type_id) {
        this.facility_type_id = facility_type_id;
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
