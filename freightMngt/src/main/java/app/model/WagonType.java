package app.model;

/**
 * Represents a wagon type in the freight management system.
 * Each wagon type defines a category of railway wagon based on its design and purpose,
 * such as flatcar, boxcar, tank car, hopper car, or gondola.
 * This class is used to classify wagons according to their cargo handling characteristics.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class WagonType {
    private Integer wagonTypeId;
    private String name;

    /**
     * Default constructor that creates a WagonType with null values.
     * Initializes all fields to null.
     */

    public WagonType() {
        this.wagonTypeId = null;
        this.name = null;
    }

    /**
     * Parameterized constructor that creates a WagonType with specified values.
     *
     * @param wagonTypeId the unique identifier for this wagon type
     * @param name the name of the wagon type
     */

    public WagonType(Integer wagonTypeId, String name) {
        this.wagonTypeId = wagonTypeId;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this wagon type.
     *
     * @return the wagon type identifier as an Integer
     */

    public Integer getWagonTypeId() {
        return wagonTypeId;
    }

    /**
     * Sets the unique identifier of this wagon type.
     *
     * @param wagonTypeId the wagon type identifier to set
     */

    public void setWagonTypeId(Integer wagonTypeId) {
        this.wagonTypeId = wagonTypeId;
    }

    /**
     * Gets the name of this wagon type.
     *
     * @return the wagon type name as a String
     */

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this wagon type.
     *
     * @param name the wagon type name to set
     */

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "WagonType{" +
                "wagonTypeId=" + wagonTypeId +
                ", name='" + name + '\'' +
                '}';
    }
}
