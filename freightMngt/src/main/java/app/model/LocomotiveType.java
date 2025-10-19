package app.model;

/**
 * Represents a locomotive type in the freight management system.
 * Each locomotive type defines a category of locomotive based on its power source or technology,
 * such as diesel, electric, or steam locomotives.
 * This class is used to classify locomotives according to their operational characteristics.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class LocomotiveType {
    private Integer locomotiveTypeId;
    private String name;

    /**
     * Default constructor that creates a LocomotiveType with null values.
     * Initializes all fields to null.
     */

    public LocomotiveType() {
        locomotiveTypeId = null;
        name = null;
    }

    /**
     * Parameterized constructor that creates a LocomotiveType with specified values.
     *
     * @param locomotiveTypeId the unique identifier for this locomotive type
     * @param name the name of the locomotive type
     */

    public LocomotiveType(Integer locomotiveTypeId, String name) {
        this.locomotiveTypeId = locomotiveTypeId;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this locomotive type.
     *
     * @return the locomotive type identifier as an Integer
     */

    public Integer getLocomotiveTypeId() {
        return locomotiveTypeId;
    }

    /**
     * Sets the unique identifier of this locomotive type.
     *
     * @param locomotiveTypeId the locomotive type identifier to set
     */

    public void setLocomotiveTypeId(Integer locomotiveTypeId) {
        this.locomotiveTypeId = locomotiveTypeId;
    }

    /**
     * Gets the name of this locomotive type.
     *
     * @return the locomotive type name as a String
     */

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this locomotive type.
     *
     * @param name the locomotive type name to set
     */

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "LocomotiveType{" +
                "locomotiveTypeId=" + locomotiveTypeId +
                ", name='" + name + '\'' +
                '}';
    }
}
