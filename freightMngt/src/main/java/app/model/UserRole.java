package app.model;

public class UserRole {
    private User user;
    private UserRole user_role;

    public UserRole() {
    }

    public UserRole(User user, UserRole user_role) {
        this.user = user;
        this.user_role = user_role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserRole getUser_role() {
        return user_role;
    }

    public void setUser_role(UserRole user_role) {
        this.user_role = user_role;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "user=" + user +
                ", user_role=" + user_role +
                '}';
    }
}
