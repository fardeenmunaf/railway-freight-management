package app.controller;

import app.model.Company;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller class for managing Company entities.
 * Provides CRUD operations (Create, Read, Update, Delete) for Company objects.
 * This controller manages company data including identification and naming information.
 *
 * @author G113 2DK
 * @version 1.0
 */
public class CompanyController {
    private List<Company> companies;

    /**
     * Default constructor that initializes the company list.
     */
    public CompanyController() {
        this.companies = new ArrayList<>();
    }

    /**
     * Creates and adds a new company to the system.
     *
     * @param company the Company object to be created
     * @return the created Company object
     */
    public Company createCompany(Company company) {
        if (company != null) {
            companies.add(company);
        }
        return company;
    }

    /**
     * Retrieves all companies from the system.
     *
     * @return a list of all Company objects
     */
    public List<Company> getAllCompanies() {
        return new ArrayList<>(companies);
    }

    /**
     * Retrieves a company by its unique identifier.
     *
     * @param companyId the unique identifier of the company
     * @return an Optional containing the Company if found, or empty if not found
     */
    public Optional<Company> getCompanyById(Integer companyId) {
        return companies.stream()
                .filter(company -> company.getCompanyId().equals(companyId))
                .findFirst();
    }

    /**
     * Updates an existing company in the system.
     *
     * @param companyId the unique identifier of the company to update
     * @param updatedCompany the Company object with updated information
     * @return true if the company was updated successfully, false otherwise
     */
    public boolean updateCompany(Integer companyId, Company updatedCompany) {
        Optional<Company> existingCompany = getCompanyById(companyId);
        if (existingCompany.isPresent()) {
            int index = companies.indexOf(existingCompany.get());
            companies.set(index, updatedCompany);
            return true;
        }
        return false;
    }

    /**
     * Deletes a company from the system.
     *
     * @param companyId the unique identifier of the company to delete
     * @return true if the company was deleted successfully, false otherwise
     */
    public boolean deleteCompany(Integer companyId) {
        return companies.removeIf(company -> company.getCompanyId().equals(companyId));
    }
}

