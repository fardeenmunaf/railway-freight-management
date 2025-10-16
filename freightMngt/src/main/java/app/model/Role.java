package app.model;

/**
 * Represents a user role in the system.
 * <p>
 * Contains the role identifier and name (e.g., admin, operator, viewer).
 * </p>
 */
public class Role {
    private Integer roleId;
    private String name;

    public Role() {
        roleId = null;
        name = null;
    }

    public Role(Integer roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return name;
    }

    public void setRoleName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", name='" + name + '\'' +
                '}';
    }
}
