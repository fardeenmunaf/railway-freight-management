package app.model;

public class User {
    private Integer user_id;
    private String username;
    private String email;
    private String password;
    private Company company_id;
    private Boolean is_active;

    public User() {
        user_id = null;
        username = null;
        email = null;
        password = null;
        company_id = null;
        is_active = null;
    }

    public User(Integer user_id, String username, String email, String password, Company company_id, Boolean is_active) {
        this.user_id = user_id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.company_id = company_id;
        this.is_active = is_active;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
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

    public Company getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Company company_id) {
        this.company_id = company_id;
    }

    public Boolean getIs_active() {
        return is_active;
    }

    public void setIs_active(Boolean is_active) {
        this.is_active = is_active;
    }


    @Override
    public String toString() {
        return "User{" +
                "user_id=" + user_id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", company_id=" + company_id +
                ", is_active=" + is_active +
                '}';
    }
}
