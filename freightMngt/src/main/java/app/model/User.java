package app.model;

/**
 * Represents a user in the system.
 * <p>
 * Contains user credentials, associated company and active status.
 * </p>
 */
public class User {
    private Integer userId;
    private String username;
    private String email;
    private String password;
    private Company companyId;
    private Boolean isActive;

    public User() {
        userId = null;
        username = null;
        email = null;
        password = null;
        companyId = null;
        isActive = null;
    }

    public User(Integer userId, String username, String email, String password, Company companyId, Boolean isActive) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.companyId = companyId;
        this.isActive = isActive;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Company getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Company companyId) {
        this.companyId = companyId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

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
