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
    private String shortName;
    private String vat;

    /**
     * Default constructor that creates a Company with null values.
     * Initializes all fields to null.
     */

    public Company() {
        companyId = null;
        name = null;
        shortName = null;
        vat = null;
    }

    /**
     * Parameterized constructor that creates a Company with specified values.
     *
     * @param companyId the unique identifier for the company
     * @param name the name of the company
     * @param shortName the short name of the company
     * @param vat the VAT number of the company
     */

    public Company(Integer companyId, String name, String shortName, String vat) {
        this.companyId = companyId;
        this.name = name;
        this.shortName = shortName;
        this.vat = vat;
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

    /**
     * Gets the short name of this company.
     *
     * @return the company short name as a String
     */

    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the short name of this company.
     *
     * @param shortName the company short name to set
     */

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the VAT number of this company.
     *
     * @return the company VAT number as a String
     */

    public String getVat() {
        return vat;
    }

    /**
     * Sets the VAT number of this company.
     *
     * @param vat the company VAT number to set
     */

    public void setVat(String vat) {
        this.vat = vat;
    }

    @Override
    public String toString() {
        return "Company{" +
                "companyId=" + companyId +
                ", name='" + name + '\'' +
                ", shortName='" + shortName + '\'' +
                ", vat='" + vat + '\'' +
                '}';
    }
}
