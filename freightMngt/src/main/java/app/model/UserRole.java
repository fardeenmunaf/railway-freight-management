package app.model;

public class UserRole {
    private User user;
    private Role userRole;

    public UserRole() {
    }

    public UserRole(User user, Role userRole) {
        this.user = user;
        this.userRole = userRole;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getUserRole() {
        return userRole;
    }

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
