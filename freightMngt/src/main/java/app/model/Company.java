package app.model;

/**
 * Represents a company (operator) in the freight management system.
 * Each company entity stores basic information about transportation or logistics operators
 * that may own or operate facilities, trains, or other assets within the system.
 * This class maintains company identification and naming information.
 *
 * @author G113 2DK
 * @version 1.0
 */

public class Company {
    private Integer companyId;
    private String name;

    /**
     * Default constructor that creates a Company with null values.
     * Initializes all fields to null.
     */

    public Company() {
        companyId = null;
        name = null;
    }

    /**
     * Parameterized constructor that creates a Company with specified values.
     *
     * @param companyId the unique identifier for the company
     * @param name the name of the company
     */

    public Company(Integer companyId, String name) {
        this.companyId = companyId;
        this.name = name;
    }

    /**
     * Gets the unique identifier of this company.
     *
     * @return the company identifier as an Integer
     */

    public Integer getCompanyId() {
        return companyId;
    }

    /**
     * Sets the unique identifier of this company.
     *
     * @param companyId the company identifier to set
     */

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    /**
     * Gets the name of this company.
     *
     * @return the company name as a String
     */

    public String getName() {
        return name;
    }

    /**
     * Sets the name of this company.
     *
     * @param name the company name to set
     */

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyId=" + companyId +
                ", name='" + name + '\'' +
                '}';
    }
}
