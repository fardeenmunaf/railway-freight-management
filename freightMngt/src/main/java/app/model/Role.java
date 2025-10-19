package app.model;

/**
 * Represents a user role in the freight management system.
 * Each role defines a set of permissions and access levels that can be assigned to users,
 * such as admin, operator, viewer, or manager.
 * This class is used for role-based access control within the system.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Role {
    private Integer roleId;
    private String name;

    /**
     * Default constructor that creates a Role with null values.
     * Initializes all fields to null.
     */

    public Role() {
        roleId = null;
        name = null;
    }

    /**
     * Parameterized constructor that creates a Role with specified values.
     *
     * @param roleId the unique identifier for this role
     * @param name the name of the role
     */

    public Role(Integer roleId, String name) {
        this.roleId = roleId;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this role.
     *
     * @return the role identifier as an Integer
     */

    public Integer getRoleId() {
        return roleId;
    }

    /**
     * Sets the unique identifier of this role.
     *
     * @param roleId the role identifier to set
     */

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * Gets the name of this role.
     *
     * @return the role name as a String
     */

    public String getRoleName() {
        return name;
    }

    /**
     * Sets the name of this role.
     *
     * @param name the role name to set
     */

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
