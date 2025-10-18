package app.model;

/**
 * Represents a user in the freight management system.
 * This class defines system users with their credentials, associated company,
 * and active status. Users can be assigned different roles within the system
 * and are associated with specific companies for access control.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class User {
    private Integer userId;
    private String username;
    private String email;
    private String password;
    private Company companyId;
    private Boolean isActive;

    /**
     * Default constructor that creates a User with null/default values.
     * Initializes all fields to null.
     */

    public User() {
        userId = null;
        username = null;
        email = null;
        password = null;
        companyId = null;
        isActive = null;
    }

    /**
     * Parameterized constructor that creates a User with specified values.
     *
     * @param userId the unique identifier for this user
     * @param username the username for authentication
     * @param email the user's email address
     * @param password the user's password
     * @param companyId the company this user belongs to
     * @param isActive whether the user account is active
     */

    public User(Integer userId, String username, String email, String password, Company companyId, Boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.companyId = companyId;
        this.isActive = isActive;
    }

    /**
     * Gets the unique identifier of this user.
     *
     * @return the user identifier as an Integer
     */

    public Integer getUserId() {
        return userId;
    }

    /**
     * Sets the unique identifier of this user.
     *
     * @param userId the user identifier to set
     */

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * Gets the username of this user.
     *
     * @return the username as a String
     */

    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of this user.
     *
     * @param username the username to set
     */

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the email address of this user.
     *
     * @return the email address as a String
     */

    public String getEmail() {
        return email;
    }

    /**
     * Sets the email address of this user.
     *
     * @param email the email address to set
     */

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the password of this user.
     *
     * @return the password as a String
     */

    public String getPassword() {
        return password;
    }

    /**
     * Sets the password of this user.
     *
     * @param password the password to set
     */

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the company this user belongs to.
     *
     * @return the company as a Company object
     */

    public Company getCompanyId() {
        return companyId;
    }

    /**
     * Sets the company this user belongs to.
     *
     * @param companyId the company to set
     */

    public void setCompanyId(Company companyId) {
        this.companyId = companyId;
    }

    /**
     * Gets whether this user account is active.
     *
     * @return true if the user is active, false otherwise
     */

    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * Sets whether this user account is active.
     *
     * @param isActive true to activate the user, false to deactivate
     */

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", companyId=" + companyId +
                ", isActive=" + isActive +
                '}';
    }
}
