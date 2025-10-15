package app.model;

public class Role {
    private Integer role_id;
    private String name;

    public Role() {
        role_id = null;
        name = null;
    }

    public Role(Integer role_id, String name) {
        this.role_id = role_id;
        this.name = name;
    }

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public String getRole_name() {
        return name;
    }

    public void setRole_name(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "role_id=" + role_id +
                ", name='" + name + '\'' +
                '}';
    }
}
