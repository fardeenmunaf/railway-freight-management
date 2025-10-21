package app.repositories;

import app.model.Company;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing companies in the freight management system.
 * This class provides methods to add, retrieve, and manage company entities.
 * It maintains a collection of all companies and allows searching by company ID or name.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class CompanyRepository {

    private final List<Company> companies = new ArrayList<>();

    /**
     * Adds a new company to the repository if it doesn't already exist.
     *
     * @param company the company to add
     */
    public void addCompany(Company company) {
        if (company != null && company.getCompanyId() != null) {
            if (!containsCompany(company.getCompanyId())) {
                companies.add(company);
            }
        }
    }

    /**
     * Retrieves all companies in the repository.
     *
     * @return a new list containing all companies
     */
    public List<Company> getAllCompanies() {
        return new ArrayList<>(companies);
    }

    /**
     * Retrieves a specific company by its ID.
     *
     * @param companyId the company identifier
     * @return the matching company, or null if not found
     */
    public Company getCompanyById(Integer companyId) {
        for (Company company : companies) {
            if (companyId.equals(company.getCompanyId())) {
                return company;
            }
        }
        return null;
    }

    /**
     * Retrieves a specific company by its name.
     *
     * @param name the company name
     * @return the matching company, or null if not found
     */
    public Company getCompanyByName(String name) {
        for (Company company : companies) {
            if (name != null && name.equals(company.getName())) {
                return company;
            }
        }
        return null;
    }

    /**
     * Updates an existing company in the repository.
     *
     * @param companyId the company identifier
     * @param updatedCompany the updated company
     * @return true if the company was updated successfully, false otherwise
     */
    public boolean updateCompany(Integer companyId, Company updatedCompany) {
        Company existingCompany = getCompanyById(companyId);
        if (existingCompany != null) {
            int index = companies.indexOf(existingCompany);
            companies.set(index, updatedCompany);
            return true;
        }
        return false;
    }

    /**
     * Deletes a company from the repository.
     *
     * @param companyId the company identifier
     * @return true if the company was deleted successfully, false otherwise
     */
    public boolean deleteCompany(Integer companyId) {
        return companies.removeIf(company -> companyId.equals(company.getCompanyId()));
    }

    /**
     * Gets the next available company ID.
     * Finds the maximum company ID currently in use and returns the next sequential ID.
     *
     * @return the next available company ID (max current ID + 1)
     */
    public Integer getNextCompanyId() {
        int maxId = 0;
        for (Company company : companies) {
            if (company.getCompanyId() != null && company.getCompanyId() > maxId) {
                maxId = company.getCompanyId();
            }
        }
        return maxId + 1;
    }

    /**
     * Checks if a company exists in the repository by its ID.
     *
     * @param companyId the company identifier
     * @return true if the company exists, false otherwise
     */
    private boolean containsCompany(Integer companyId) {
        return getCompanyById(companyId) != null;
    }

    /**
     * Checks if a company with the given name exists in the repository.
     *
     * @param name the company name
     * @return true if a company with this name exists, false otherwise
     */
    public boolean containsName(String name) {
        return getCompanyByName(name) != null;
    }

    /**
     * Clears all companies from the repository.
     */
    public void clear() {
        companies.clear();
    }
}

