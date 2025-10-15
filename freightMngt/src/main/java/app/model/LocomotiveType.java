package app.model;

public class LocomotiveType {
    private Integer locomotiveTypeId;
    private String name;

    public LocomotiveType() {
        locomotiveTypeId = null;
        name = null;
    }

    public LocomotiveType(Integer locomotiveTypeId, String name) {
        this.locomotiveTypeId = locomotiveTypeId;
        this.name = name;
    }

    public Integer getLocomotiveTypeId() {
        return locomotiveTypeId;
    }

    public void setLocomotiveTypeId(Integer locomotiveTypeId) {
        this.locomotiveTypeId = locomotiveTypeId;
    }

    public String getName() {
        return name;
    }

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
