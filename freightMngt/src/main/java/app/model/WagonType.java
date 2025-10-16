package app.model;

/**
 * Represents a wagon type (e.g., flatcar, boxcar, tank car).
 * <p>
 * Contains the type identifier and descriptive name.
 * </p>
 */
public class WagonType {
    private Integer wagonTypeId;
    private String name;

    public WagonType() {
        this.wagonTypeId = null;
        this.name = null;
    }

    public WagonType(Integer wagonTypeId, String name) {
        this.wagonTypeId = wagonTypeId;
        this.name = name;
    }

    public Integer getWagonTypeId() {
        return wagonTypeId;
    }

    public void setWagonTypeId(Integer wagonTypeId) {
        this.wagonTypeId = wagonTypeId;
    }

    public String getName() {
        return name;
    }

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
