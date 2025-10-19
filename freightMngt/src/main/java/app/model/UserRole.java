package app.model;

/**
 * Represents the association between a user and their role in the freight management system.
 * This class establishes the relationship between users and the roles they are assigned,
 * which is essential for access control and permission management.
 * A user may have multiple roles, and this class tracks those relationships.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class UserRole {
    private User user;
    private Role userRole;

    /**
     * Default constructor that creates a UserRole with null values.
     * Initializes all fields to null.
     */

    public UserRole() {
    }

    /**
     * Parameterized constructor that creates a UserRole with specified values.
     *
     * @param user the user in the relationship
     * @param userRole the role assigned to the user
     */

    public UserRole(User user, Role userRole) {
        this.user = user;
        this.userRole = userRole;
    }

    /**
     * Gets the user in this role association.
     *
     * @return the user as a User object
     */

    public User getUser() {
        return user;
    }

    /**
     * Sets the user in this role association.
     *
     * @param user the user to set
     */

    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the role assigned to the user.
     *
     * @return the role as a Role object
     */

    public Role getUserRole() {
        return userRole;
    }

    /**
     * Sets the role assigned to the user.
     *
     * @param userRole the role to set
     */

    public void setUserRole(Role userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "user=" + user +
                ", userRole=" + userRole +
                '}';
    }
}
