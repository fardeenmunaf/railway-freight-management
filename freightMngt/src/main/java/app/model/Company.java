package app.model;

public class Company {
    private Integer company_id;
    private String name;

    public Company() {
        company_id = null;
        name = null;
    }

    public Company(Integer company_id, String name) {
        this.company_id = company_id;
        this.name = name;
    }

    public Integer getCompany_id() {
        return company_id;
    }

    public void setCompany_id(Integer company_id) {
        this.company_id = company_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Company{" +
                "company_id=" + company_id +
                ", name='" + name + '\'' +
                '}';
    }
}
