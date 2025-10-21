package app.controller;

import app.model.Company;
import app.repositories.CompanyRepository;
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
    private final CompanyRepository repository;

    /**
     * Default constructor that initializes the controller with a new repository.
     */
    public CompanyController() {
        this.repository = new CompanyRepository();
    }

    /**
     * Constructor that accepts a repository instance.
     *
     * @param repository the CompanyRepository to use
     */
    public CompanyController(CompanyRepository repository) {
        this.repository = repository;
    }

    /**
     * Creates and adds a new company to the system.
     *
     * @param company the Company object to be created
     * @return the created Company object
     */
    public Company createCompany(Company company) {
        if (company != null) {
            repository.addCompany(company);
        }
        return company;
    }

    /**
     * Retrieves all companies from the system.
     *
     * @return a list of all Company objects
     */
    public List<Company> getAllCompanies() {
        return repository.getAllCompanies();
    }

    /**
     * Retrieves a company by its unique identifier.
     *
     * @param companyId the unique identifier of the company
     * @return an Optional containing the Company if found, or empty if not found
     */
    public Optional<Company> getCompanyById(Integer companyId) {
        Company company = repository.getCompanyById(companyId);
        return Optional.ofNullable(company);
    }

    /**
     * Retrieves a company by its name.
     *
     * @param name the name of the company
     * @return an Optional containing the Company if found, or empty if not found
     */
    public Optional<Company> getCompanyByName(String name) {
        Company company = repository.getCompanyByName(name);
        return Optional.ofNullable(company);
    }

    /**
     * Updates an existing company in the system.
     *
     * @param companyId the unique identifier of the company to update
     * @param updatedCompany the Company object with updated information
     * @return true if the company was updated successfully, false otherwise
     */
    public boolean updateCompany(Integer companyId, Company updatedCompany) {
        return repository.updateCompany(companyId, updatedCompany);
    }

    /**
     * Deletes a company from the system.
     *
     * @param companyId the unique identifier of the company to delete
     * @return true if the company was deleted successfully, false otherwise
     */
    public boolean deleteCompany(Integer companyId) {
        return repository.deleteCompany(companyId);
    }

    /**
     * Gets the next available company ID.
     *
     * @return the next available company ID
     */
    public Integer getNextCompanyId() {
        return repository.getNextCompanyId();
    }

    /**
     * Checks if a company with the given name exists.
     *
     * @param name the company name
     * @return true if a company with this name exists, false otherwise
     */
    public boolean containsName(String name) {
        return repository.containsName(name);
    }
}
